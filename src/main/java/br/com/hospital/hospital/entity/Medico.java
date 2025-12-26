package br.com.hospital.hospital.entity;

import jakarta.persistence.*;
import lombok.Data;
// REMOVER: import java.util.UUID; 
// REMOVER: import jakarta.persistence.PrePersist;

@Entity
@Data
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idMedico;

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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_fk_id", referencedColumnName = "id")
    private Usuario usuario;
    @Override

    public String toString() {
        return "Medico [idMedico=" + idMedico + 
            ", nomeMedico=" + nomeMedico + 
            ", idUsuario=" + (usuario != null ? usuario.getId() : "null") + "]";
    }
}