package br.com.hospital.hospital.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {


    @GetMapping("/funcionarioHome")
    public String funcionarioHome() {
        return "funcionarioHome";
    }

}
