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
    private UsuarioService usuarioService;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Funcionario cadastrarNovoFuncionario(FuncionarioCadastroDTO dto) {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setUsername(dto.getUsername());
        novoUsuario.setPassword(usuarioService.encodePassword(dto.getPassword()));
        novoUsuario.setRole("FUNCIONARIO");

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        Funcionario novoFuncionario = new Funcionario();

        novoFuncionario.setNome(dto.getNome());
        novoFuncionario.setCpfFuncionario(dto.getCpfFuncionario());
        novoFuncionario.setCargo(dto.getCargo());
        novoFuncionario.setSetor(dto.getSetor());
        novoFuncionario.setUsuario(usuarioSalvo);

        Funcionario funcionarioSalvo = funcionarioRepository.save(novoFuncionario);

        return funcionarioSalvo;
    }
    public Funcionario save(Funcionario funcionario) {
        return funcionarioRepository.save(funcionario);
    }
    public List<Funcionario> findAll(){
        return funcionarioRepository.findAll();
    }
    public void deleteById(Integer id){
        funcionarioRepository.deleteById(id);
    }
    public Optional<Funcionario> findById(Integer id) {
    return funcionarioRepository.findById(id);
}


    @Transactional
    public Funcionario atualizarFuncionario(Funcionario form) {
        if (form.getIdFuncionario() == null) throw new IllegalArgumentException("Funcionário não encontrado.");
        Funcionario existing = funcionarioRepository.findById(form.getIdFuncionario())
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));
        existing.setNome(form.getNome());
        existing.setCpfFuncionario(form.getCpfFuncionario());
        existing.setCargo(form.getCargo());
        existing.setSetor(form.getSetor());
        return funcionarioRepository.save(existing);
    }
}
