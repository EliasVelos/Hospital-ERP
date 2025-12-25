package br.com.hospital.hospital.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator; // Importação Adicionada
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "relatorio")
public class Relatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "relatorio_seq")
    @SequenceGenerator(name = "relatorio_seq", sequenceName = "relatorio_id_seq", allocationSize = 1)
    private Integer idRelatorio;

    @Column(nullable = false)
    private LocalDate dataGeracao;

    @Column(nullable = false)
    private LocalDate dataInicioPeriodo;

    @Column(nullable = false)
    private LocalDate dataFimPeriodo;

    @Column(nullable = false, length = 200)
    private String tituloRelatorio;

    // Uso de columnDefinition = "TEXT" para PostgreSQL
    @Column(columnDefinition = "TEXT") 
    private String conteudo;
}