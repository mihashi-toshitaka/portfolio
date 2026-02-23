package com.mihashi_toshitaka.portfolio.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** 認証済みユーザーのみがアクセスできるデモページコントローラー。 Azure Entra ID 認証のショーケースとして OidcUser の情報を表示する。 */
@Controller
@RequestMapping("/secured")
public class SecuredController {

  private static final Logger logger = LoggerFactory.getLogger(SecuredController.class);

  /**
   * 認証デモページを表示する。未認証の場合は Spring Security により自動的にログインページへリダイレクトされる。
   *
   * @param oidcUser 認証済み OidcUser（Spring Security が自動注入）
   * @param model Thymeleaf テンプレートに渡すモデル
   * @return 認証デモテンプレート名
   */
  @GetMapping("/demo")
  public String demo(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
    if (oidcUser != null) {
      model.addAttribute("preferredUsername", oidcUser.getPreferredUsername());
      model.addAttribute("fullName", oidcUser.getFullName());
      model.addAttribute("email", oidcUser.getEmail());
      model.addAttribute("subject", oidcUser.getSubject());
      model.addAttribute(
          "issuer", oidcUser.getIssuer() != null ? oidcUser.getIssuer().toString() : null);
      model.addAttribute("claims", oidcUser.getClaims());
      logger.debug("secured/demo accessed by: {}", oidcUser.getPreferredUsername());
    }
    return "secured-demo";
  }
}
