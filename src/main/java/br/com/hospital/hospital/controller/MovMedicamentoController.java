package br.com.hospital.hospital.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.hospital.hospital.entity.Medicamento;
import br.com.hospital.hospital.entity.MovMedicamento;
import br.com.hospital.hospital.service.MedicamentoService;
import br.com.hospital.hospital.service.MovMedicamentoService;

@Controller
@RequestMapping("/movMedicamentos")
public class MovMedicamentoController {

    @Autowired
    private MovMedicamentoService movMedicamentoService;

    @Autowired
    private MedicamentoService medicamentoService;
    
    // Método auxiliar para carregar dados para o formulário
    @ModelAttribute("medicamentos")
    public List<Medicamento> popularMedicamentos() {
        return medicamentoService.listarAtivos(); 
    }

    @PostMapping("/salvar")
    public String salvar(
            @ModelAttribute MovMedicamento movMedicamento,
            @RequestParam(value = "dataValidade", required = false) LocalDate dataValidade,
            @RequestParam(value = "numeroLote", required = false) String numeroLote,
            RedirectAttributes ra) {

        try {
            movMedicamentoService.processarMovimentacao(movMedicamento, dataValidade, numeroLote);

            ra.addFlashAttribute("mensagemSucesso", "Movimentação registrada com sucesso!");
            return "redirect:/movMedicamentos/listar";
            
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao registrar: " + e.getMessage());
            return "redirect:/movMedicamentos/criar"; 
            
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro interno ao processar a movimentação: " + e.getMessage());
            return "redirect:/movMedicamentos/criar";
        }
    }

    // MÉTODO LISTAR CORRETO
    @GetMapping("/listar")
    public String listar(
            @PageableDefault(size = 10, sort = {"dataMovimentacao"}, direction = Sort.Direction.DESC) Pageable pageable, 
            @RequestParam(required = false) String termoBusca, 
            Model model) {
        
        Page<MovMedicamento> paginaMovimentacoes = movMedicamentoService.findAll(termoBusca, pageable);

        // ✅ ATRIBUTO CORRETO: 'movimentacoes'
        model.addAttribute("movimentacoes", paginaMovimentacoes.getContent());

        model.addAttribute("paginaMovimentacoes", paginaMovimentacoes);
        model.addAttribute("termoBusca", termoBusca);
        
        int totalPages = paginaMovimentacoes.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        
        return "movMedicamento/listaMovMedicamento";
    }

    // Criar
    @GetMapping("/criar")
    public String criarForm(Model model) {
        if (!model.containsAttribute("movMedicamento")) {
             model.addAttribute("movMedicamento", new MovMedicamento());
        }
        return "movMedicamento/formularioMovMedicamento";
    }

    // Editar
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        MovMedicamento mov = movMedicamentoService.findById(id);
        if (mov == null) {
            ra.addFlashAttribute("mensagemErro", "Movimentação não encontrada.");
            return "redirect:/movMedicamentos/listar";
        }
        model.addAttribute("movMedicamento", mov);
        return "movMedicamento/formularioMovMedicamento";
    }
}