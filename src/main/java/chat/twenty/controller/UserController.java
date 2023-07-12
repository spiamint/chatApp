package chat.twenty.controller;

import chat.twenty.controller.form.UserAddForm;
import chat.twenty.domain.User;
import chat.twenty.service.lower.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/add")
    public String addForm(Model model) {
        model.addAttribute("userAddForm", new UserAddForm()); // 빈 command 객체
        return "user/addForm";
    }

    @PostMapping("/user/add")
    public String addUser(@Validated @ModelAttribute UserAddForm addForm,
                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "user/addForm";
        }

        log.info("addUser(), addForm={}", addForm);

        // addForm -> User
        User user = new User(addForm.getLoginId(), addForm.getUsername(), addForm.getPassword());

        userService.save(user);

        return "redirect:/";
    }

    @ResponseBody
    @GetMapping("/user/duplicate-check")
    public boolean duplicateCheck(@RequestParam(name = "loginId", defaultValue = "") String loginId,
                                 @RequestParam(name = "username", defaultValue = "") String username) {

        String option = loginId.equals("") ? "username" : "loginId";
        String param = option.equals("username") ? username : loginId;

        log.info("duplicateCheck(), loginId = {}, username = {}, option = {}, param = {}", loginId, username, option, param);

        return userService.duplicateCheck(option, param);
    }

}
