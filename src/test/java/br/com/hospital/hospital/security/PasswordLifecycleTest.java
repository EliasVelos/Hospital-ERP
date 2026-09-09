package br.com.hospital.hospital.security;

import br.com.hospital.hospital.DTO.*;
import br.com.hospital.hospital.config.DataInitializer;
import br.com.hospital.hospital.config.LegacyPasswordMigration;
import br.com.hospital.hospital.entity.Usuario;
import br.com.hospital.hospital.repository.*;
import br.com.hospital.hospital.service.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PasswordLifecycleTest {
    private final UsuarioRepository users = mock(UsuarioRepository.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);
    private final UsuarioService service = new UsuarioService(users, encoder);

    @Test
    void validatesPasswordBeforeSaving() {
        Usuario form = new Usuario(); form.setUsername("test"); form.setRole("ADMIN"); form.setPassword("123");
        assertThatThrownBy(() -> service.save(form)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.encodePassword("á".repeat(37))).isInstanceOf(IllegalArgumentException.class);
        verify(users, never()).save(any());
    }

    @Test
    void bootstrapHasNoDefaultPasswordAndNeverOverwritesAccounts() {
        assertThatThrownBy(() -> new DataInitializer(users, service, "admin", "").run()).isInstanceOf(IllegalArgumentException.class);
        when(users.count()).thenReturn(1L);
        new DataInitializer(users, service, "admin", "different-password").run();
        verify(users, never()).save(any());
    }

    @Test
    void legacyMigrationHashesPlaintextOnceAndPreservesExistingBcrypt() {
        Usuario legacy = new Usuario(); legacy.setPassword("senha-antiga");
        Usuario modern = new Usuario(); String hash = encoder.encode("senha-moderna"); modern.setPassword(hash);
        when(users.findAll()).thenReturn(List.of(legacy, modern));
        LegacyPasswordMigration migration = new LegacyPasswordMigration(users, encoder);
        migration.run();
        assertThat(encoder.matches("senha-antiga", legacy.getPassword())).isTrue();
        assertThat(modern.getPassword()).isEqualTo(hash);
        String migrated = legacy.getPassword(); migration.run();
        assertThat(legacy.getPassword()).isEqualTo(migrated);
    }

    @Test
    void everyEnrollmentHashesPasswords() {
        when(users.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        PacienteService patientService = new PacienteService();
        ReflectionTestUtils.setField(patientService, "pacienteRepository", mock(PacienteRepository.class));
        ReflectionTestUtils.setField(patientService, "usuarioRepository", users);
        ReflectionTestUtils.setField(patientService, "usuarioService", service);
        PacienteCadastroDTO patient = new PacienteCadastroDTO(); patient.setUsername("patient"); patient.setPassword("Senha-paciente-2026");
        patientService.cadastrarNovoPaciente(patient);
        FuncionarioService staffService = new FuncionarioService();
        ReflectionTestUtils.setField(staffService, "funcionarioRepository", mock(FuncionarioRepository.class));
        ReflectionTestUtils.setField(staffService, "usuarioRepository", users);
        ReflectionTestUtils.setField(staffService, "usuarioService", service);
        FuncionarioCadastroDTO staff = new FuncionarioCadastroDTO(); staff.setUsername("staff"); staff.setPassword("Senha-funcionario-2026");
        staffService.cadastrarNovoFuncionario(staff);
        var captor = org.mockito.ArgumentCaptor.forClass(Usuario.class);
        verify(users, times(2)).save(captor.capture());
        assertThat(encoder.matches(patient.getPassword(), captor.getAllValues().get(0).getPassword())).isTrue();
        assertThat(encoder.matches(staff.getPassword(), captor.getAllValues().get(1).getPassword())).isTrue();
        MedicoService doctorService = new MedicoService(); MedicoRepository doctors = mock(MedicoRepository.class);
        ReflectionTestUtils.setField(doctorService, "medicoRepository", doctors);
        ReflectionTestUtils.setField(doctorService, "usuarioService", service);
        MedicoCadastroDTO doctor = new MedicoCadastroDTO(); doctor.setUsername("doctor"); doctor.setPassword("Senha-medico-2026");
        doctorService.cadastrarNovoMedico(doctor);
        var doctorCaptor = org.mockito.ArgumentCaptor.forClass(br.com.hospital.hospital.entity.Medico.class);
        verify(doctors).save(doctorCaptor.capture());
        assertThat(encoder.matches(doctor.getPassword(), doctorCaptor.getValue().getUsuario().getPassword())).isTrue();
    }
}
