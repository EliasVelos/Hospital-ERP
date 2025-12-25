package br.com.hospital.hospital.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Adicionado para mensagens de feedback

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections; // Adicionado para a lógica de listas

import br.com.hospital.hospital.entity.Internacao;
import br.com.hospital.hospital.service.InternacaoService;
import br.com.hospital.hospital.service.PacienteService;
import br.com.hospital.hospital.service.LeitoService;
import br.com.hospital.hospital.entity.Leito;

@Controller
@RequestMapping("/internacoes")
public class InternacaoController {

    @Autowired
    private InternacaoService internacaoService;
    
    @Autowired 
    private PacienteService pacienteService;
    
    @Autowired 
    private LeitoService leitoService;

    // --- 1. SALVAR (CREATE & UPDATE) ---
    // --- 1. SALVAR (CREATE & UPDATE) ---
@PostMapping("/salvar")
public String salvar(@ModelAttribute Internacao internacao, RedirectAttributes ra, Model model) {
    try {
        // 🚨 VALIDAÇÃO CORRIGIDA: Usa MODEL para a mensagem de erro
        if (internacaoService.existeInternacaoAtivaParaPaciente(internacao)) {
            
            // 1. Usa MODEL para passar a mensagem de erro (POIS NÃO HAVERÁ REDIRECT)
            String nomePaciente = internacao.getPaciente() != null ? internacao.getPaciente().getNomePaciente() : "Selecionado";
            model.addAttribute("mensagemErro", "Erro: O Paciente " + nomePaciente + " já possui uma internação ATIVA.");
            
            // 2. Recarregar os dados necessários para o formulário
            // O objeto 'internacao' já está no Model, mas é importante garantir as listas:
            model.addAttribute("pacientes", pacienteService.findAll());
            
            // Recarrega Leitos Disponíveis com a mesma lógica do editarForm
            List<Integer> leitosOcupadosIds = internacaoService.findLeitoIdsOcupados(); 
            
            // Somente tenta remover o leito atual se ele não for nulo, evitando NullPointerException
            if (internacao.getLeito() != null && internacao.getLeito().getIdQuarto() != null) {
                 // Remove o leito atual da lista de ocupados (se estiver lá)
                 leitosOcupadosIds.remove(internacao.getLeito().getIdQuarto());
            }
            
            model.addAttribute("leitosDisponiveis", leitoService.findAllExcludingIds(leitosOcupadosIds));

            // 3. Retorna a view diretamente para renderizar a mensagem de erro
            return "internacao/formularioInternacao";
        }
        
        // --- Lógica de SALVAR em caso de SUCESSO ---
        internacaoService.save(internacao);
        String mensagem = (internacao.getIdInternacao() == null) 
                          ? "Internação registrada com sucesso!" 
                          : "Internação atualizada com sucesso!";
        
        // Usar REDIRECTATTRIBUTES para a mensagem de sucesso (POIS HAVERÁ REDIRECT)
        ra.addFlashAttribute("mensagemSucesso", mensagem);

    } catch (Exception e) {
        // Em caso de exceção de banco de dados, volta para a lista com erro
        ra.addFlashAttribute("mensagemErro", "Erro ao salvar a internação: " + e.getMessage());
        return "redirect:/internacoes/listar";
    }
    // Sucesso sempre redireciona para a lista
    return "redirect:/internacoes/listar";
}

    // --- 2. LISTAR (READ ALL) ---
    @GetMapping("/listar")
    public String listar(Model model) {
        List<Internacao> internacoes = internacaoService.findAll();
        model.addAttribute("internacoes", internacoes);
        return "internacao/listaInternacao";
    }

    // --- 3. CRIAR (Abre o formulário vazio) ---
    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("internacao", new Internacao());
        
        // Carrega a lista de pacientes
        model.addAttribute("pacientes", pacienteService.findAll());

