package com.studyforyou_retry.modules.account;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final SignUpFormValidator signUpFormValidator;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm")
    private void signUpFormValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    private String getSignUpPage(Model model) {
        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    private String postSignUpPage(@Valid SignUpForm signUpForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "account/sign-up";
        }

        accountService.createNewAccount(signUpForm);
        return "redirect:/";
    }

    @GetMapping("check-email-token")
    private String checkEmailToken(String token, String email, Model model) {

        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            model.addAttribute("error", "Wrong Email");
        }

        if (!account.getEmailCheckToken().equals(token)) {
            model.addAttribute("error", "Wrong Token");
        }
        accountService.verifiedEmailToken(account);
        long count = accountRepository.countByEmailVerified(true);
        model.addAttribute("numberOfUser", count);
        model.addAttribute("nickname", account.getNickname());

        return "account/checked-email";
    }

    @GetMapping("/login")
    private String login() {
        return "account/login";
    }
}
