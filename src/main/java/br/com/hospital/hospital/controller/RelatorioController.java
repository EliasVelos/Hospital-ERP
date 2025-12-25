package br.com.hospital.hospital.controller;

import java.util.List;
import java.time.LocalDate; // Necessário para LocalDate
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; // Necessário para receber o objeto Relatorio
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.hospital.hospital.entity.Relatorio;
import br.com.hospital.hospital.service.RelatorioService;

@Controller
@RequestMapping("/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    // Listar todos os relatórios
    @GetMapping("/listar")
    public String listar(Model model) {
        List<Relatorio> relatorios = relatorioService.findAll();
        model.addAttribute("relatorios", relatorios);
        return "relatorio/listaRelatorio";
    }
    
    // 1. ABRIR FORMULÁRIO (Exibe o formulário de datas com a data de inauguração pré-preenchida)
    @GetMapping("/criar")
    public String criarform(Model model) {
        
        // Define a data de inauguração: 08/12/2003
        LocalDate dataInauguracao = LocalDate.of(2003, 12, 8); 
        
        Relatorio relatorio = new Relatorio();
        
        // Inicializa o período com a data de inauguração e a data atual
        relatorio.setDataInicioPeriodo(dataInauguracao);
        relatorio.setDataFimPeriodo(LocalDate.now());

        model.addAttribute("relatorio", relatorio);
        
        return "relatorio/formularioRelatorio"; 
    }

    // 2. GERAR NOVO RELATÓRIO (Passa as datas do formulário para o Service)
    @PostMapping("/gerar")
    public String gerarNovoRelatorio(@ModelAttribute Relatorio relatorio, RedirectAttributes redirectAttributes) {
        try {
            // ✅ CORREÇÃO: Chamando o Service com os dois argumentos (dataInicio e dataFim)
            relatorioService.gerarNovoRelatorio(
                relatorio.getDataInicioPeriodo(),
                relatorio.getDataFimPeriodo()
            );
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Novo relatório administrativo gerado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao gerar o relatório: " + e.getMessage());
        }
        return "redirect:/relatorios/listar";
    }

    // Exibir detalhes de um relatório
    @GetMapping("/detalhes/{id}")
    public String detalhes(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Relatorio relatorio = relatorioService.findById(id);
        if (relatorio == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Relatório não encontrado!");
            return "redirect:/relatorios/listar";
        }
        model.addAttribute("relatorio", relatorio);
        return "relatorio/detalhesRelatorio";
    }

    // Excluir
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            relatorioService.deleteById(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Relatório excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao excluir o relatório.");
        }
        return "redirect:/relatorios/listar";
    }
}