package chat.twenty.config;

import chat.twenty.auth.CustomAuthenticationFailureHandler;
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

    @Bean // filter 체인을 component 방식으로 스프링 컨테이너가 관리
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf().disable(); // 나중에 csrf 토큰 사용해볼것.

        httpSecurity.authorizeRequests()
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
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .clearAuthentication(true);

        return httpSecurity.build();
    }


}
