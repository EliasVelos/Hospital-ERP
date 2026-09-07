// PacienteCadastroDTO.java
package br.com.hospital.hospital.DTO;

import lombok.Data;

@Data
public class FuncionarioCadastroDTO {

    // --- DADOS DO PACIENTE ---
    private String nome;
    private String cpfFuncionario;
    private String cargo;
    private String setor;

    // --- DADOS DO USUÁRIO (LOGIN) ---
    private String username;
    @lombok.ToString.Exclude
    private String password;
}