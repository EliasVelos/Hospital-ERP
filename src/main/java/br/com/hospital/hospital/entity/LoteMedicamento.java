package br.com.hospital.hospital.entity;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator; // Importação Adicionada
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "lote_medicamento")
public class LoteMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lote_medicamento_seq")
    @SequenceGenerator(name = "lote_medicamento_seq", sequenceName = "lote_medicamento_id_seq", allocationSize = 1)
    private Long idLote;

    // Relacionamento: N Lotes pertencem a 1 Medicamento (Mestre)
    @ManyToOne
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @Column(nullable = false, length = 50)
    private String numeroLote; // Campo para o número de lote do fabricante

    @Column(nullable = false)
    private Integer quantidadeEmEstoque; // A quantidade neste lote específico

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate dataValidade; // A validade deste lote

    @Column(nullable = true)
    private Double precoCusto; // Um campo para registrar o preço de compra (opcional, mas bom para controle de custo)
}