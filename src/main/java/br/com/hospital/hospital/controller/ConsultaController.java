package br.com.hospital.hospital.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.hospital.hospital.entity.Consulta;
import br.com.hospital.hospital.service.AtendimentoService;
import br.com.hospital.hospital.service.ConsultaService;
import br.com.hospital.hospital.service.MedicoService;
import br.com.hospital.hospital.service.PacienteService;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @Autowired
    private AtendimentoService atendimentoService;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private MedicoService medicoService;
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Consulta consulta) {
        consultaService.save(consulta);
        return "redirect:/consultas/listar";
    }
    @GetMapping("/listar")
    public String listar(Model model, org.springframework.security.core.Authentication authentication) {
        List<Consulta> consultas = consultaService.buscarTodasComAtendimento();

        model.addAttribute("consultas", consultas);
        model.addAttribute("canManage", true);
        model.addAttribute("canAttend", authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        return "consulta/listaConsulta";
    }
    @GetMapping("/criar")
    public String criarform(Model model) {
        model.addAttribute("consulta", new Consulta());
        model.addAttribute("pacientes", pacienteService.findAll());
        model.addAttribute("medicos", medicoService.findAll());
        return "consulta/formularioConsulta";
    }
    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Integer id) {
        consultaService.deleteById(id);
        return "redirect:/consultas/listar";
    }
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable("id") Integer id, Model model) {
        Consulta consulta = consultaService.findById(id);
        model.addAttribute("consulta", consulta);
        model.addAttribute("pacientes", pacienteService.findAll());
        model.addAttribute("medicos", medicoService.findAll());
        return "consulta/formularioConsulta";
    }

    @GetMapping("/minhas")
    public String minhas(Model model,
            @org.springframework.security.core.annotation.AuthenticationPrincipal br.com.hospital.hospital.security.HospitalPrincipal user) {
        model.addAttribute("consultas", consultaService.buscarDoMedico(user.getEntityId()));
        model.addAttribute("canManage", false);
        model.addAttribute("canAttend", true);
        return "consulta/listaConsulta";
    }
}
