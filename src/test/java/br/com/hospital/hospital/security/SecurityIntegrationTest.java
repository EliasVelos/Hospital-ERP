package br.com.hospital.hospital.security;

import br.com.hospital.hospital.entity.*;
import br.com.hospital.hospital.repository.*;
import br.com.hospital.hospital.service.UsuarioService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityIntegrationTest {
    private static final String PASSWORD = "Senha-de-teste-2026";
    @Autowired MockMvc mvc;
    @Autowired UsuarioRepository users;
    @Autowired UsuarioService userService;
    @Autowired PacienteRepository patients;
    @Autowired MedicoRepository doctors;
    @Autowired ConsultaRepository consultations;
    @Autowired AtendimentoRepository attendances;
    @Autowired PasswordEncoder encoder;
    private HospitalPrincipal admin;
    private HospitalPrincipal doctor;
    private HospitalPrincipal patient;
    private Medico medico;
    private Paciente paciente;

    @BeforeEach
    void setup() {
        Usuario adminAccount = account("admin-test", "ADMIN");
        admin = principal(adminAccount, adminAccount.getId());
        Usuario doctorAccount = account("doctor-test", "MEDICO");
        medico = new Medico();
        medico.setNomeMedico("Médico de teste");
        medico.setCpfMedico("00000000000");
        medico.setCrmMedico(123456);
        medico.setEspecialidadeMedico("Clínica");
        medico.setTelefoneMedico("17900000000");
        medico.setEnderecoMedico("Endereço de teste");
        medico.setUsuario(doctorAccount);
        medico = doctors.saveAndFlush(medico);
        doctorAccount.setMedico(medico);
        doctor = principal(doctorAccount, medico.getIdMedico());
        Usuario patientAccount = account("patient-test", "PACIENTE");
        paciente = new Paciente();
        paciente.setNomePaciente("Paciente de teste");
        paciente.setCpfPaciente("11111111111");
        paciente.setEnderecoPaciente("Endereço de teste");
        paciente.setTipoSanguinioPaciente("O+");
        paciente.setSexoPaciente("F");
        paciente.setTelefonePaciente("17900000001");
        paciente.setNascPaciente(LocalDate.of(2000, 1, 1));
        paciente.setUsuario(patientAccount);
        paciente = patients.saveAndFlush(paciente);
        patientAccount.setPaciente(paciente);
        patient = principal(patientAccount, paciente.getIdPaciente());
    }

    private Usuario account(String username, String role) {
        Usuario form = new Usuario();
        form.setUsername(username); form.setPassword(PASSWORD); form.setRole(role);
        return userService.save(form);
    }

    private HospitalPrincipal principal(Usuario account, Integer entityId) {
        return new HospitalPrincipal(account.getUsername(), account.getPassword(), account.getRole(), entityId);
    }

    private Consulta consultation(Medico assigned) {
        Consulta c = new Consulta(); c.setPaciente(paciente); c.setMedico(assigned);
        c.setDataehoraConsulta(LocalDateTime.now().plusHours(1)); c.setObservacoesConsulta("Consulta de teste");
        return consultations.saveAndFlush(c);
    }

    @Test
    void loginIncludesCsrfAndIgnoresSubmittedRole() throws Exception {
        String html = mvc.perform(get("/login")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(Jsoup.parse(html).select("form input[name=_csrf]")).hasSize(1);
        mvc.perform(post("/login").with(csrf()).param("username", "patient-test").param("password", PASSWORD)
                .param("tipoUsuario", "ADMIN"))
            .andExpect(authenticated().withRoles("PACIENTE")).andExpect(redirectedUrl("/"));
    }

    @Test
    void incorrectAndLegacyPlaintextPasswordsDoNotAuthenticate() throws Exception {
        mvc.perform(post("/login").with(csrf()).param("username", "admin-test").param("password", "incorreta"))
            .andExpect(unauthenticated()).andExpect(redirectedUrl("/login?error"));
        Usuario legacy = users.findByUsername("admin-test"); legacy.setPassword(PASSWORD); users.saveAndFlush(legacy);
        mvc.perform(post("/login").with(csrf()).param("username", "admin-test").param("password", PASSWORD))
            .andExpect(unauthenticated()).andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void unauthenticatedRequestsCannotReadPatients() throws Exception {
        mvc.perform(get("/pacientes/listar")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrlPattern("**/login"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/admin/usuarios/listar", "/medicos/listar", "/funcionarios/listar", "/relatorios/listar", "/pacientes/listar", "/consultas/listar"})
    void patientCannotReadManagementScreens(String path) throws Exception {
        mvc.perform(get(path).with(user(patient))).andExpect(status().isForbidden());
    }

    @Test
    void staffCannotCreatePrivilegedAccounts() throws Exception {
        mvc.perform(post("/medicos/cadastrar").with(user("staff").roles("FUNCIONARIO")).with(csrf()))
            .andExpect(status().isForbidden());
        mvc.perform(post("/admin/usuarios/salvar").with(user("staff").roles("FUNCIONARIO")).with(csrf()).param("role", "ADMIN"))
            .andExpect(status().isForbidden());
    }

    @Test
    void patientScreensUseAuthenticatedIdentity() throws Exception {
        mvc.perform(get("/pacientes/meusdados").with(user(patient)).param("pacienteId", "99999"))
            .andExpect(status().isOk()).andExpect(model().attribute("paciente", paciente));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/pacientes/excluir/999", "/medicos/excluir/999", "/funcionarios/excluir/999", "/consultas/excluir/999", "/internacoes/excluir/999", "/internacoes/darAlta/999", "/leitos/excluir/999", "/medicamentos/excluir/999", "/relatorios/excluir/999", "/admin/usuarios/excluir/999"})
    void destructiveActionsRejectGetAndMissingCsrf(String path) throws Exception {
        mvc.perform(get(path).with(user(admin))).andExpect(status().isMethodNotAllowed());
        mvc.perform(post(path).with(user(admin))).andExpect(status().isForbidden());
    }

    @Test
    void adminCanDeleteWithCsrf() throws Exception {
        Usuario removable = account("removable", "ADMIN");
        mvc.perform(post("/admin/usuarios/excluir/" + removable.getId()).with(user(admin)).with(csrf()))
            .andExpect(status().is3xxRedirection());
        assertThat(users.findById(removable.getId())).isEmpty();
    }

    @Test
    void logoutRequiresCsrfAndInvalidatesSession() throws Exception {
        MockHttpSession session = (MockHttpSession) mvc.perform(post("/login").with(csrf()).param("username", "admin-test").param("password", PASSWORD))
                .andExpect(authenticated()).andReturn().getRequest().getSession(false);
        mvc.perform(post("/logout").session(session)).andExpect(status().isForbidden());
        mvc.perform(post("/logout").session(session).with(csrf())).andExpect(redirectedUrl("/login?logout"));
        assertThat(session.isInvalid()).isTrue();
    }

    @Test
    void doctorCannotReadOrWriteAnotherDoctorsConsultation() throws Exception {
        Consulta c = consultation(medico);
        HospitalPrincipal other = new HospitalPrincipal("other", "unused", "MEDICO", medico.getIdMedico() + 100);
        mvc.perform(get("/atendimento/iniciar/" + c.getIdConsulta()).with(user(other))).andExpect(status().isForbidden());
        mvc.perform(post("/atendimento/salvar/" + c.getIdConsulta()).with(user(other)).with(csrf()).param("diagnostico", "Tentativa indevida"))
            .andExpect(status().isForbidden());
        assertThat(attendances.findByConsulta(c)).isEmpty();
        mvc.perform(get("/consultas/minhas").with(user(other))).andExpect(status().isOk()).andExpect(model().attribute("consultas", java.util.List.of()));
    }

    @Test
    void attendanceUsesUrlConsultationAndProtectsClinicalDetails() throws Exception {
        Consulta c = consultation(medico);
        mvc.perform(get("/atendimento/iniciar/" + c.getIdConsulta()).with(user(doctor))).andExpect(status().isOk());
        mvc.perform(post("/atendimento/salvar/" + c.getIdConsulta()).with(user(doctor)).with(csrf())
                .param("idAtendimento", "9999").param("consulta.idConsulta", "9999").param("diagnostico", "Registro de teste"))
            .andExpect(redirectedUrl("/consultas/minhas"));
        Atendimento saved = attendances.findByConsulta(c).orElseThrow();
        assertThat(saved.getIdAtendimento()).isNotEqualTo(9999);
        assertThat(c.getStatusConsulta()).isEqualTo("ATENDIDA");
        HospitalPrincipal other = new HospitalPrincipal("other", "unused", "MEDICO", medico.getIdMedico() + 100);
        mvc.perform(get("/atendimento/detalhes/" + saved.getIdAtendimento()).with(user(other))).andExpect(status().isForbidden());
        mvc.perform(get("/atendimento/detalhes/" + saved.getIdAtendimento()).with(user(doctor))).andExpect(status().isOk());
        mvc.perform(post("/atendimento/salvar/" + c.getIdConsulta()).with(user(doctor)).with(csrf())).andExpect(status().isConflict());
    }

    @Test
    void userEditNeverRendersPasswordHashAndPreservesPatientLink() throws Exception {
        Usuario account = users.findByUsername("patient-test");
        String hash = account.getPassword();
        String html = mvc.perform(get("/admin/usuarios/formulario").param("id", account.getId().toString()).with(user(admin)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(html).doesNotContain(hash);
        mvc.perform(post("/admin/usuarios/salvar").with(user(admin)).with(csrf())
                .param("id", account.getId().toString()).param("username", "changed").param("role", "PACIENTE").param("password", ""))
            .andExpect(status().is3xxRedirection());
        assertThat(account.getPassword()).isEqualTo(hash);
        assertThat(account.getUsername()).isEqualTo("patient-test");
        assertThat(account.getPaciente().getIdPaciente()).isEqualTo(paciente.getIdPaciente());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/adminHome", "/admin/usuarios/listar", "/pacientes/listar", "/pacientes/criar", "/medicos/listar", "/medicos/criar", "/funcionarios/listar", "/funcionarios/criar", "/consultas/listar", "/leitos/listar", "/internacoes/listar", "/medicamentos/listar", "/relatorios/listar"})
    void affectedAdminTemplatesRender(String path) throws Exception {
        mvc.perform(get(path).with(user(admin))).andExpect(status().isOk());
    }
}
