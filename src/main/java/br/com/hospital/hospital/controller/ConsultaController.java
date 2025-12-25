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

    // OBS: Removi o AtendimentoRepository daqui. 
    // O ideal é o Controller falar apenas com Services, e não direto com Repository.

    // Salvar
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Consulta consulta) {
        consultaService.save(consulta);
        return "redirect:/consultas/listar";
    }

    // Listar (CORRIGIDO AQUI)
    @GetMapping("/listar")
    public String listar(Model model) {
        // AQUI ESTÁ A MUDANÇA MÁGICA:
        // Em vez de .findAll(), usamos o método que traz o Atendimento junto.
        // Certifique-se de ter criado esse método no Service conforme o passo 1.
        List<Consulta> consultas = consultaService.buscarTodasComAtendimento();
        
        model.addAttribute("consultas", consultas);
        return "consulta/listaConsulta";
    }

    // Criar
    @GetMapping("/criar")
    public String criarform(Model model) {
        model.addAttribute("consulta", new Consulta());
        model.addAttribute("pacientes", pacienteService.findAll());
        model.addAttribute("medicos", medicoService.findAll());
        return "consulta/formularioConsulta";
    }

    // Excluir
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Integer id) {
        // Remove os atendimentos antes para não dar erro de chave estrangeira
        atendimentoService.excluirAtendimentosDaConsulta(id);
        consultaService.deleteById(id);
        return "redirect:/consultas/listar";
    }

    // Editar
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable("id") Integer id, Model model) {
        Consulta consulta = consultaService.findById(id);
        model.addAttribute("consulta", consulta);
        model.addAttribute("pacientes", pacienteService.findAll());
        model.addAttribute("medicos", medicoService.findAll());
        return "consulta/formularioConsulta";
    }
}