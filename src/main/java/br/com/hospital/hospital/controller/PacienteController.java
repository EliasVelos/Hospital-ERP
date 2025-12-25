package br.com.hospital.hospital.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils; // Importação essencial para copiar propriedades
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.hospital.hospital.DTO.PacienteCadastroDTO;
import org.springframework.beans.BeanUtils;
import br.com.hospital.hospital.entity.Consulta;
import br.com.hospital.hospital.entity.Paciente;
import br.com.hospital.hospital.repository.ConsultaRepository;
import br.com.hospital.hospital.repository.PacienteRepository;
import br.com.hospital.hospital.service.AtendimentoService;
import br.com.hospital.hospital.service.PacienteService;
import jakarta.servlet.http.HttpSession;

@Controller
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;
    @Autowired
    private AtendimentoService atendimentoService;
    @Autowired
    private ConsultaRepository consultaRepository;
    @Autowired
    private PacienteRepository pacienteRepository;

    // --------------------------------------------------------
    // MÉTODOS AUXILIARES E DE ACESSO DO PACIENTE LOGADO
    // --------------------------------------------------------

    /**
     * Método auxiliar para buscar o Paciente pelo ID da sessão e validar o Role.
     * Retorna o Paciente ou null se a sessão for inválida.
     */
    private Paciente getPacienteLogado(HttpSession session) {
        // Busca o ID e o Role que foram salvos no LoginController
        Integer pacienteId = (Integer) session.getAttribute("pacienteId");
        String userRole = (String) session.getAttribute("userRole");

        // ⭐️ Ponto de Validação
        if (pacienteId == null || !"PACIENTE".equals(userRole)) {
            return null;
        }

        // Busca o Paciente completo no banco
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(pacienteId);
        return pacienteOpt.orElse(null);
    }

    // Rota Home do Paciente (Dashboard)
    @GetMapping("/pacienteHome")
    public String pacienteHome(HttpSession session, Model model) {
        Paciente paciente = getPacienteLogado(session);

        if (paciente == null) {
            return "redirect:/login"; // Redireciona se não estiver logado
        }

        model.addAttribute("pacienteNome", paciente.getNomePaciente());

        return "/pacienteHome"; // Retorna o template do dashboard bonito
    }

    // Rota para Minhas Consultas
    @GetMapping("/minhasConsultas")
    public String minhasConsultas(HttpSession session, Model model) {

        Paciente pacienteLogado = getPacienteLogado(session);

        if (pacienteLogado == null) {
            return "redirect:/login";
        }

        List<Consulta> consultas = consultaRepository.findByPaciente(pacienteLogado);

        // Carrega o atendimento de cada consulta
        for (Consulta c : consultas) {
            atendimentoService.buscarPorIdConsulta(c.getIdConsulta())
                    .ifPresent(c::setAtendimento);
        }

        model.addAttribute("listaDeConsultas", consultas);

        return "pacienteHome/consultasPaciente";
    }

    // Rota para Meus Dados Cadastrais
    @GetMapping("/pacientes/meusdados")
    public String meusDados(HttpSession session, Model model) {
        Paciente pacienteLogado = getPacienteLogado(session);

        if (pacienteLogado == null) {
            return "redirect:/login";
        }

        // Adiciona o objeto Paciente completo ao modelo
        model.addAttribute("paciente", pacienteLogado);

        return "pacienteHome/dadosPaciente";
    }

    // --------------------------------------------------------
    // MÉTODOS ADMINISTRATIVOS (CRUD BÁSICO)
    // --------------------------------------------------------

    // Rota Admin para exibir o formulário de CRIAÇÃO (USA DTO)
    @GetMapping("/pacientes/criar")
    public String criarform(Model model) {
        // Envia um DTO vazio para o formulário
        model.addAttribute("pacienteDTO", new PacienteCadastroDTO());
        return "paciente/formularioPaciente";
    }

    // ⭐ MÉTODO DE EDIÇÃO CORRIGIDO PARA CARREGAR TODOS OS DADOS DO PACIENTE
    @GetMapping("/pacientes/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Optional<Paciente> pacienteOpt = pacienteService.findById(id);

        if (pacienteOpt.isPresent()) {
            Paciente paciente = pacienteOpt.get();
            PacienteCadastroDTO pacienteDTO = new PacienteCadastroDTO();

            // ⭐ USO DO BEANUTILS.COPYPROPERTIES
            // Se as propriedades (campos) em Paciente e PacienteCadastroDTO tiverem 
            // os mesmos nomes e tipos, o BeanUtils faz a cópia em uma linha.
            // Isso garante que todos os dados do paciente existente sejam transferidos
            // para o DTO que o formulário espera.
            BeanUtils.copyProperties(paciente, pacienteDTO);
            
            // Tratamento de campos específicos, se necessário (ex: username, que 
            // pode estar em uma entidade separada se você estiver usando um relacionamento 1:1)
            // Assumindo que o PacienteEntity possui o campo 'username':
            // pacienteDTO.setUsername(paciente.getUsername());

            // Garante que o campo de senha seja nulo ao editar para não sobrescrever a senha 
            // acidentalmente, e o formulário deve ser configurado para só exigir a senha 
            // se o idPaciente for nulo.
            pacienteDTO.setPassword(null);

            // Adiciona o DTO PREENCHIDO ao Model, com o nome 'pacienteDTO'
            model.addAttribute("pacienteDTO", pacienteDTO);
        } else {
            // Se o ID não for encontrado
            ra.addFlashAttribute("mensagemErro", "Paciente não encontrado.");
            return "redirect:/pacientes/listar";
        }

        return "paciente/formularioPaciente";
    }

    // Salvar/Atualizar (Método POST Admin para Entity)
    // ATENÇÃO: Se o seu formulário usa PacienteCadastroDTO, este método precisa receber o DTO
    // e chamar o PacienteService para atualizar.
    @PostMapping("/pacientes/salvar")
    public String salvar(@ModelAttribute("pacienteDTO") PacienteCadastroDTO dto, RedirectAttributes ra) {
        try {
             if (dto.getIdPaciente() != null) {
                // Lógica para ATUALIZAR Paciente
                pacienteService.atualizarPaciente(dto);
                ra.addFlashAttribute("mensagemSucesso", "Dados do Paciente atualizados com sucesso!");
            } else {
                // Lógica para CADASTRAR Novo Paciente (se houver navegação direta para cá)
                pacienteService.cadastrarNovoPaciente(dto);
                ra.addFlashAttribute("mensagemSucesso", "Paciente e Acesso criados com sucesso!");
            }
            return "redirect:/pacientes/listar";
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao salvar/atualizar paciente: " + e.getMessage());
            // Se houver erro, redireciona para o formulário de onde veio
            if (dto.getIdPaciente() != null) {
                 return "redirect:/pacientes/editar/" + dto.getIdPaciente();
            }
            return "redirect:/pacientes/criar";
        }
    }


    // Listar
    @GetMapping("/pacientes/listar")
    public String listar(Model model) {
        List<Paciente> pacientes = pacienteService.findAll();
        model.addAttribute("pacientes", pacientes);
        return "paciente/listaPaciente";
    }

    // Excluir
    @GetMapping("/pacientes/excluir/{id}")
    public String excluir(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            pacienteService.deleteById(id);
            ra.addFlashAttribute("mensagemSucesso", "Paciente excluído com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao excluir paciente.");
        }
        return "redirect:/pacientes/listar";
    }


    // --------------------------------------------------------
    // CADASTRO INTEGRADO (USUÁRIO + PACIENTE) - ROTA PÚBLICA
    // --------------------------------------------------------

    // Rota GET para exibir o formulário de cadastro integrado
    @GetMapping("/cadastroPaciente")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("pacienteDTO", new PacienteCadastroDTO());
        return "paciente/formularioPaciente";
    }

    // Rota POST para salvar o cadastro integrado
    @PostMapping("/pacientes/cadastrar")
    public String salvarPaciente(@ModelAttribute("pacienteDTO") PacienteCadastroDTO dto, RedirectAttributes ra) {
        try {
            pacienteService.cadastrarNovoPaciente(dto);
            ra.addFlashAttribute("mensagemSucesso", "Paciente e Acesso criados com sucesso! Faça o login.");
            return "redirect:/login"; // Redireciona para o login após o cadastro público
        } catch (Exception e) {
            System.err.println("Erro ao salvar: " + e.getMessage());
            ra.addFlashAttribute("mensagemErro", "Erro ao cadastrar: " + e.getMessage());
            // Adiciona o DTO de volta para manter os dados preenchidos
            ra.addFlashAttribute("pacienteDTO", dto);
            return "redirect:/cadastroPaciente";
        }
    }
}