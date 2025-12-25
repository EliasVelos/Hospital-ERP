package br.com.hospital.hospital.controller;

import br.com.hospital.hospital.entity.Atendimento;
import br.com.hospital.hospital.entity.Consulta;
import br.com.hospital.hospital.service.AtendimentoService;
import br.com.hospital.hospital.service.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AtendimentoController {

    @Autowired
    private ConsultaService consultaService;

    @Autowired
    private AtendimentoService atendimentoService;

    @GetMapping("/atendimento/iniciar/{id}")
public String iniciarAtendimento(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {

    Optional<Consulta> optionalConsulta = consultaService.buscarConsultaPorId(id);

    if (optionalConsulta.isEmpty()) {
        redirectAttributes.addFlashAttribute("mensagemErro", "Consulta não encontrada.");
        return "redirect:/consultas/listar";
    }

    Consulta consulta = optionalConsulta.get();

    Atendimento atendimento = new Atendimento();
    atendimento.setConsulta(consulta); // ← Aqui está o ajuste

    model.addAttribute("consulta", consulta);
    model.addAttribute("atendimento", atendimento);

    return "medicoHome/formularioAtendimento";
}

@PostMapping("/atendimento/salvar/{idConsulta}") // Adicione o ID da Consulta na URL ou como @RequestParam
public String salvarAtendimento(@PathVariable("idConsulta") Integer idConsulta, 
                                @ModelAttribute Atendimento atendimento, // Use @ModelAttribute para ligar os campos do form
                                RedirectAttributes redirectAttributes) {
    
    try {
        // 1. Busque a Consulta persistente do banco (Resolvendo o erro 'null or transient value')
        Optional<Consulta> optionalConsulta = consultaService.buscarConsultaPorId(idConsulta);

        if (optionalConsulta.isEmpty()) {
             throw new Exception("Consulta associada não encontrada.");
        }
        
        // 2. Vincule a Consulta persistente ao Atendimento
        atendimento.setConsulta(optionalConsulta.get());
        
        // 3. Salva o atendimento
        atendimentoService.salvarAtendimento(atendimento);

        // 4. Atualiza o status da consulta associada (Agora o ID é garantido)
        consultaService.atualizarStatusConsulta(
            idConsulta, // Use o ID da URL, que é seguro
            "ATENDIDA"
        );

        redirectAttributes.addFlashAttribute("mensagemSucesso", "Atendimento salvo e consulta concluída!");
    } catch (Exception e) {
        // ... tratamento de erro
        redirectAttributes.addFlashAttribute("mensagemErro",
            "Erro ao salvar o atendimento: " + e.getMessage());
    }

    return "redirect:/consultas/listar";
}
@GetMapping("/atendimento/detalhes/{idAtendimento}")
public String detalhesAtendimento(@PathVariable("idAtendimento") Integer idAtendimento, Model model, RedirectAttributes redirectAttributes) {

    Optional<Atendimento> optionalAtendimento = atendimentoService.findById(idAtendimento); // Assumindo que este método existe no seu Service

    if (optionalAtendimento.isEmpty()) {
        redirectAttributes.addFlashAttribute("mensagemErro", "Atendimento não encontrado.");
        return "redirect:/consultas/listar";
    }

    Atendimento atendimento = optionalAtendimento.get();
    model.addAttribute("atendimento", atendimento);
    
    return "medicoHome/detalhesAtendimento"; // Nome do arquivo HTML criado
}
}
