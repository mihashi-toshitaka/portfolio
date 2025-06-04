package com.mihashi_toshitaka.portfolio.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

    @GetMapping("")
    public String login(HttpSession session, Model model) {

        logger.debug(session.getId());

        SecurityContext ctx = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        if (ctx != null && ctx.getAuthentication().getPrincipal() instanceof OidcUser p) {
            String loginId = p.getPreferredUsername();
            logger.debug(loginId);
        }

        return "menu";
    }

}
