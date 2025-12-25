package br.com.hospital.hospital.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idMedicamento;

    @Column(nullable = false, length = 60)
    private String nomeMedicamento;

    @Column(nullable = false, length = 20)
    private String dosagemMedicamento;

    @Column(nullable = false)
    private boolean ativo = true;
    
}