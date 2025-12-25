package br.com.hospital.hospital.entity;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class MovMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idMovimentacao;

    @ManyToOne
    @JoinColumn(name = "id_medicamento", nullable = false)
    private Medicamento medicamento;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(nullable = false)
    private LocalDateTime dataMovimentacao;

    @Column(nullable = false, length = 10)
    private String tipoMovimentacao; // ENTRADA ou SAIDA

    @Column(nullable = false)
    private Integer quantidadeMovimentada;

    @Column(length = 60)
    private String responsavelMovimentacao;
    // NA ENTIDADE MovMedicamento.java
    
    @ManyToOne
    @JoinColumn(name = "lote_id") // Nome da coluna no banco (sugestão)
    private LoteMedicamento lote;
    }
