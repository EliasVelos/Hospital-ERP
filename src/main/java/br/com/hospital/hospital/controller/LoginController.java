package br.com.hospital.hospital.controller;

import br.com.hospital.hospital.security.HospitalPrincipal;
import br.com.hospital.hospital.service.ConsultaService;
import br.com.hospital.hospital.service.InternacaoService;
import br.com.hospital.hospital.service.LeitoService;
import br.com.hospital.hospital.service.MedicamentoService;
import br.com.hospital.hospital.repository.MedicoRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    private final InternacaoService internacoes;
    private final LeitoService leitos;
    private final ConsultaService consultas;
    private final MedicamentoService medicamentos;
    private final MedicoRepository medicos;

    public LoginController(InternacaoService internacoes, LeitoService leitos, ConsultaService consultas,
            MedicamentoService medicamentos, MedicoRepository medicos) {
        this.internacoes = internacoes;
        this.leitos = leitos;
        this.consultas = consultas;
        this.medicamentos = medicamentos;
        this.medicos = medicos;
    }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping({"/", "/dashboard"})
    public String home(@AuthenticationPrincipal HospitalPrincipal user) {
        return "redirect:" + user.homeUrl();
    }

    @GetMapping("/adminHome")
    public String adminHome(Model model, @AuthenticationPrincipal HospitalPrincipal user) {
        model.addAttribute("nomeUsuarioLogado", user.getUsername());
        model.addAttribute("totalInternados", internacoes.contarInternacoesAtivas());
        model.addAttribute("totalLeitosDisponiveis", leitos.countLeitosDisponiveis());
        model.addAttribute("totalConsultasHoje", consultas.contarConsultasDeHoje());
        model.addAttribute("totalAlertasEstoque", medicamentos.contarAlertasEstoqueBaixo());
        model.addAttribute("totalMedicosAtivos", medicos.count());
        return "adminHome";
    }

    @GetMapping("/medicoHome")
    public String medicoHome(Model model, @AuthenticationPrincipal HospitalPrincipal user) {
        model.addAttribute("nomeMedicoLogado", user.getUsername());
        model.addAttribute("listaProximasConsultas", consultas.buscarDoMedicoHoje(user.getEntityId()));
        return "medicoHome";
    }
}
