package br.com.hospital.hospital.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Leito {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idQuarto;

    @Column(nullable = false, length = 10)
    private String numero;

    @Column(nullable = false, length = 20)
    private String tipo; // enfermaria, UTI, particular

    @Column(nullable = false, length = 20)
    private String quarto; //numero do quarto
    
    @Column(nullable = false, length = 20)
    private String status; // ocupado, disponível, manutenção

    @Column(nullable = false)
    private boolean disponivel = true;
}
