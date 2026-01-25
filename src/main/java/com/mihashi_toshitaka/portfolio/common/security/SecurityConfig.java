package com.mihashi_toshitaka.portfolio.common.security;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.DefaultSecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Autowired private CustomSuccessHandler customSuccessHandler;

  @Bean
  DefaultSecurityFilterChain securityFilterChain(
              HttpSecurity http, ObjectProvider<ClientRegistrationRepository> clientRegistrationRepository)
              throws Exception {
    http.authorizeHttpRequests(
                auth ->
                            auth
                                        // 外部向けAPIは認証不要
                                        .requestMatchers("/api/public/**")
                                        .permitAll()
                                        // プライバシーポリシーHTMLは認証不要
                                        .requestMatchers("/html/privacy.html")
                                        .permitAll()
                                        // ヘルスチェックエンドポイントは認証不要
                                        .requestMatchers("/actuator/health", "/actuator/health/**")
                                        .permitAll()
                                        // それ以外は認証が必要
                                        .anyRequest()
                                        .authenticated());
    if (clientRegistrationRepository.getIfAvailable() != null) {
      // 認証時の挙動（デフォルトログイン画面は無効）
      http.oauth2Login(
                  oauth2 ->
                              oauth2.loginPage("/oauth2/authorization/azure").successHandler(customSuccessHandler));
    }
    return http.build();
  }
}
