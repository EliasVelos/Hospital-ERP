package br.com.hospital.hospital.config;

import br.com.hospital.hospital.entity.Usuario;
import br.com.hospital.hospital.repository.UsuarioRepository;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Conversão pontual e explícita da base antiga. O login nunca aceita texto puro. */
@Component
@ConditionalOnProperty(name = "app.migrate-legacy-passwords", havingValue = "true")
public class LegacyPasswordMigration implements CommandLineRunner {
    private static final Pattern BCRYPT = Pattern.compile("^\\$2[aby]\\$[0-9]{2}\\$[./A-Za-z0-9]{53}$");
    private final UsuarioRepository users;
    private final PasswordEncoder passwords;

    public LegacyPasswordMigration(UsuarioRepository users, PasswordEncoder passwords) {
        this.users = users;
        this.passwords = passwords;
    }

    @Override
    @Transactional
    public void run(String... args) {
        for (Usuario user : users.findAll()) {
            String existing = user.getPassword();
            if (existing == null || existing.isBlank()) {
                throw new IllegalStateException("Há uma conta sem senha. Redefina seu acesso antes da migração.");
            }
            if (!BCRYPT.matcher(existing).matches()) {
                user.setPassword(passwords.encode(existing));
            }
        }
    }
}
