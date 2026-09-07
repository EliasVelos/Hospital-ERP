package br.com.hospital.hospital.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.hospital.hospital.entity.Paciente;
import br.com.hospital.hospital.entity.Usuario;
import br.com.hospital.hospital.repository.PacienteRepository;
import br.com.hospital.hospital.repository.UsuarioRepository;
import br.com.hospital.hospital.DTO.PacienteCadastroDTO;

@Service
public class PacienteService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Paciente cadastrarNovoPaciente(PacienteCadastroDTO dto) {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setUsername(dto.getUsername());
        novoUsuario.setPassword(usuarioService.encodePassword(dto.getPassword()));
        novoUsuario.setRole("PACIENTE");

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        Paciente novoPaciente = new Paciente();

        novoPaciente.setNomePaciente(dto.getNomePaciente());
        novoPaciente.setCpfPaciente(dto.getCpfPaciente());
        novoPaciente.setNascPaciente(dto.getNascPaciente());
        novoPaciente.setTelefonePaciente(dto.getTelefonePaciente());
        novoPaciente.setEnderecoPaciente(dto.getEnderecoPaciente());
        novoPaciente.setSexoPaciente(dto.getSexoPaciente());
        novoPaciente.setPesoPaciente(dto.getPesoPaciente());
        novoPaciente.setTipoSanguinioPaciente(dto.getTipoSanguinioPaciente());
        novoPaciente.setUsuario(usuarioSalvo);

        Paciente pacienteSalvo = pacienteRepository.save(novoPaciente);

        return pacienteSalvo;
    }
    public Paciente save(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    public List<Paciente> findAll() {
        return pacienteRepository.findAll();
    }

    public void deleteById(Integer id) {
        pacienteRepository.deleteById(id);
    }

    public Optional<Paciente> findById(Integer id) {
        return pacienteRepository.findById(id);
    }
    public void atualizarPaciente(PacienteCadastroDTO dto) {
        if (dto.getIdPaciente() == null) {
            throw new IllegalArgumentException("ID do Paciente é obrigatório para atualização.");
        }
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(dto.getIdPaciente());

        if (pacienteOpt.isEmpty()) {
            throw new RuntimeException("Paciente com ID " + dto.getIdPaciente() + " não encontrado para atualização.");
        }

        Paciente pacienteExistente = pacienteOpt.get();
        pacienteExistente.setNomePaciente(dto.getNomePaciente());
        pacienteExistente.setCpfPaciente(dto.getCpfPaciente().replaceAll("[^0-9]", ""));
        pacienteExistente.setTelefonePaciente(dto.getTelefonePaciente().replaceAll("[^0-9]", ""));
        pacienteExistente.setNascPaciente(dto.getNascPaciente());
        pacienteExistente.setEnderecoPaciente(dto.getEnderecoPaciente());
        pacienteExistente.setSexoPaciente(dto.getSexoPaciente());
        pacienteExistente.setPesoPaciente(dto.getPesoPaciente());
        pacienteExistente.setTipoSanguinioPaciente(dto.getTipoSanguinioPaciente());
        pacienteRepository.save(pacienteExistente);
    }
}
