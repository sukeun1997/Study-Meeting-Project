package com.studyforyou_retry.modules.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    public static final String ACCOUNT_CHECKED_EMAIL = "account/checked-email";
    public static final String ACCOUNT_CHECK_EMAIL = "account/check-email";
    public static final String ACCOUNT_SIGN_UP = "account/sign-up";

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
        return ACCOUNT_SIGN_UP;
    }

    @PostMapping("/sign-up")
    private String postSignUpPage(@Valid SignUpForm signUpForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ACCOUNT_SIGN_UP;
        }

        accountService.createNewAccount(signUpForm);
        return "redirect:/";
    }

    @GetMapping("check-email-token")
    private String checkEmailToken(String token, String email, Model model) {

        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            model.addAttribute("error", "Wrong Email");
            return ACCOUNT_CHECKED_EMAIL;
        }

        if (!account.getEmailCheckToken().equals(token)) {
            model.addAttribute("error", "Wrong Token");
            return ACCOUNT_CHECKED_EMAIL;
        }
        accountService.verifiedEmailToken(account);
        long count = accountRepository.countByEmailVerified(true);
        model.addAttribute("numberOfUser", count);
        model.addAttribute("nickname", account.getNickname());

        return ACCOUNT_CHECKED_EMAIL;
    }

    @GetMapping("/login")
    private String login() {
        return "account/login";
    }

    @GetMapping("/check-email")
    private String checkEmail(String email, Model model) {

        model.addAttribute("email", email);
        return ACCOUNT_CHECK_EMAIL;
    }

    @GetMapping("/resend-confirm-email")
    private String resendConfirmEmail(@CurrentAccount Account account, Model model) {

        Account byEmail = accountRepository.findByEmail(account.getEmail());

        if (!byEmail.canResendEmail()) {
            model.addAttribute("error", "인증 이메일은 1시간 마다 보낼 수 있습니다.");
            model.addAttribute("email", byEmail.getEmail());
            return ACCOUNT_CHECK_EMAIL;
        }

        accountService.sendConfirmEmail(byEmail);
        return "redirect:/";
    }

}
