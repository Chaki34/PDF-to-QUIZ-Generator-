package PDFquizAI.com.PDFquizAI.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // ✅ Your custom OAuth success handler
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // ❌ Disable CSRF for API + form hybrid apps (you can enable later if needed)
                .csrf(csrf -> csrf.disable())

                // 🔐 AUTH RULES
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/auth",
                                "/gateway",
                                "/oauth2/**",
                                "/login/**",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        .anyRequest().authenticated()
                )

                // 🔐 FORM LOGIN (your custom login page)
                .formLogin(form -> form
                        .loginPage("/auth")
                        .permitAll()
                )

                // 🔥 GOOGLE OAUTH LOGIN (IMPORTANT FIX HERE)
                .oauth2Login(oauth -> oauth
                        .loginPage("/auth")

                        // ✅ THIS IS THE ONLY FLOW YOU SHOULD USE
                        .successHandler(oAuth2SuccessHandler)
                )

                // 🚪 LOGOUT
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}