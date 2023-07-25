package chat.twenty.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity // 시큐리티 필터 등록
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationFailureHandler customFailureHandler;

    @Bean
    public BCryptPasswordEncoder pwEncoder() { return new BCryptPasswordEncoder(); }

    @Bean // filter 체인을 스프링 컨테이너에 등록
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity

                .sessionManagement()
                .maximumSessions(1)
                .expiredUrl("/?needLogin=true")
                .and()
//                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and()

                .authorizeRequests()
                .antMatchers("/chat/**").authenticated()
                .antMatchers("/room/**").authenticated()
                .anyRequest().permitAll()

                .and()

                .formLogin()
                .usernameParameter("loginId")
                .loginPage("/?needLogin=true")
                .loginProcessingUrl("/loginProc")
                .defaultSuccessUrl("/")
                .failureHandler(customFailureHandler)

                .and()

                .logout()
                .permitAll()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true);

        return httpSecurity.build();
    }


}
