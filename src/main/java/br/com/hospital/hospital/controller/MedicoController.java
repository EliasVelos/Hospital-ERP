package br.com.hospital.hospital.controller;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 

import br.com.hospital.hospital.DTO.MedicoCadastroDTO; // ⭐️ NOVO: Importa o DTO
import br.com.hospital.hospital.entity.Medico;
import br.com.hospital.hospital.service.MedicoService;

@Controller
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;
    
    // --- Método Auxiliar para as Especialidades (Não alterado) ---
    private Map<String, List<String>> getEspecialidadesAgrupadas() {
        Map<String, List<String>> especialidades = new LinkedHashMap<>();

        // GRUPO 1: ATENÇÃO PRIMÁRIA E CLÍNICA GERAL
        especialidades.put("Atenção Primária", List.of(
            "Clínica Médica", 
            "Pediatria", 
            "Ginecologia e Obstetrícia",
            "Medicina da Família e Comunidade"
        ));

        // GRUPO 2: ESPECIALIDADES CLÍNICAS (Comuns em Hospitais)
        especialidades.put("Especialidades Clínicas", List.of(
            "Cardiologia", 
            "Dermatologia", 
            "Endocrinologia",
            "Gastroenterologia", 
            "Neurologia", 
            "Psiquiatria",
            "Pneumologia"
        ));

        // GRUPO 3: ESPECIALIDADES CIRÚRGICAS E OUTRAS
        especialidades.put("Cirurgia e Suporte", List.of(
            "Cirurgia Geral",
            "Anestesiologia",
            "Ortopedia e Traumatologia",
            "Oftalmologia",
            "Urologia"
        ));
        
        return especialidades;
    }


    // ⭐️ 1A. NOVO MÉTODO POST PARA CRIAÇÃO (USA DTO) ⭐️
    @PostMapping("/cadastrar") // Rota específica para CRIAÇÃO
    public String cadastrar(@ModelAttribute("medicoDTO") MedicoCadastroDTO dto, RedirectAttributes ra) {
        try {
            medicoService.cadastrarNovoMedico(dto); // Usa o método do Service que cria o Usuário e o Médico
            ra.addFlashAttribute("mensagemSucesso", "Médico e Usuário cadastrados com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao cadastrar o médico: " + e.getMessage());
            // Se falhar, volta para o formulário de criação
            return "redirect:/medicos/criar"; 
        }
        return "redirect:/medicos/listar";
    }

    // ⭐️ 1B. MÉTODO POST ANTIGO PARA ATUALIZAÇÃO (USA ENTITY) ⭐️
    // --- 3A. MÉTODO GET PARA CRIAÇÃO (USA DTO) ---
@GetMapping("/criar")
public String criarform(Model model) {
    // ⭐️ Ambos usam a CHAVE "medicoForm"
    model.addAttribute("medicoForm", new MedicoCadastroDTO()); 
    model.addAttribute("isNew", true);
    model.addAttribute("especialidadesAgrupadas", getEspecialidadesAgrupadas());
    return "medico/formularioMedico";
}

// --- 5. MÉTODO GET PARA EDIÇÃO (USA ENTITY) ---



    // --- 2. LISTAR (READ ALL) ---
    @GetMapping("/listar")
    public String listar(Model model) {
        List<Medico> medicos = medicoService.findAll();
        model.addAttribute("medicos", medicos);
        return "medico/listaMedico";
    }
    // --- 4. EXCLUIR (DELETE) ---
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id, RedirectAttributes ra) { 
        try {
            medicoService.deleteById(id);
            ra.addFlashAttribute("mensagemSucesso", "Médico excluído com sucesso.");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao excluir o médico.");
        }
        return "redirect:/medicos/listar";
    }

    // ⭐️ 5. MÉTODO GET PARA EDIÇÃO (USA ENTITY) ⭐️
    @GetMapping("/editar/{id}")
        public String editarForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
            Medico medico = medicoService.findById(id);
            // ... (verifica se medico é null)
            
            // ⭐️ Ambos usam a CHAVE "medicoForm"
            model.addAttribute("medicoForm", medico);
            model.addAttribute("isNew", false);
            model.addAttribute("especialidadesAgrupadas", getEspecialidadesAgrupadas()); 
            
            return "medico/formularioMedico";
        }
}