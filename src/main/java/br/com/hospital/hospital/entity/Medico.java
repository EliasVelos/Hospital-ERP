package br.com.hospital.hospital.entity;

import jakarta.persistence.*;
import lombok.Data;
// REMOVER: import java.util.UUID; 
// REMOVER: import jakarta.persistence.PrePersist;

@Entity
@Data
public class Medico {

    // Chave primária
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // OK, embora IDENTITY seja mais comum
    private Integer idMedico;

    // CRM gerado no Service, único, não nulo (se for novo)
    @Column(unique = true)
    private Integer crmMedico;

    @Column(nullable = false, length = 40)
    private String nomeMedico;

    @Column(nullable = false, length = 40)
    private String cpfMedico;

    @Column(nullable = false, length = 40)
    private String especialidadeMedico;

    @Column(nullable = false, length = 15)
    private String telefoneMedico;

    @Column(nullable = false, length = 40)
    private String enderecoMedico;

    @OneToOne(cascade = CascadeType.ALL) // 'Cascade' salva o 'Usuario' automaticamente
    @JoinColumn(name = "usuario_fk_id", referencedColumnName = "id")
    private Usuario usuario;
    @Override

public String toString() {
    return "Medico [idMedico=" + idMedico + 
           ", nomeMedico=" + nomeMedico + 
           ", idUsuario=" + (usuario != null ? usuario.getId() : "null") + "]";
}
}