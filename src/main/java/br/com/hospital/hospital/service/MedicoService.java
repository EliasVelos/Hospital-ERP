package br.com.hospital.hospital.service;

import java.util.List;
import java.util.UUID; // Import necessário

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hospital.hospital.DTO.MedicoCadastroDTO;
import br.com.hospital.hospital.entity.Medico;
import br.com.hospital.hospital.entity.Usuario;
import br.com.hospital.hospital.repository.MedicoRepository;
import jakarta.transaction.Transactional;

@Service
public class MedicoService{

    @Autowired
    private MedicoRepository medicoRepository;

    // --- Lógica de Geração de CRM movida para cá ---
    private Integer gerarNovoCrm() {
        String uuid = UUID.randomUUID().toString().replaceAll("[^0-9]", "");
        while (uuid.length() < 6) {
            uuid += "0";
        }
        int numero = Integer.parseInt(uuid.substring(0, 6));

        // Garante que tenha 6 dígitos (evita 0 na primeira posição se o random for muito pequeno)
        if (numero < 100000) {
            // O ideal aqui seria garantir unicidade, mas o JPA cuida do unique=true
            numero += 100000;
            numero = numero % 999999;
        }

        return numero;
    }
    // ------------------------------------------------

    // Salvar (CREATE & UPDATE)
    public Medico save(Medico medico) {
        return medicoRepository.save(medico);
    }

    // Listar, Excluir, findById permanecem os mesmos...
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
        
        // 1. CRIAR O USUÁRIO (É OBRIGATÓRIO SALVAR ESTE OBJETO SE NÃO USAR CASCADE)
        Usuario novoUsuario = new Usuario();
        novoUsuario.setUsername(dto.getUsername());
        // Salvando em texto puro, confirme se o PasswordEncoder DEVE ser usado aqui!
        // novoUsuario.setPassword(passwordEncoder.encode(dto.getPassword())); // Idealmente deve ser criptografado
        novoUsuario.setPassword(dto.getPassword()); 
        novoUsuario.setRole("MEDICO"); 

        // 🚨 Se você não usa CASCADE.ALL, PRECISA salvar o usuário explicitamente aqui:
        // usuarioRepository.save(novoUsuario); 

        // 2. CRIAR O MÉDICO
        Medico novoMedico = new Medico();
        novoMedico.setNomeMedico(dto.getNomeMedico());
        
        // ⭐️ CORREÇÃO 1: Mapear o CPF do DTO 
        novoMedico.setCpfMedico(dto.getCpfMedico()); 
        
        // ⭐️ CORREÇÃO 2: Gerar o CRM AQUI, ignorando o valor do DTO (que pode ser null)
        novoMedico.setCrmMedico(gerarNovoCrm()); 
        
        novoMedico.setEspecialidadeMedico(dto.getEspecialidadeMedico());
        novoMedico.setTelefoneMedico(dto.getTelefoneMedico());
        novoMedico.setEnderecoMedico(dto.getEnderecoMedico());

        // 3. LIGAR OS DOIS
        novoMedico.setUsuario(novoUsuario); 
        
        // 4. Salva o Médico (e o Usuário, se o Cascade estiver configurado)
        return medicoRepository.save(novoMedico);
    }
}