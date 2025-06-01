package com.mihashi_toshitaka.portfolio.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/menu")
public class MenuController {

    @GetMapping("")
    public String login(HttpSession session, Model model) {

        System.out.println(session.getId());

        SecurityContext ctx = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        if (ctx != null && ctx.getAuthentication().getPrincipal() instanceof OidcUser p) {
            String loginId = p.getPreferredUsername();
            System.out.println(loginId);
        }

        return "menu";
    }

}
