package br.com.hospital.hospital.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.hospital.hospital.DTO.PacienteCadastroDTO;
import br.com.hospital.hospital.entity.Consulta;
import br.com.hospital.hospital.entity.Paciente;
import br.com.hospital.hospital.repository.ConsultaRepository;
import br.com.hospital.hospital.repository.PacienteRepository;
import br.com.hospital.hospital.service.AtendimentoService;
import br.com.hospital.hospital.service.PacienteService;
import br.com.hospital.hospital.security.HospitalPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

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
    private Paciente getPacienteLogado(HospitalPrincipal user) {
        Integer pacienteId = user.getEntityId();
        String userRole = user.getRole();
        if (pacienteId == null || !"PACIENTE".equals(userRole)) {
            return null;
        }
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(pacienteId);
        return pacienteOpt.orElse(null);
    }
    @GetMapping("/pacienteHome")
    public String pacienteHome(@AuthenticationPrincipal HospitalPrincipal user, Model model) {
        Paciente paciente = getPacienteLogado(user);

        if (paciente == null) {
            return "redirect:/login";
        }

        model.addAttribute("pacienteNome", paciente.getNomePaciente());

        return "/pacienteHome";
    }
    @GetMapping("/minhasConsultas")
    public String minhasConsultas(@AuthenticationPrincipal HospitalPrincipal user, Model model) {

        Paciente pacienteLogado = getPacienteLogado(user);

        if (pacienteLogado == null) {
            return "redirect:/login";
        }

        List<Consulta> consultas = consultaRepository.findByPaciente(pacienteLogado);
        for (Consulta c : consultas) {
            atendimentoService.buscarPorIdConsulta(c.getIdConsulta())
                    .ifPresent(c::setAtendimento);
        }

        model.addAttribute("listaDeConsultas", consultas);

        return "pacienteHome/consultasPaciente";
    }
    @GetMapping("/pacientes/meusdados")
    public String meusDados(@AuthenticationPrincipal HospitalPrincipal user, Model model) {
        Paciente pacienteLogado = getPacienteLogado(user);

        if (pacienteLogado == null) {
            return "redirect:/login";
        }
        model.addAttribute("paciente", pacienteLogado);

        return "pacienteHome/dadosPaciente";
    }
    @GetMapping("/pacientes/criar")
    public String criarform(Model model) {
        model.addAttribute("pacienteDTO", new PacienteCadastroDTO());
        return "paciente/formularioPaciente";
    }
    @GetMapping("/pacientes/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        Optional<Paciente> pacienteOpt = pacienteService.findById(id);

        if (pacienteOpt.isPresent()) {
            Paciente paciente = pacienteOpt.get();
            PacienteCadastroDTO pacienteDTO = new PacienteCadastroDTO();
            BeanUtils.copyProperties(paciente, pacienteDTO);
            pacienteDTO.setPassword(null);
            model.addAttribute("pacienteDTO", pacienteDTO);
        } else {
            ra.addFlashAttribute("mensagemErro", "Paciente não encontrado.");
            return "redirect:/pacientes/listar";
        }

        return "paciente/formularioPaciente";
    }
    @PostMapping("/pacientes/salvar")
    public String salvar(@ModelAttribute("pacienteDTO") PacienteCadastroDTO dto, RedirectAttributes ra) {
        try {
             if (dto.getIdPaciente() != null) {
                pacienteService.atualizarPaciente(dto);
                ra.addFlashAttribute("mensagemSucesso", "Dados do Paciente atualizados com sucesso!");
            } else {
                pacienteService.cadastrarNovoPaciente(dto);
                ra.addFlashAttribute("mensagemSucesso", "Paciente e Acesso criados com sucesso!");
            }
            return "redirect:/pacientes/listar";
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Não foi possível salvar os dados. Confira os campos e tente novamente.");
            if (dto.getIdPaciente() != null) {
                 return "redirect:/pacientes/editar/" + dto.getIdPaciente();
            }
            return "redirect:/pacientes/criar";
        }
    }
    @GetMapping("/pacientes/listar")
    public String listar(Model model) {
        List<Paciente> pacientes = pacienteService.findAll();
        model.addAttribute("pacientes", pacientes);
        return "paciente/listaPaciente";
    }
    @PostMapping("/pacientes/excluir/{id}")
    public String excluir(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            pacienteService.deleteById(id);
            ra.addFlashAttribute("mensagemSucesso", "Paciente excluído com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao excluir paciente.");
        }
        return "redirect:/pacientes/listar";
    }
    @GetMapping("/cadastroPaciente")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("pacienteDTO", new PacienteCadastroDTO());
        return "paciente/formularioPaciente";
    }
    @PostMapping("/pacientes/cadastrar")
    public String salvarPaciente(@ModelAttribute("pacienteDTO") PacienteCadastroDTO dto, RedirectAttributes ra) {
        try {
            pacienteService.cadastrarNovoPaciente(dto);
            ra.addFlashAttribute("mensagemSucesso", "Paciente e conta de acesso cadastrados.");
            return "redirect:/pacientes/listar";
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Não foi possível salvar os dados. Confira os campos e tente novamente.");
            dto.setPassword(null);
            ra.addFlashAttribute("pacienteDTO", dto);
            return "redirect:/cadastroPaciente";
        }
    }
}
