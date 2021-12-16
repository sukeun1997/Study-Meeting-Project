package com.studyforyou_retry.modules.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    public static final String ACCOUNT_CHECKED_EMAIL = "account/checked-email";
    public static final String ACCOUNT_CHECK_EMAIL = "account/check-email";
    public static final String ACCOUNT_SIGN_UP = "account/sign-up";
    public static final String ACCOUNT_PROFILE = "account/profile";
    public static final String ACCOUNT_EMAIL_LOGIN = "account/email-login";
    public static final String ACCOUNT_CHECK_LOGIN_EMAIL = "account/check-login-email";
    public static final String ACCOUNT_LOGGED_IN_BY_EMAIL = "account/logged-in-by-email";

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

        if (!account.canResendEmail()) {
            model.addAttribute("error", "인증 이메일은 1시간 마다 보낼 수 있습니다.");
            model.addAttribute("email", account.getEmail());
            return ACCOUNT_CHECK_EMAIL;
        }

        accountService.sendConfirmEmail(account);
        return "redirect:/";
    }

    @GetMapping("/profile/{nickname}")
    private String profileView(@CurrentAccount Account account, Model model, @PathVariable String nickname) {

        Account byNickname = accountRepository.findByNickname(nickname);
        model.addAttribute(account);
        model.addAttribute("isOwner", byNickname.equals(account));
        return ACCOUNT_PROFILE;
    }


    @GetMapping("/email-login")
    private String emailLoginForm() {
        return ACCOUNT_EMAIL_LOGIN;
    }

    @PostMapping("/email-login")
    private String emailLogin(String email, Model model) {

        Account byEmail = accountRepository.findByEmail(email);
        model.addAttribute("email", email);
        if (byEmail == null) {
            model.addAttribute("error", "입력하신 이메일에 해당하는 계정이 없습니다.");
            return ACCOUNT_CHECK_LOGIN_EMAIL;
        }

        if (!byEmail.canResendEmail()) {
            model.addAttribute("error", "이메일 로그인은 1시간 마다 가능합니다.");
            return ACCOUNT_CHECK_LOGIN_EMAIL;
        }

        accountService.sendLoginEmail(byEmail);
        return ACCOUNT_CHECK_LOGIN_EMAIL;
    }

    @GetMapping("/logged-in-by-email")
    private String emailLoginCheck(String email, String token, Model model) {
        Account byEmail = accountRepository.findByEmail(email);

        if (byEmail == null || !byEmail.getEmailCheckToken().equals(token)) {
            model.addAttribute("error", "이메일 로그인 링크가 정확하지 않습니다.");
            return ACCOUNT_LOGGED_IN_BY_EMAIL;
        }

        accountService.login(byEmail);
        return ACCOUNT_LOGGED_IN_BY_EMAIL;
    }
}
