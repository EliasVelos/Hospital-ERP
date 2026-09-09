package br.com.hospital.hospital.controller;

import br.com.hospital.hospital.entity.Atendimento;
import br.com.hospital.hospital.security.HospitalPrincipal;
import br.com.hospital.hospital.service.ClinicalAccessService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AtendimentoController {
    private final ClinicalAccessService clinical;

    public AtendimentoController(ClinicalAccessService clinical) { this.clinical = clinical; }

    @InitBinder("atendimento")
    void clinicalFields(WebDataBinder binder) {
        binder.setAllowedFields("queixaPrincipal", "exameFisico", "diagnostico", "planoTerapeutico", "prescricaoMedicamentos");
    }

    @GetMapping("/atendimento/iniciar/{id}")
    public String iniciar(@PathVariable Integer id, Model model, @AuthenticationPrincipal HospitalPrincipal user) {
        model.addAttribute("consulta", clinical.consultation(id, user));
        model.addAttribute("atendimento", new Atendimento());
        return "medicoHome/formularioAtendimento";
    }

    @PostMapping("/atendimento/salvar/{idConsulta}")
    public String salvar(@PathVariable Integer idConsulta, @ModelAttribute Atendimento atendimento,
            @AuthenticationPrincipal HospitalPrincipal user, RedirectAttributes redirect) {
        clinical.record(idConsulta, atendimento, user);
        redirect.addFlashAttribute("mensagemSucesso", "Atendimento registrado.");
        return "redirect:" + ("MEDICO".equals(user.getRole()) ? "/consultas/minhas" : "/consultas/listar");
    }

    @GetMapping("/atendimento/detalhes/{idAtendimento}")
    public String detalhes(@PathVariable Integer idAtendimento, Model model, @AuthenticationPrincipal HospitalPrincipal user) {
        model.addAttribute("atendimento", clinical.attendance(idAtendimento, user));
        return "medicoHome/detalhesAtendimento";
    }
}
