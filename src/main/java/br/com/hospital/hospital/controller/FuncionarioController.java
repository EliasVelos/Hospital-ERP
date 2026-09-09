package br.com.hospital.hospital.controller;

import br.com.hospital.hospital.DTO.FuncionarioCadastroDTO;
import br.com.hospital.hospital.entity.Funcionario;
import br.com.hospital.hospital.security.HospitalPrincipal;
import br.com.hospital.hospital.service.FuncionarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/funcionarios")
public class FuncionarioController {
    private final FuncionarioService funcionarios;

    public FuncionarioController(FuncionarioService funcionarios) {
        this.funcionarios = funcionarios;
    }

    @InitBinder("funcionario")
    void bindFields(WebDataBinder binder) {
        binder.setAllowedFields("idFuncionario", "nome", "cpfFuncionario", "cargo", "setor", "username", "password");
    }

    @GetMapping("/funcionarioHome")
    public String home() { return "redirect:/funcionarioHome"; }

    @GetMapping({"/criar", "/cadastroFuncionario"})
    public String criar(Model model) {
        model.addAttribute("funcionario", new FuncionarioCadastroDTO());
        model.addAttribute("isNew", true);
        return "funcionario/formularioFuncionario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("funcionario", find(id));
        model.addAttribute("isNew", false);
        return "funcionario/formularioFuncionario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Funcionario funcionario, RedirectAttributes redirect) {
        funcionarios.atualizarFuncionario(funcionario);
        redirect.addFlashAttribute("mensagemSucesso", "Dados do funcionário atualizados.");
        return "redirect:/funcionarios/listar";
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("funcionarios", funcionarios.findAll());
        return "funcionario/listaFuncionario";
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            funcionarios.deleteById(id);
            redirect.addFlashAttribute("mensagemSucesso", "Funcionário excluído.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            redirect.addFlashAttribute("mensagemErro", "O funcionário possui registros vinculados e não pode ser excluído.");
        }
        return "redirect:/funcionarios/listar";
    }

    @GetMapping("/perfil")
    public String perfil(@AuthenticationPrincipal HospitalPrincipal user, Model model) {
        model.addAttribute("usuario", find(user.getEntityId()));
        return "funcionarioHome/meusDados";
    }

    @PostMapping("/cadastrar")
    public String cadastrar(@ModelAttribute("funcionario") FuncionarioCadastroDTO form, RedirectAttributes redirect) {
        try {
            funcionarios.cadastrarNovoFuncionario(form);
            redirect.addFlashAttribute("mensagemSucesso", "Funcionário e conta de acesso cadastrados.");
        } catch (IllegalArgumentException | org.springframework.dao.DataIntegrityViolationException e) {
            redirect.addFlashAttribute("mensagemErro", "Confira os dados, o login e a senha de pelo menos 12 caracteres.");
            return "redirect:/funcionarios/criar";
        }
        return "redirect:/funcionarios/listar";
    }

    private Funcionario find(Integer id) {
        return funcionarios.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
