package com.mihashi_toshitaka.portfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping("")
    public String login(Model model) {
        return "login";
    }

    @PostMapping("")
    public String login(@RequestParam String username, @RequestParam String password,
            RedirectAttributes redirectAttributes) {
        return "redirect:/menu";
    }

}
