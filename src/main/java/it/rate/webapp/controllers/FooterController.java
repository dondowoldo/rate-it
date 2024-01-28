package it.rate.webapp.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class FooterController {

    @GetMapping("/contact")
    public String contact() {
        return "footer/contact";
    }

    @GetMapping("/developers")
    public String developers() {
        return "footer/developers";
    }

    @GetMapping("/about")
    public String about() {
        return "footer/about";
    }

    @GetMapping("/terms")
    public String terms() {
        return "footer/terms";
    }
}
