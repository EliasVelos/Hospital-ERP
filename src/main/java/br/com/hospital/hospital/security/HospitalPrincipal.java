package br.com.hospital.hospital.security;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

/** Mantém apenas a identidade de acesso na sessão, sem carregar o prontuário. */
public class HospitalPrincipal extends User {
    private static final long serialVersionUID = 1L;
    private final Integer entityId;
    private final String role;

    public HospitalPrincipal(String username, String passwordHash, String role, Integer entityId) {
        super(username, passwordHash, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        this.role = role;
        this.entityId = entityId;
    }

    public Integer getEntityId() { return entityId; }
    public String getRole() { return role; }

    public String homeUrl() {
        return switch (role) {
            case "ADMIN" -> "/adminHome";
            case "MEDICO" -> "/medicoHome";
            case "FUNCIONARIO" -> "/funcionarioHome";
            case "PACIENTE" -> "/pacienteHome";
            default -> throw new IllegalStateException("Perfil de acesso não reconhecido.");
        };
    }
}
