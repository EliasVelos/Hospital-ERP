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

import br.com.hospital.hospital.DTO.MedicoCadastroDTO;
import br.com.hospital.hospital.entity.Medico;
import br.com.hospital.hospital.service.MedicoService;

@Controller
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired
    private MedicoService medicoService;
    private Map<String, List<String>> getEspecialidadesAgrupadas() {
        Map<String, List<String>> especialidades = new LinkedHashMap<>();
        especialidades.put("Atenção Primária", List.of(
            "Clínica Médica",
            "Pediatria",
            "Ginecologia e Obstetrícia",
            "Medicina da Família e Comunidade"
        ));
        especialidades.put("Especialidades Clínicas", List.of(
            "Cardiologia",
            "Dermatologia",
            "Endocrinologia",
            "Gastroenterologia",
            "Neurologia",
            "Psiquiatria",
            "Pneumologia"
        ));
        especialidades.put("Cirurgia e Suporte", List.of(
            "Cirurgia Geral",
            "Anestesiologia",
            "Ortopedia e Traumatologia",
            "Oftalmologia",
            "Urologia"
        ));

        return especialidades;
    }
    @PostMapping("/cadastrar")
    public String cadastrar(@ModelAttribute("medicoDTO") MedicoCadastroDTO dto, RedirectAttributes ra) {
        try {
            medicoService.cadastrarNovoMedico(dto);
            ra.addFlashAttribute("mensagemSucesso", "Médico e Usuário cadastrados com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Não foi possível cadastrar. Confira os dados e use uma senha com pelo menos 12 caracteres.");
            return "redirect:/medicos/criar";
        }
        return "redirect:/medicos/listar";
    }
@GetMapping("/criar")
public String criarform(Model model) {
    model.addAttribute("medicoForm", new MedicoCadastroDTO());
    model.addAttribute("isNew", true);
    model.addAttribute("especialidadesAgrupadas", getEspecialidadesAgrupadas());
    return "medico/formularioMedico";
}
    @GetMapping("/listar")
    public String listar(Model model) {
        List<Medico> medicos = medicoService.findAll();
        model.addAttribute("medicos", medicos);
        return "medico/listaMedico";
    }
    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            medicoService.deleteById(id);
            ra.addFlashAttribute("mensagemSucesso", "Médico excluído com sucesso.");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao excluir o médico.");
        }
        return "redirect:/medicos/listar";
    }
    @GetMapping("/editar/{id}")
        public String editarForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
            Medico medico = medicoService.findById(id);
            model.addAttribute("medicoForm", medico);
            model.addAttribute("isNew", false);
            model.addAttribute("especialidadesAgrupadas", getEspecialidadesAgrupadas());

            return "medico/formularioMedico";
        }

    @PostMapping("/salvar")
    public String atualizar(@ModelAttribute Medico medico, RedirectAttributes redirect) {
        medicoService.atualizarMedico(medico);
        redirect.addFlashAttribute("mensagemSucesso", "Dados do médico atualizados.");
        return "redirect:/medicos/listar";
    }
}
