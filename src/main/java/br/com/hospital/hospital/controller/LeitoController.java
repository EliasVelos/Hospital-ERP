package br.com.hospital.hospital.controller;

import java.util.List;
import java.util.HashMap; // 🟢 NOVO IMPORT
import java.util.Map;     // 🟢 NOVO IMPORT

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.hospital.hospital.entity.Leito;
import br.com.hospital.hospital.service.LeitoService;

@Controller
@RequestMapping("/leitos")
public class LeitoController {

    @Autowired
    private LeitoService leitoService;

    // --- SALVAR/ATUALIZAR ---
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Leito leito, RedirectAttributes ra) {
        try {
            leitoService.save(leito);
            // Mensagem de sucesso
            String mensagem = (leito.getIdQuarto() == null) ? "Leito cadastrado com sucesso!" : "Leito atualizado com sucesso!";
            ra.addFlashAttribute("mensagemSucesso", mensagem);
        } catch (Exception e) {
            // Mensagem de erro em caso de falha no DB ou Service
            ra.addFlashAttribute("mensagemErro", "Erro ao salvar o leito: " + e.getMessage());
            // Retorna ao formulário para corrigir (mantendo os dados)
            return "leito/formularioLeito";
        }
        return "redirect:/leitos/listar";
    }

    // ----------------------------------------------------
    // 🟢 MÉTODO LISTAR CORRIGIDO PARA STATUS DINÂMICO
    // ----------------------------------------------------
    @GetMapping("/listar")
    public String listar(Model model) {
        // 1. Obtém todos os leitos cadastrados
        List<Leito> leitos = leitoService.findAll();

        // 2. Cria um mapa para armazenar o status (Ocupado/Disponível) de cada leito
        Map<Integer, String> statusLeitos = new HashMap<>();

        for (Leito leito : leitos) {
            String status;
            // 3. Usa o LeitoService para verificar o status dinâmico (consultando Internacoes Ativas)
            if (leitoService.isLeitoOcupado(leito.getIdQuarto())) {
                status = "Ocupado";
            } else {
                // Se não estiver ocupado por uma Internação Ativa, assume Disponível
                // (Você pode adicionar lógica para 'Manutenção' aqui, se o Leito tiver um campo para isso)
                status = "Disponível";
            }
            // 4. Armazena o status no mapa usando o ID do Leito como chave
            statusLeitos.put(leito.getIdQuarto(), status);
        }

        // 5. Adiciona a lista de leitos e o mapa de status ao Model
        model.addAttribute("leitos", leitos);
        model.addAttribute("statusLeitos", statusLeitos); // 🟢 CHAVE PRINCIPAL PARA O THYMELEAF

        return "leito/listaLeito";
    }

    // --- CRIAR (FORMULÁRIO VAZIO) ---
    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("leito", new Leito());
        return "leito/formularioLeito";
    }

    // --- EXCLUIR ---
    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            leitoService.deleteById(id);
            ra.addFlashAttribute("mensagemSucesso", "Leito ID " + id + " excluído com sucesso!");
        } catch (Exception e) {
            // Erro se o leito estiver em uso (chave estrangeira)
            ra.addFlashAttribute("mensagemErro", "Erro ao excluir o leito: O leito pode estar referenciado por uma Internação.");
        }
        return "redirect:/leitos/listar";
    }

    // --- EDITAR (CARREGAR FORMULÁRIO) ---
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Leito leito = leitoService.findById(id);

            if (leito == null) {
                ra.addFlashAttribute("mensagemErro", "Leito ID " + id + " não encontrado.");
                return "redirect:/leitos/listar";
            }

            model.addAttribute("leito", leito);
            return "leito/formularioLeito";

        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao buscar o leito para edição.");
            return "redirect:/leitos/listar";
        }
    }
}
