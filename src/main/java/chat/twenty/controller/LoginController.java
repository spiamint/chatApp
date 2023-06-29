package chat.twenty.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class LoginController {

    @RequestMapping("/loginFailure")
    public String loginFailure(HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        // 로그인 실패시, 포워딩을 통해 들어옴
        String loginId = (String) request.getAttribute("loginId");
        String errorMessage= (String) request.getAttribute("errorMessage");
        boolean isLoginError = (boolean) request.getAttribute("isLoginError");

        log.info("loginFailure(), isLoginError = {}, loginId = {}, errorMessage = {}", isLoginError, loginId, errorMessage);

        redirectAttributes.addFlashAttribute("isLoginError", isLoginError);
        redirectAttributes.addFlashAttribute("loginId", loginId);
        redirectAttributes.addFlashAttribute("loginErrorMessage", errorMessage);

        return "redirect:/?needLogin=true";
    }
}
