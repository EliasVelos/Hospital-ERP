package br.com.hospital.hospital.security;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                .requestMatchers(HttpMethod.GET, "/login", "/hospital", "/css/**", "/js/**", "/img/**", "/favicon.ico").permitAll()
                .requestMatchers("/", "/dashboard").authenticated()
                .requestMatchers("/pacienteHome", "/minhasConsultas", "/pacientes/meusdados").hasRole("PACIENTE")
                .requestMatchers("/funcionarioHome", "/funcionarios/perfil", "/funcionarios/funcionarioHome").hasRole("FUNCIONARIO")
                .requestMatchers("/medicoHome", "/consultas/minhas").hasRole("MEDICO")
                .requestMatchers("/atendimento/**").hasAnyRole("MEDICO", "ADMIN")
                .requestMatchers("/admin/**", "/adminHome", "/medicos/**", "/funcionarios/**", "/relatorios/**").hasRole("ADMIN")
                .requestMatchers("/pacientes/**", "/cadastroPaciente", "/consultas/**", "/internacoes/**", "/leitos/**", "/medicamentos/**", "/movMedicamentos/**").hasAnyRole("ADMIN", "FUNCIONARIO")
                .anyRequest().denyAll())
            .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", true).failureUrl("/login?error").permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/login?logout").invalidateHttpSession(true).deleteCookies("JSESSIONID"))
            .build();
    }
}
