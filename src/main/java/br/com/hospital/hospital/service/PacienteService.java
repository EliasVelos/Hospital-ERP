// PacienteService.java
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
    private PacienteRepository pacienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Paciente cadastrarNovoPaciente(PacienteCadastroDTO dto) {

        // 1. SALVAR USUÁRIO
        Usuario novoUsuario = new Usuario();
        novoUsuario.setUsername(dto.getUsername());
        novoUsuario.setPassword(dto.getPassword());
        novoUsuario.setRole("PACIENTE");

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);


        // 2. SALVAR PACIENTE
        Paciente novoPaciente = new Paciente();

        novoPaciente.setNomePaciente(dto.getNomePaciente());
        novoPaciente.setCpfPaciente(dto.getCpfPaciente());
        novoPaciente.setNascPaciente(dto.getNascPaciente());
        novoPaciente.setTelefonePaciente(dto.getTelefonePaciente());
        novoPaciente.setEnderecoPaciente(dto.getEnderecoPaciente());
        novoPaciente.setSexoPaciente(dto.getSexoPaciente());
        novoPaciente.setPesoPaciente(dto.getPesoPaciente());
        novoPaciente.setTipoSanguinioPaciente(dto.getTipoSanguinioPaciente());

        // Ligar o paciente ao usuário
        novoPaciente.setUsuario(usuarioSalvo);

        Paciente pacienteSalvo = pacienteRepository.save(novoPaciente);

        // 3. Parte de atualizar o usuário foi removida
        // Porque o Usuario NÃO possui relacionamento com Paciente.
        // Se quiser tornar bidirecional, basta pedir.

        return pacienteSalvo;
    }


    // Métodos auxiliares já existentes
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
        // 1. Garante que o ID existe para a atualização
        if (dto.getIdPaciente() == null) {
            throw new IllegalArgumentException("ID do Paciente é obrigatório para atualização.");
        }

        // 2. Busca a entidade Paciente existente
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(dto.getIdPaciente());

        if (pacienteOpt.isEmpty()) {
            throw new RuntimeException("Paciente com ID " + dto.getIdPaciente() + " não encontrado para atualização.");
        }

        Paciente pacienteExistente = pacienteOpt.get();

        // 3. Mapeia/Atualiza os campos da entidade com base no DTO
        // IMPORTANTE: Aqui, você só atualiza os campos do Paciente, não do Usuário/Login.
        pacienteExistente.setNomePaciente(dto.getNomePaciente());
        
        // ⚠️ ATENÇÃO: Se o seu backend exige CPF e Telefone sem máscara, 
        // você deve limpá-los antes de salvar.
        // Se o seu DTO já tem um método para limpar, use-o. Senão, faça aqui:
        pacienteExistente.setCpfPaciente(dto.getCpfPaciente().replaceAll("[^0-9]", ""));
        pacienteExistente.setTelefonePaciente(dto.getTelefonePaciente().replaceAll("[^0-9]", ""));
        
        // ----------------------------------------------------
        // Atualize todos os campos de Paciente aqui:
        // ----------------------------------------------------
        pacienteExistente.setNascPaciente(dto.getNascPaciente());
        pacienteExistente.setEnderecoPaciente(dto.getEnderecoPaciente());
        pacienteExistente.setSexoPaciente(dto.getSexoPaciente());
        pacienteExistente.setPesoPaciente(dto.getPesoPaciente());
        pacienteExistente.setTipoSanguinioPaciente(dto.getTipoSanguinioPaciente());
        
        // 4. Salva a entidade atualizada no banco de dados
        pacienteRepository.save(pacienteExistente);
    }
}
