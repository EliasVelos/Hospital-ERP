package br.com.hospital.hospital.config;

import br.com.hospital.hospital.entity.Usuario;
import br.com.hospital.hospital.repository.UsuarioRepository;
import br.com.hospital.hospital.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "app.bootstrap.enabled", havingValue = "true")
public class DataInitializer implements CommandLineRunner {
    private final UsuarioRepository users;
    private final UsuarioService userService;
    private final String username;
    private final String password;

    public DataInitializer(UsuarioRepository users, UsuarioService userService,
            @Value("${app.bootstrap.username:}") String username,
            @Value("${app.bootstrap.password:}") String password) {
        this.users = users;
        this.userService = userService;
        this.username = username;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (users.count() != 0) return;
        Usuario admin = new Usuario();
        admin.setUsername(username);
        admin.setPassword(password);
        admin.setRole("ADMIN");
        userService.save(admin);
    }
}
