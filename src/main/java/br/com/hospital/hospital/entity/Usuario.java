package br.com.hospital.hospital.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", sequenceName = "usuario_id_seq", allocationSize = 1)
    private Integer id;

    @OneToOne(mappedBy = "usuario")
    private Paciente paciente;

    @OneToOne(mappedBy = "usuario")
    private Medico medico;

    @OneToOne(mappedBy = "usuario")
    private Funcionario funcionario;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    // ADMIN / MEDICO / PACIENTE / FUNCIONARIO
    @Column(nullable = false)
    private String role;

    // Método para obter o entidade_id automaticamente
    @Transient
    public Integer getEntidadeId() {
        switch (this.role.toUpperCase()) {
            case "PACIENTE":
                return paciente != null ? paciente.getIdPaciente() : null;
            case "MEDICO":
                return medico != null ? medico.getIdMedico() : null;
            case "FUNCIONARIO":
                return funcionario != null ? funcionario.getIdFuncionario() : null;
            case "ADMIN":
                return this.id; // Usa o próprio ID do usuário para admin
            default:
                return null;
        }
    }
    @Override
    public String toString() {
        return "Usuario [idUsuario=" + id + 
            ", username=" + username + 
            ", role=" + role + "]"; 
            // Remova ou substitua qualquer referência a Medico, Paciente, etc.
    }
}