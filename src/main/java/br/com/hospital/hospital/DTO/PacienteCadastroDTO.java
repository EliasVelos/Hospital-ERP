// PacienteCadastroDTO.java
package br.com.hospital.hospital.DTO;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PacienteCadastroDTO {

    // --- DADOS DO PACIENTE ---
    private Integer idPaciente;
    private String nomePaciente;
    private String cpfPaciente;
    private LocalDate nascPaciente;
    private String telefonePaciente;
    private String enderecoPaciente;
    private String sexoPaciente; // 'M' ou 'F'
    private Double pesoPaciente;
    private String tipoSanguinioPaciente; // 'A+', 'O-', etc.

    // --- DADOS DO USUÁRIO (LOGIN) ---
    private String username;
    private String password;
}