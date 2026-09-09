package br.com.hospital.hospital.service;

import br.com.hospital.hospital.entity.Usuario;
import br.com.hospital.hospital.repository.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {
    public static final Set<String> ROLES = Set.of("ADMIN", "FUNCIONARIO", "MEDICO", "PACIENTE");
    private final UsuarioRepository users;
    private final PasswordEncoder passwords;

    public UsuarioService(UsuarioRepository users, PasswordEncoder passwords) {
        this.users = users;
        this.passwords = passwords;
    }

    public List<Usuario> findAll() { return users.findAll(); }
    public Optional<Usuario> findById(Integer id) { return users.findById(id); }

    public String encodePassword(String password) {
        if (password == null || password.isBlank() || password.length() < 12
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Use uma senha com pelo menos 12 caracteres e até 72 bytes.");
        }
        return passwords.encode(password);
    }

    @Transactional
    public Usuario save(Usuario form) {
        if (form.getRole() == null || !ROLES.contains(form.getRole())) {
            throw new IllegalArgumentException("Selecione um perfil válido.");
        }
        boolean creating = form.getId() == null;
        Usuario user = creating ? new Usuario() : users.findById(form.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        if (creating) {
            if (form.getUsername() == null || form.getUsername().isBlank() || form.getUsername().length() > 100) {
                throw new IllegalArgumentException("Informe um login de até 100 caracteres.");
            }
            user.setUsername(form.getUsername().trim());
        }
        // Atualiza somente os campos de acesso, preservando os vínculos do usuário.
        user.setRole(form.getRole());
        if (creating || (form.getPassword() != null && !form.getPassword().isEmpty())) {
            user.setPassword(encodePassword(form.getPassword()));
        }
        return users.save(user);
    }

    @Transactional
    public void deleteById(Integer id) { users.deleteById(id); }
}
