package br.com.hospital.hospital.entity;

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
public class Funcionario {

    //Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idFuncionario;

    @Column(nullable = false, length = 40)
    private String nome;

    @Column(nullable = false, length = 40)
    private String cpfFuncionario;

    @Column(nullable = false, length = 40)
    private String cargo;

    @Column(nullable = false, length = 40)
    private String setor;

    @OneToOne
    @JoinColumn(name = "usuario_fk_id")
    private Usuario usuario;
    
}   