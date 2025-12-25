package br.com.hospital.hospital.service;

import br.com.hospital.hospital.entity.Usuario;
import br.com.hospital.hospital.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario fazerLogin(String username, String password) {
        return usuarioRepository.findByUsernameAndPassword(username, password);
    }
    
    public Usuario fazerLoginComTipo(String username, String password, String tipoUsuario) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsernameAndPasswordWithRelations(username, password);
        
        if (usuarioOpt.isEmpty()) {
            return null;
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Verifica se o tipo de usuário corresponde
        if (!usuario.getRole().equalsIgnoreCase(tipoUsuario)) {
            throw new RuntimeException("Tipo de usuário incorreto. Você é um: " + usuario.getRole());
        }
        
        // Verifica se a entidade específica existe (se aplicável)
        Integer entidadeId = usuario.getEntidadeId();
        if (entidadeId == null && !tipoUsuario.equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Perfil não encontrado para este tipo de usuário");
        }
        
        return usuario;
    }
    // ------------------------------------

    // --- NOVOS MÉTODOS DE CRUD ---
    
    public List<Usuario> findAll() {
        // Usamos findAll, mas sem JOIN FETCH explícito aqui para simplicidade. 
        // O JPA pode carregar as entidades relacionadas lazymente quando acessadas na Controller/Template.
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findById(Integer id) {
        return usuarioRepository.findById(id);
    }

    public Usuario save(Usuario usuario) {
        // **ATENÇÃO: Adicione aqui a lógica de criptografia de senha se estiver usando Spring Security!**
        // Exemplo: usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        // Se estiver editando, e o campo de senha vier vazio, não altere a senha antiga.
        if (usuario.getId() != null && (usuario.getPassword() == null || usuario.getPassword().isEmpty())) {
            usuarioRepository.findById(usuario.getId()).ifPresent(u -> usuario.setPassword(u.getPassword()));
        }

        return usuarioRepository.save(usuario);
    }

    public void deleteById(Integer id) {
        // ATENÇÃO: Se o usuário tiver um Paciente/Medico/Funcionario, 
        // a exclusão aqui pode falhar devido à FK. Você precisará tratar 
        // a exclusão da entidade relacionada primeiro, ou configurar o cascade.
        // Se a Entity relacionada for essencial, a exclusão do usuário deve ser impedida.
        usuarioRepository.deleteById(id);
    }
}