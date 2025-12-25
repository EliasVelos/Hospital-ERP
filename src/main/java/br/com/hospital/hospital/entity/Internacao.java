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

    // Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idInternacao; 
    
    // --- CAMPOS DE ID PRIMITIVO REMOVIDOS ---
    // Os campos pacienteId e leitoId foram excluídos daqui.

    // ✅ RELACIONAMENTO COM PACIENTE (Chave Estrangeira)
    @ManyToOne 
    @JoinColumn(name = "paciente_fk_id", nullable = false) // Coluna no banco que armazena o ID do paciente
    private Paciente paciente; 

    // ✅ RELACIONAMENTO COM LEITO (Chave Estrangeira)
    @ManyToOne 
    @JoinColumn(name = "leito_fk_id", nullable = false) // Coluna no banco que armazena o ID do leito
    private Leito leito; 

    // --- CAMPOS PRÓPRIOS DA INTERNAÇÃO ---
    
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
