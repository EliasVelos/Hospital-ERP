package br.com.hospital.hospital.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.hospital.hospital.DTO.FuncionarioCadastroDTO;
import br.com.hospital.hospital.entity.Funcionario;
import br.com.hospital.hospital.entity.Usuario;
import br.com.hospital.hospital.repository.FuncionarioRepository;
import br.com.hospital.hospital.service.FuncionarioService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/funcionarios")
public class FuncionarioController {

@Autowired
private FuncionarioService funcionarioService;

@Autowired
private FuncionarioRepository funcionarioRepository;

    /**
     * Método auxiliar para buscar o Paciente pelo ID da sessão e validar o Role.
     * Retorna o Paciente ou null se a sessão for inválida.
     */
        private Funcionario getFuncionarioLogado(HttpSession session) {
        // Busca o ID e o Role que foram salvos no LoginController
        Integer funcionarioId = (Integer) session.getAttribute("funcionarioId");
        String userRole = (String) session.getAttribute("userRole");

        // Ponto de Validação
        if (funcionarioId == null || !"FUNCIONARIO".equals(userRole)) {
        return null; 
        }

// Busca o Paciente completo no banco
Optional<Funcionario> funcionarioOpt = funcionarioRepository.findById(funcionarioId); 
 return funcionarioOpt.orElse(null);
}

// Rota Home do Paciente (Dashboard)
 @GetMapping("/funcionarioHome")
public String funcionarioHome(HttpSession session, Model model) {
Funcionario funcionario = getFuncionarioLogado(session);

if (funcionario == null) {
return "redirect:/login"; // Redireciona se não estiver logado
}

model.addAttribute("funcionarioNome", funcionario.getNome()); 
 
return "/funcionarioHome"; // Retorna o template do dashboard bonito
}

// --------------------------------------------------------
// MÉTODOS ADMINISTRATIVOS (CRUD BÁSICO)
 // --------------------------------------------------------

// Rota Admin para exibir o formulário de CRIAÇÃO (USA DTO)
@GetMapping("/criar")
 public String criarform(Model model) {
// ⭐️ CORREÇÃO 1: Usando o nome 'funcionario' que o HTML espera ⭐️
model.addAttribute("funcionario", new FuncionarioCadastroDTO());
model.addAttribute("isNew", true); // Flag para mostrar campos de login
 return "funcionario/formularioFuncionario"; 
}

// Editar (usa a Entity Funcionário)
 @GetMapping("/editar/{id}")
public String editarForm(@PathVariable Integer id, Model model) {
Optional<Funcionario> funcionario = funcionarioService.findById(id);
 // O nome 'funcionario' está correto aqui
 model.addAttribute("funcionario", funcionario.orElse(new Funcionario())); 
model.addAttribute("isNew", false); // Flag para esconder campos de login
return "funcionario/formularioFuncionario"; 
}
 
// Salvar/Atualizar (Método POST Admin para Entity)
@PostMapping("/salvar")
public String salvar(@ModelAttribute Funcionario funcionario, RedirectAttributes ra) {
// ⚠️ ATENÇÃO: Esta rota /salvar espera a Entity Funcionario.
// Se você usar a rota /criar (que passa o DTO) para este POST, 
// o Spring pode ter problemas para fazer a conversão. 
 // Mantenha a atenção neste ponto caso surjam erros no POST.
try {
 funcionarioService.save(funcionario);
 ra.addFlashAttribute("mensagemSucesso", "Funcionário salvo com sucesso!");
return "redirect:/funcionarios/listar";
} catch (Exception e) {
ra.addFlashAttribute("mensagemErro", "Erro ao salvar funcionário: " + e.getMessage());
// Redireciona para o formulário de edição se falhar no POST de edição
if (funcionario.getIdFuncionario() != null) {
return "redirect:/funcionarios/editar/" + funcionario.getIdFuncionario();
}
return "redirect:/funcionarios/listar"; 
}
}

// Listar
@GetMapping("/listar")
public String listar(Model model) {
List<Funcionario> funcionarios = funcionarioService.findAll();
model.addAttribute("funcionarios", funcionarios);
return "funcionario/listaFuncionario";
}

// Excluir
@GetMapping("/excluir/{id}")
public String excluir(@PathVariable Integer id, RedirectAttributes ra) {
try {
funcionarioService.deleteById(id);
ra.addFlashAttribute("mensagemSucesso", "Funcionário excluído com sucesso!");
} catch (Exception e) {
ra.addFlashAttribute("mensagemErro", "Erro ao excluir: " + e.getMessage());
}
return "redirect:/funcionarios/listar";
}

@GetMapping("/perfil")
public String exibirPerfil(HttpSession session, Model model) {
    Funcionario funcionarioLogado = getFuncionarioLogado(session);
    if (funcionarioLogado == null) {
        return "redirect:/login"; 
    }
    model.addAttribute("usuario", funcionarioLogado); 
    return "funcionarioHome/meusDados";
}

// --------------------------------------------------------
// CADASTRO INTEGRADO (USUÁRIO + FUNCIONÁRIO)
// --------------------------------------------------------
// Rota GET para exibir o formulário de cadastro integrado
@GetMapping("/cadastroFuncionario") 
public String mostrarFormularioCadastro(Model model) {
// ⭐️ CORREÇÃO 2: Usando o nome 'funcionario' que o HTML espera ⭐️
model.addAttribute("funcionario", new FuncionarioCadastroDTO()); 
model.addAttribute("isNew", true); // Flag para mostrar campos de login
return "funcionario/formularioFuncionario";
}

// Rota POST para salvar o cadastro integrado
@PostMapping("/cadastrar") 
public String salvarFuncionario(@ModelAttribute("funcionario") FuncionarioCadastroDTO dto, RedirectAttributes ra) {
// ⚠️ ATENÇÃO: Se o th:object no HTML for 'funcionario', o ModelAttribute DEVE ser 'funcionario'
// Se você está usando "funcionarioDTO" como nome, ele deve ser alterado aqui também para ser consistente.
try {
funcionarioService.cadastrarNovoFuncionario(dto); 
ra.addFlashAttribute("mensagemSucesso", "Funcionário e Acesso criados com sucesso!");
return "redirect:/funcionarios/listar"; 
} catch (Exception e) {
System.err.println("Erro ao salvar: " + e.getMessage()); 
ra.addFlashAttribute("mensagemErro", "Erro ao cadastrar: " + e.getMessage());
// ⚠️ ATENÇÃO: Se falhar, é melhor retornar a view diretamente para manter os dados no formulário, 
// ou redirecionar e adicionar o DTO novamente, mas o redirecionamento apaga o 'dto'.
            return "redirect:/funcionarios/cadastroFuncionario"; 
        }
    }
}