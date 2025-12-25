package br.com.hospital.hospital.entity;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Paciente{

    //Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idPaciente;

    @OneToOne
    @JoinColumn(name = "usuario_fk_id")
    private Usuario usuario;

    @Column(nullable = false, length = 40)
    private String nomePaciente;

    @Column(nullable = false, length = 40)
    private String cpfPaciente;

    @Column(nullable = false, length = 4000)
    private String enderecoPaciente;

    @Column(nullable = false, length = 40, name = "tiposanguinio_paciente")
    private String tipoSanguinioPaciente;

    @Column(nullable = false, length = 40)
    private String sexoPaciente;

    @Column(nullable = false, length = 15)
    private String telefonePaciente;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate nascPaciente;


    private Double pesoPaciente;

    @Override
public String toString() {
    return "Paciente{" +
            "idPaciente=" + idPaciente +
            ", nomePaciente='" + nomePaciente + '\'' +
            ", cpfPaciente='" + cpfPaciente + '\'' +
            '}';
}
}