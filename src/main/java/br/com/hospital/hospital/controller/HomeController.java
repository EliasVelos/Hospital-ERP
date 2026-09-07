package br.com.hospital.hospital.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {


    @GetMapping("/funcionarioHome")
    public String funcionarioHome(org.springframework.ui.Model model,
            @org.springframework.security.core.annotation.AuthenticationPrincipal br.com.hospital.hospital.security.HospitalPrincipal user) {
        model.addAttribute("nomeUsuarioLogado", user.getUsername());
        return "funcionarioHome";
    }

}
