package br.com.hospital.hospital.security;

import br.com.hospital.hospital.entity.Usuario;
import br.com.hospital.hospital.repository.UsuarioRepository;
import br.com.hospital.hospital.service.UsuarioService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HospitalUserDetailsService implements UserDetailsService {
    private final UsuarioRepository users;

    public HospitalUserDetailsService(UsuarioRepository users) { this.users = users; }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        Usuario user = users.findByUsernameWithRelations(username)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas."));
        if (user.getRole() == null || !UsuarioService.ROLES.contains(user.getRole()) || user.getEntidadeId() == null) {
            throw new UsernameNotFoundException("Credenciais inválidas.");
        }
        return new HospitalPrincipal(user.getUsername(), user.getPassword(), user.getRole(), user.getEntidadeId());
    }
}
