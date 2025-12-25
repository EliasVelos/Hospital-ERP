package br.com.hospital.hospital.controller;

import br.com.hospital.hospital.entity.Usuario;
import br.com.hospital.hospital.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // 1. LISTAR TODOS OS USUÁRIOS
    @GetMapping("/listar")
    public String listar(Model model) {
        List<Usuario> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);
        return "usuario/listaUsuario"; // Template: listaUsuario.html
    }

    // 2. EXIBIR FORMULÁRIO DE CADASTRO/EDIÇÃO (GET)
    @GetMapping("/formulario")
    public String formulario(Model model, @RequestParam(required = false) Integer id) {
        
        Usuario usuario = new Usuario();
        boolean isNew = true;

        if (id != null) {
            Optional<Usuario> usuarioOpt = usuarioService.findById(id);
            if (usuarioOpt.isPresent()) {
                usuario = usuarioOpt.get();
                isNew = false;
            }
        }
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("isNew", isNew);
        // Lista de perfis para o Select (opcional, pode ser fixo no HTML)
        model.addAttribute("perfis", new String[]{"ADMIN", "FUNCIONARIO", "MEDICO", "PACIENTE"});
        
        return "usuario/formularioUsuario"; // Template: formularioUsuario.html
    }

    // 3. SALVAR (CADASTRO OU EDIÇÃO) (POST)
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario, RedirectAttributes ra) {
        try {
            // Se estiver editando, não permita alterar o username (login)
            if (usuario.getId() != null) {
                usuarioService.findById(usuario.getId()).ifPresent(u -> usuario.setUsername(u.getUsername()));
            }
            
            usuarioService.save(usuario);
            ra.addFlashAttribute("mensagemSucesso", "Usuário salvo/atualizado com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao salvar usuário: " + e.getMessage());
            // Se for erro de duplicidade (unique=true no username), redireciona para o form
            return "redirect:/admin/usuarios/formulario?id=" + (usuario.getId() != null ? usuario.getId() : "");
        }
        return "redirect:/admin/usuarios/listar";
    }

    // 4. EXCLUIR
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            usuarioService.deleteById(id);
            ra.addFlashAttribute("mensagemSucesso", "Usuário excluído com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao excluir: Verifique se há entidades relacionadas (Paciente, Médico, Funcionário) associadas a este usuário.");
        }
        return "redirect:/admin/usuarios/listar";
    }
}