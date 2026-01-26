package io.github.hyeonqz.manager.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Spring Security 설정
 * - Form 로그인: 브라우저를 통한 Admin UI 접근
 * - HTTP Basic: Admin Client의 프로그래밍 방식 등록
 * - CSRF: Client 등록 엔드포인트만 예외 처리
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AdminServerProperties adminServerProperties;

    @Autowired
    public SecurityConfig(AdminServerProperties adminServerProperties) {
        this.adminServerProperties = adminServerProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String adminContextPath = adminServerProperties.getContextPath();

        SavedRequestAwareAuthenticationSuccessHandler successHandler =
            new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(adminContextPath + "/");

        http
            .authorizeHttpRequests(auth -> auth
                // 공개 엔드포인트: 로그인 페이지, 정적 자원, 헬스체크, 클라이언트 등록
                .requestMatchers(
                    adminContextPath + "/assets/**",
                    adminContextPath + "/login",
                    adminContextPath + "/actuator/health",
                    adminContextPath + "/actuator/info",
                    adminContextPath + "/instances",
                    adminContextPath + "/instances/**"
                ).permitAll()
                // 그 외 모든 엔드포인트는 인증 필요
                .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin
                .loginPage(adminContextPath + "/login")
                .successHandler(successHandler)
            )
            .logout(logout -> logout
                .logoutUrl(adminContextPath + "/logout")
                .logoutSuccessUrl(adminContextPath + "/login?logout")
            )
            .httpBasic(Customizer.withDefaults())
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers(
                    adminContextPath + "/instances",
                    adminContextPath + "/instances/**",
                    adminContextPath + "/actuator/**"
                )
            );

        return http.build();
    }
}
