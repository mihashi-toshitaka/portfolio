package com.mihashi_toshitaka.portfolio.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomSuccessHandler customSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                // 外部向けAPIは認証不要
                .requestMatchers("/api/public/**").permitAll()
                // プライバシーポリシーHTMLは認証不要
                .requestMatchers("/html/privacy.html").permitAll()
                // それ以外は認証が必要
                .anyRequest().authenticated())
                // 認証時の挙動
                .oauth2Login(oauth2 -> oauth2.successHandler(customSuccessHandler));
        return http.build();
    }

}
