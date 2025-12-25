package br.com.hospital.hospital.DTO;

// Use Lombok se quiser (ou crie Getters/Setters manualmente)
import lombok.Data;

@Data
public class MedicoCadastroDTO {

    // 1. Dados do Usuário (para o Login)
    private String username;
    private String password;
    
    // 2. Dados do Médico
    // (Ajuste os nomes dos campos para bater com sua Entity Medico)
    private String nomeMedico;
    private String cpfMedico; // <--- ESTE CAMPO DEVE EXISTIR
    private Integer crmMedico;
    private String especialidadeMedico;
    private String telefoneMedico;
    private String enderecoMedico;
    
    // Adicione outros campos do Médico se necessário...
}