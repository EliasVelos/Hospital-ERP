package br.com.hospital.hospital.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hospital.hospital.DTO.MedicoCadastroDTO;
import br.com.hospital.hospital.entity.Medico;
import br.com.hospital.hospital.entity.Usuario;
import br.com.hospital.hospital.repository.MedicoRepository;
import jakarta.transaction.Transactional;

@Service
public class MedicoService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MedicoRepository medicoRepository;
    private Integer gerarNovoCrm() {
        String uuid = UUID.randomUUID().toString().replaceAll("[^0-9]", "");
        while (uuid.length() < 6) {
            uuid += "0";
        }
        int numero = Integer.parseInt(uuid.substring(0, 6));
        if (numero < 100000) {
            numero += 100000;
            numero = numero % 999999;
        }

        return numero;
    }
    public Medico save(Medico medico) {
        return medicoRepository.save(medico);
    }
    public List<Medico> findAll(){
        return medicoRepository.findAll();
    }

    public void deleteById(Integer id){
        medicoRepository.deleteById(id);
    }

    public Medico findById(Integer id){
        return medicoRepository.findById(id).orElse(null);
    }

    @Transactional
    public Medico cadastrarNovoMedico(MedicoCadastroDTO dto) {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setUsername(dto.getUsername());
        novoUsuario.setPassword(usuarioService.encodePassword(dto.getPassword()));
        novoUsuario.setRole("MEDICO");
        Medico novoMedico = new Medico();
        novoMedico.setNomeMedico(dto.getNomeMedico());
        novoMedico.setCpfMedico(dto.getCpfMedico());
        novoMedico.setCrmMedico(gerarNovoCrm());

        novoMedico.setEspecialidadeMedico(dto.getEspecialidadeMedico());
        novoMedico.setTelefoneMedico(dto.getTelefoneMedico());
        novoMedico.setEnderecoMedico(dto.getEnderecoMedico());
        novoMedico.setUsuario(novoUsuario);
        return medicoRepository.save(novoMedico);
    }
    public long countTotalMedicos() {
        return medicoRepository.count();
    }


    @Transactional
    public void atualizarMedico(Medico form) {
        if (form.getIdMedico() == null) throw new IllegalArgumentException("Médico não encontrado.");
        Medico existing = medicoRepository.findById(form.getIdMedico())
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado."));
        existing.setNomeMedico(form.getNomeMedico());
        existing.setCpfMedico(form.getCpfMedico());
        existing.setEspecialidadeMedico(form.getEspecialidadeMedico());
        existing.setTelefoneMedico(form.getTelefoneMedico());
        existing.setEnderecoMedico(form.getEnderecoMedico());
        medicoRepository.save(existing);
    }
}
