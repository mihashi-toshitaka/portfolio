package com.mihashi_toshitaka.portfolio.common.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(
              HttpServletRequest request, HttpServletResponse response, Authentication authentication)
              throws IOException, ServletException {

    // セッションに保存された元のリクエストURIを取得
    SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);

    // 優先順位1: viewid がある場合
    if (savedRequest != null && savedRequest.getParameterMap() != null) {
      String[] viewid = savedRequest.getParameterMap().get("viewid");
      if (viewid != null) {
        response.sendRedirect("/viewer/" + viewid[0]);
        return;
      }
    }

    // 優先順位2: 元のURLがある場合
    if (savedRequest != null) {
      String targetUrl = savedRequest.getRedirectUrl();
      try {
        URI uri = new URI(targetUrl);
        String targetPath = uri.getPath();
        String contextPath = request.getContextPath() + (targetPath.endsWith("/") ? "/" : "");
        // サブコンテキストパスがある場合はそちらに遷移
        if (!Objects.equals(targetPath, contextPath)) {
          response.sendRedirect(targetUrl);
          return;
        }
      } catch (URISyntaxException e) {
        throw new RuntimeException(e);
      }
    }

    // 優先順位3: 通常は /menu に遷移
    response.sendRedirect("/menu");
  }
}
