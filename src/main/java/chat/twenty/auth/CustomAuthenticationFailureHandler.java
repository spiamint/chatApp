package chat.twenty.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * SimpleUrlAuthenticationFailureHandler 를 상속하여 SpringSecurity 일반 로그인 실패 처리
 */
@Slf4j
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler{

    /**
     * authentication 중 예외가 발생한 request 를 받아 에러메시지를 설정하고,
     * 에러메시지와 로그인정보를 request 객체에 파라미터로 설정한 후, 포워딩
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        log.info("CustomAuthenticationFailureHandler.onAuthenticationFailure() exception={} message={}", exception.getClass(), exception.getMessage());

        String loginId = request.getParameter("loginId");

        String errorMessage;

        if (exception instanceof BadCredentialsException) {
            // 자격 증명 실패
            errorMessage = "아이디 또는 비밀번호가 맞지 않습니다.";
        } else if (exception instanceof UsernameNotFoundException) {
            // 이거만 일단 로그 찍기 (로그인시 간헐적으로 발생)
            log.info("CustomAuthenticationFailureHandler.onAuthenticationFailure() exception instanceof UsernameNotFoundException, loginId={}, e.message={}", loginId, exception.getMessage());
            // ID 를 찾을 수 없음
            errorMessage = "계정이 존재하지 않습니다.";
        } else if (exception instanceof InternalAuthenticationServiceException) {
            // 내부 인증 서비스 예외 인데, 아이디가 존재하지 않아도 해당 예외가 터짐.
            errorMessage = "존재 하지 않는 ID 입니다.";
        } else {
            errorMessage = "시스템 오류로 로그인에 실패하였습니다.";
        }

        setDefaultFailureUrl("/loginFailure");
        setUseForward(true);

        // url 에 노출하지 않음.
        request.setAttribute("isLoginError", true);
        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("loginId", loginId);

        super.onAuthenticationFailure(request, response, exception);
    }
}
