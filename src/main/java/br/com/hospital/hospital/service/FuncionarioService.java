package br.com.hospital.hospital.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.hospital.hospital.DTO.FuncionarioCadastroDTO;
import br.com.hospital.hospital.entity.Funcionario;
import br.com.hospital.hospital.entity.Usuario;
import br.com.hospital.hospital.repository.FuncionarioRepository;
import br.com.hospital.hospital.repository.UsuarioRepository;

@Service
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Funcionario cadastrarNovoFuncionario(FuncionarioCadastroDTO dto) {

        // 1. SALVAR USUÁRIO
        Usuario novoUsuario = new Usuario();
        novoUsuario.setUsername(dto.getUsername());
        novoUsuario.setPassword(dto.getPassword());
        novoUsuario.setRole("FUNCIONARIO");

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);


        // 2. SALVAR PACIENTE
        Funcionario novoFuncionario = new Funcionario();

        novoFuncionario.setNome(dto.getNome());
        novoFuncionario.setCpfFuncionario(dto.getCpfFuncionario());
        novoFuncionario.setCargo(dto.getCargo());
        novoFuncionario.setSetor(dto.getSetor());

        // Ligar o paciente ao usuário
        novoFuncionario.setUsuario(usuarioSalvo);

        Funcionario funcionarioSalvo = funcionarioRepository.save(novoFuncionario);

        // 3. Parte de atualizar o usuário foi removida
        // Porque o Usuario NÃO possui relacionamento com Paciente.
        // Se quiser tornar bidirecional, basta pedir.

        return funcionarioSalvo;
    }

    //Salvar
    public Funcionario save(Funcionario funcionario) {
        return funcionarioRepository.save(funcionario);
    }

    //Listar
    public List<Funcionario> findAll(){
        return funcionarioRepository.findAll();
    }

    //Excluir
    public void deleteById(Integer id){
        funcionarioRepository.deleteById(id);
    }

    //Editar (Busca por ID, conforme o exemplo)
    public Optional<Funcionario> findById(Integer id) {
    return funcionarioRepository.findById(id);
}

}