package chat.twenty.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Http 요청 로그
 */
@Slf4j
public class HttpLogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        String method = request.getMethod();
        String session = request.getSession(false) == null ? "null" : request.getSession().getId();

        if (queryString == null) {
            log.info("HTTP REQUEST : {} {} session = {}", method, requestURI, session);
        } else {
            log.info("HTTP REQUEST : {} {}?{} session = {}", method, requestURI, queryString, session);
        }

        return true;
    }
}
