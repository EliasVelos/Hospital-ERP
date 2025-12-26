package br.com.hospital.hospital.controller;

import br.com.hospital.hospital.service.UsuarioService; // <--- Ajuste o pacote para onde você criou essa classe
import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.hospital.hospital.entity.Usuario;
import br.com.hospital.hospital.entity.Paciente;
import br.com.hospital.hospital.entity.Medico;
import br.com.hospital.hospital.entity.Consulta;
import br.com.hospital.hospital.entity.Funcionario;
import br.com.hospital.hospital.service.ConsultaService;
import br.com.hospital.hospital.service.InternacaoService;
import br.com.hospital.hospital.service.LeitoService;
import br.com.hospital.hospital.service.MedicamentoService;
import br.com.hospital.hospital.service.UsuarioService;
import br.com.hospital.hospital.repository.PacienteRepository;
import br.com.hospital.hospital.repository.MedicoRepository;
import br.com.hospital.hospital.repository.FuncionarioRepository;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private PacienteRepository pacienteRepository;
    @Autowired
    private MedicoRepository medicoRepository;
    @Autowired
    private FuncionarioRepository funcionarioRepository;
    @Autowired
    private InternacaoService internacaoService;
    @Autowired
    private LeitoService leitoService;
    @Autowired
    private ConsultaService consultaService;
    @Autowired
    private MedicamentoService medicamentoService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username,
            @RequestParam String password,
            @RequestParam String tipoUsuario,
            @RequestParam(required = false) String cpf, // CPF agora é opcional
            HttpSession session) {

        try {
            // Usa o serviço que carrega as relações automaticamente
            Usuario usuario = usuarioService.fazerLoginComTipo(username, password, tipoUsuario);

            if (usuario == null) {
                return "redirect:/login?error=credenciais_invalidas";
            }

            // NOVO: Validação do CPF apenas para tipos que precisam
            if (tipoUsuario.equals("PACIENTE") || tipoUsuario.equals("MEDICO") || tipoUsuario.equals("FUNCIONARIO")) {
                if (cpf == null || cpf.trim().isEmpty()) {
                    return "redirect:/login?error=cpf_incorreto";
                }

                boolean cpfValido = validarCPF(usuario, cpf, tipoUsuario);
                if (!cpfValido) {
                    return "redirect:/login?error=cpf_incorreto";
                }
            }

            // Obtém o entidade_id automaticamente
            Integer entidadeId = usuario.getEntidadeId();

            String homeUrl = "";
            session.setAttribute("usuarioLogado", usuario);
            session.setAttribute("entidadeId", entidadeId);

            // Define a página home baseada no tipo de usuário
            switch (tipoUsuario.toUpperCase()) {
                case "ADMIN":
                    homeUrl = "/adminHome";
                    session.setAttribute("adminId", usuario.getId());
                    break;
                case "MEDICO":
                    homeUrl = "/medicoHome";
                    session.setAttribute("medicoId", entidadeId);
                    session.setAttribute("medicoLogado", usuario.getMedico());
                    break;
                case "FUNCIONARIO":
                    homeUrl = "/funcionarioHome";
                    session.setAttribute("funcionarioId", entidadeId);
                    session.setAttribute("funcionarioLogado", usuario.getFuncionario());
                    break;
                case "PACIENTE":
                    homeUrl = "/pacienteHome";
                    session.setAttribute("pacienteId", entidadeId);
                    session.setAttribute("pacienteLogado", usuario.getPaciente());
                    break;
                default:
                    return "redirect:/login?error=tipo_invalido";
            }

            session.setAttribute("userHomeUrl", homeUrl);
            session.setAttribute("userRole", tipoUsuario);

            return "redirect:" + homeUrl;

        } catch (RuntimeException e) {
            // Captura erros específicos do serviço
            if (e.getMessage().contains("Tipo de usuário incorreto")) {
                return "redirect:/login?error=tipo_incorreto";
            } else if (e.getMessage().contains("Perfil não encontrado")) {
                return "redirect:/login?error=perfil_nao_encontrado";
            }
            return "redirect:/login?error=erro_sistema";
        } catch (Exception e) {
            return "redirect:/login?error=erro_sistema";
        }
    }

    // Método de validação do CPF (mantém o mesmo)
    private boolean validarCPF(Usuario usuario, String cpfInformado, String tipoUsuario) {
        // Remove caracteres não numéricos do CPF informado
        String cpfLimpo = cpfInformado.replaceAll("[^0-9]", "");

        switch (tipoUsuario.toUpperCase()) {
            case "PACIENTE":
                if (usuario.getPaciente() != null) {
                    String cpfPaciente = usuario.getPaciente().getCpfPaciente().replaceAll("[^0-9]", "");
                    return cpfPaciente.equals(cpfLimpo);
                }
                break;

            case "MEDICO":
                if (usuario.getMedico() != null) {
                    String cpfMedico = usuario.getMedico().getCpfMedico().replaceAll("[^0-9]", "");
                    return cpfMedico.equals(cpfLimpo);
                }
                break;

            case "FUNCIONARIO":
                if (usuario.getFuncionario() != null) {
                    String cpfFuncionario = usuario.getFuncionario().getCpfFuncionario().replaceAll("[^0-9]", "");
                    return cpfFuncionario.equals(cpfLimpo);
                }
                break;
        }

        return false;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }

    @GetMapping("/adminHome")
    public String adminHome(Model model, Principal principal) { // <--- Mudou aqui

        // 1. PEGAR O NOME DO USUÁRIO (Alternativa Simples)
        String nome = "Admin"; // Valor padrão caso algo falhe
        if (principal != null) {
            nome = principal.getName(); // Pega o login (email ou username)
        }
        model.addAttribute("nomeUsuarioLogado", nome);

        // --- O RESTO CONTINUA IGUAL (SEUS KPIs) ---

        // 2. BUSCAR OS DADOS (Certifique-se que seus Services estão injetados com
        // @Autowired acima)
        int internados = internacaoService.contarInternacoesAtivas();
        Long leitosLivres = leitoService.countLeitosOcupados();
        int consultasHoje = consultaService.contarConsultasDeHoje();
        int alertasEstoque = medicamentoService.contarAlertasEstoqueBaixo();

        // ⭐ LINHA ADICIONADA: Conta o total de médicos no banco
        long totalMedicos = medicoRepository.count();

        // 3. MANDAR PRO HTML
        model.addAttribute("totalInternados", internados);
        model.addAttribute("totalLeitosDisponiveis", leitosLivres);
        model.addAttribute("totalConsultasHoje", consultasHoje);
        model.addAttribute("totalAlertasEstoque", alertasEstoque);

        // ⭐ LINHA ADICIONADA: Envia o valor para o HTML
        model.addAttribute("totalMedicosAtivos", totalMedicos);

        return "adminHome"; // O nome do seu arquivo HTML
    }

    @GetMapping("/medicoHome")
    public String medicoHome(Model model, HttpSession session) {

        List<Consulta> proximas = consultaService.buscarProximasDoDia();
        model.addAttribute("listaProximasConsultas", proximas);

        return "medicoHome";
    }
}