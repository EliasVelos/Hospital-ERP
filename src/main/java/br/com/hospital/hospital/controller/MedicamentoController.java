package br.com.hospital.hospital.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // 🚨 ESTE É O IMPORT CORRETO
import org.springframework.data.domain.Sort;     // Necessário para Sort.Direction.ASC
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.hospital.hospital.entity.Medicamento;
import br.com.hospital.hospital.service.MedicamentoService;

@Controller
@RequestMapping("/medicamentos")
public class MedicamentoController {

    @Autowired
    private MedicamentoService medicamentoService;

    // Salvar
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Medicamento medicamento) {
        medicamentoService.save(medicamento);
        return "redirect:/medicamentos/listar";
    }

    // Listar
    @GetMapping("/listar")
    public String listar(
            @PageableDefault(size = 10, sort = {"nomeMedicamento"}, direction = Sort.Direction.ASC) Pageable pageable, 
            @RequestParam(required = false) String termoBusca, 
            Model model) {
        
        Page<Medicamento> paginaMedicamentos;
 
        // 🚨 CORREÇÃO DA LÓGICA DE BUSCA AQUI
        if (termoBusca != null && !termoBusca.trim().isEmpty()) {
            // 1. CHAMA MÉTODO PERSONALIZADO DE BUSCA (você precisa garantir que ele exista no Service)
            // Sugestão de nome: findByNomeMedicamentoContainingIgnoreCase
            paginaMedicamentos = medicamentoService.findByNomeMedicamentoContainingIgnoreCase(termoBusca, pageable);
            
        } else {
            // 2. CHAMA O MÉTODO PADRÃO DE PAGINAÇÃO (deve ser: findAll(Pageable))
            paginaMedicamentos = medicamentoService.findAll(pageable);
        }
        // 🚨 FIM DA CORREÇÃO
 
        // Adiciona a lista ao modelo (o Thymeleaf usa esta variável para iterar)
        model.addAttribute("medicamentos", paginaMedicamentos.getContent());
        
        // Adiciona o objeto Page para a paginação no Thymeleaf
        model.addAttribute("paginaMedicamentos", paginaMedicamentos);
        
        // Devolve o termo para preencher o campo de busca
        model.addAttribute("termoBusca", termoBusca); 
        
        // CRUCIAL: Adiciona o Service para que o Thymeleaf possa chamar os métodos de Estoque/Validade
        model.addAttribute("medicamentoService", medicamentoService);
 
        // Lógica para gerar os números de página para o Thymeleaf
        int totalPages = paginaMedicamentos.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed()
                .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        
        // 🚨 ATENÇÃO: O nome do template deve ser ajustado para listaMedicamento.html ou listarMedicamentos.html
        return "medicamento/listaMedicamento"; // Ajustei para o nome que você usou anteriormente
    }

    // Criar
    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("medicamento", new Medicamento());
        return "medicamento/formularioMedicamento";
    }

    // Excluir
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        medicamentoService.desativar(id);
        return "redirect:/medicamentos/listar";
    }

    // Editar
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        Medicamento medicamento = medicamentoService.findById(id);
        model.addAttribute("medicamento", medicamento);
        return "medicamento/formularioMedicamento";
    }
}