        // 🚨 CORREÇÃO: Lógica de Disponibilidade do Leito (Para Novo Registro)
        // 1. Obtém IDs de leitos ocupados. (Necessário criar este método no InternacaoService)
        List<Integer> leitosOcupadosIds = internacaoService.findLeitoIdsOcupados(); 
        
        // 2. Filtra todos os leitos, excluindo os ocupados.
        List<Leito> leitosDisponiveis = leitoService.findAllExcludingIds(leitosOcupadosIds); 
        
        model.addAttribute("leitosDisponiveis", leitosDisponiveis);
        
        return "internacao/formularioInternacao";
    }

    // --- 4. EXCLUIR (DELETE) ---
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id, RedirectAttributes ra) { // Adicionado RedirectAttributes
        try {
            internacaoService.deleteById(id);
            ra.addFlashAttribute("mensagemSucesso", "Internação ID " + id + " excluída com sucesso.");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao excluir a internação.");
        }
        return "redirect:/internacoes/listar";
    }

    // --- 5. EDITAR (Abre o formulário preenchido) ---
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model, RedirectAttributes ra) { // Adicionado RedirectAttributes
        Internacao internacao = internacaoService.findById(id);

        if (internacao == null) {
            ra.addFlashAttribute("mensagemErro", "Internação não encontrada.");
            return "redirect:/internacoes/listar";
        }
        
        model.addAttribute("internacao", internacao);
        model.addAttribute("pacientes", pacienteService.findAll());

        // 🚨 CORREÇÃO: Lógica de Disponibilidade do Leito (Para Edição)
        
        // 1. Obtém IDs de todos os leitos ocupados
        List<Integer> leitosOcupadosIds = internacaoService.findLeitoIdsOcupados(); 
        
        // 2. Garante que o leito sendo editado NÃO seja contado como ocupado
        if (internacao.getLeito() != null) {
            // Remove o ID do leito atual para que ele apareça na lista de seleção.
            leitosOcupadosIds.remove(internacao.getLeito().getIdQuarto()); 
        }

        // 3. Busca todos os leitos, excluindo os restantes ocupados
        List<Leito> leitosDisponiveis = leitoService.findAllExcludingIds(leitosOcupadosIds);
        
        model.addAttribute("leitosDisponiveis", leitosDisponiveis);
        
        return "internacao/formularioInternacao";
    }
    // 🚨 NOVO MÉTODO: DAR ALTA (FINALIZA INTERNAÇÃO E LIBERA LEITO) 🚨
    @GetMapping("/darAlta/{id}")
    public String darAlta(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            Internacao internacao = internacaoService.findById(id);

            if (internacao == null) {
                ra.addFlashAttribute("mensagemErro", "Internação não encontrada.");
                return "redirect:/internacoes/listar";
            }
            
            if (internacao.getDataAlta() != null) {
                ra.addFlashAttribute("mensagemErro", "O paciente já possui alta registrada.");
                return "redirect:/internacoes/listar";
            }
            
            // 1. REGISTRA A DATA DE ALTA E ATUALIZA O STATUS DA INTERNAÇÃO
            internacao.setDataAlta(LocalDateTime.now());
            internacao.setStatus("Concluída"); // 🚨 Atualiza o status
            internacaoService.save(internacao);

            // 2. ATUALIZA O STATUS DO LEITO PARA 'DISPONÍVEL'
            if (internacao.getLeito() != null) {
                Leito leito = internacao.getLeito();
                leito.setStatus("Disponível"); 
                leitoService.save(leito);
            }

            ra.addFlashAttribute("mensagemSucesso", 
                                 "Alta do paciente " + internacao.getPaciente().getNomePaciente() + 
                                 " registrada com sucesso! O Leito N° " + internacao.getLeito().getNumero() + " foi liberado.");

        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao processar a alta: " + e.getMessage());
        }
        return "redirect:/internacoes/listar";
    }
}