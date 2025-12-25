package br.com.hospital.hospital.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Internacao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idInternacao; 
    
    @ManyToOne 
    @JoinColumn(name = "paciente_fk_id", nullable = false) 
    private Paciente paciente; 

    @ManyToOne 
    @JoinColumn(name = "leito_fk_id", nullable = false) 
    private Leito leito; 
    
    @Column(nullable = false)
    private LocalDateTime dataEntrada; 

    @Column(nullable = true)
    private LocalDateTime  dataAlta;

    @Column(nullable = false, length = 255)
    private String motivoInternacao;

    @Column(nullable = false, length = 20)
    private String status = "Ativa"; 

    @Override
    public String toString() {
        return "Internacao{" +
                "idInternacao=" + idInternacao +
                ", dataEntrada=" + dataEntrada +
                ", dataAlta=" + dataAlta +
                ", status='" + status + '\'' +
                '}';
    }
}
