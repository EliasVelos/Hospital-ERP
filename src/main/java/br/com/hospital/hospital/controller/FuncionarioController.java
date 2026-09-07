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
import br.com.hospital.hospital.security.HospitalPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Controller
@RequestMapping("/funcionarios")
public class FuncionarioController {

@Autowired
private FuncionarioService funcionarioService;

@Autowired
private FuncionarioRepository funcionarioRepository;
        private Funcionario getFuncionarioLogado(HospitalPrincipal user) {
        Integer funcionarioId = user.getEntityId();
        String userRole = user.getRole();
        if (funcionarioId == null || !"FUNCIONARIO".equals(userRole)) {
        return null;
        }
Optional<Funcionario> funcionarioOpt = funcionarioRepository.findById(funcionarioId);
 return funcionarioOpt.orElse(null);
}
 @GetMapping("/funcionarioHome")
public String funcionarioHome(@AuthenticationPrincipal HospitalPrincipal user, Model model) {
Funcionario funcionario = getFuncionarioLogado(user);

if (funcionario == null) {
return "redirect:/login";
}

model.addAttribute("funcionarioNome", funcionario.getNome());

return "/funcionarioHome";
}
@GetMapping("/criar")
 public String criarform(Model model) {
model.addAttribute("funcionario", new FuncionarioCadastroDTO());
model.addAttribute("isNew", true);
 return "funcionario/formularioFuncionario";
}
 @GetMapping("/editar/{id}")
public String editarForm(@PathVariable Integer id, Model model) {
Optional<Funcionario> funcionario = funcionarioService.findById(id);
 model.addAttribute("funcionario", funcionario.orElse(new Funcionario()));
model.addAttribute("isNew", false);
return "funcionario/formularioFuncionario";
}
@PostMapping("/salvar")
public String salvar(@ModelAttribute Funcionario funcionario, RedirectAttributes ra) {
try {
 funcionarioService.atualizarFuncionario(funcionario);
 ra.addFlashAttribute("mensagemSucesso", "Funcionário salvo com sucesso!");
return "redirect:/funcionarios/listar";
} catch (Exception e) {
ra.addFlashAttribute("mensagemErro", "Não foi possível salvar os dados. Confira os campos e tente novamente.");
if (funcionario.getIdFuncionario() != null) {
return "redirect:/funcionarios/editar/" + funcionario.getIdFuncionario();
}
return "redirect:/funcionarios/listar";
}
}
@GetMapping("/listar")
public String listar(Model model) {
List<Funcionario> funcionarios = funcionarioService.findAll();
model.addAttribute("funcionarios", funcionarios);
return "funcionario/listaFuncionario";
}
@PostMapping("/excluir/{id}")
public String excluir(@PathVariable Integer id, RedirectAttributes ra) {
try {
funcionarioService.deleteById(id);
ra.addFlashAttribute("mensagemSucesso", "Funcionário excluído com sucesso!");
} catch (Exception e) {
ra.addFlashAttribute("mensagemErro", "Não foi possível salvar os dados. Confira os campos e tente novamente.");
}
return "redirect:/funcionarios/listar";
}

@GetMapping("/perfil")
public String exibirPerfil(@AuthenticationPrincipal HospitalPrincipal user, Model model) {
    Funcionario funcionarioLogado = getFuncionarioLogado(user);
    if (funcionarioLogado == null) {
        return "redirect:/login";
    }
    model.addAttribute("usuario", funcionarioLogado);
    return "funcionarioHome/meusDados";
}
@GetMapping("/cadastroFuncionario")
public String mostrarFormularioCadastro(Model model) {
model.addAttribute("funcionario", new FuncionarioCadastroDTO());
model.addAttribute("isNew", true);
return "funcionario/formularioFuncionario";
}
@PostMapping("/cadastrar")
public String salvarFuncionario(@ModelAttribute("funcionario") FuncionarioCadastroDTO dto, RedirectAttributes ra) {
try {
funcionarioService.cadastrarNovoFuncionario(dto);
ra.addFlashAttribute("mensagemSucesso", "Funcionário e Acesso criados com sucesso!");
return "redirect:/funcionarios/listar";
} catch (Exception e) {
ra.addFlashAttribute("mensagemErro", "Não foi possível salvar os dados. Confira os campos e tente novamente.");
            return "redirect:/funcionarios/cadastroFuncionario";
        }
    }
}
