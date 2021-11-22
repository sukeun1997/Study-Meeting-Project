package com.studyforyou.account;


import com.studyforyou.domain.Account;
import com.studyforyou.dto.SignUpForm;
import com.studyforyou.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
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

    public static final String ACCOUNT_EMAIL_LOGIN = "account/email-login";
    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String showSignup(Model model) {

        model.addAttribute("signUpForm", new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String submitSignUp(@Valid SignUpForm signUpForm, Errors errors, Model model) {

        if (errors.hasErrors()) {
            return "account/sign-up";
        }

        Account account = accountService.processNewAccount(signUpForm);
        accountService.login(account);
        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {

        Account account = accountRepository.findByEmail(email);
        String view = "account/checked-email";

        if (account == null) {
            model.addAttribute("error", "wrong-email");
            return view;
        }

        if (!account.isValidToken(token, account)) {
            model.addAttribute("error", "wrong-token");
            return view;
        }

        accountService.completeCheckEmail(account);
        model.addAttribute("numberOfUser", accountRepository.count());
        model.addAttribute("nickname", account.getNickname());

        return view;


    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrentAccount Account account, Model model) {

        model.addAttribute("email", account.getEmail());
        return "account/check-email";
    }

    @GetMapping("/resend-confirm-email")
    public String resendEmail(@CurrentAccount Account account, Model model) {


        if (!account.canResendEmail()) {
            model.addAttribute("error", "이메일을 보내고 1시간 뒤에 다시 보낼 수 있습니다.");
            model.addAttribute("email", account.getEmail());
            return "account/check-email";
        }

        accountService.sendSignUpConfirmEmail(account);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "account/login";
    }

    @GetMapping("/profile/{nickname}")
    public String showProfile(@PathVariable String nickname, Model model, @CurrentAccount Account account) {

        Account byNickname = accountRepository.findByNickname(nickname);
        if (nickname == null) {
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다.");
        }

        model.addAttribute("account", byNickname);
        model.addAttribute("isOwner", byNickname.equals(account));
        return "account/profile";
    }

    @GetMapping("/email-login")
    public String emailLogin() {
        return ACCOUNT_EMAIL_LOGIN;
    }

    @PostMapping("/email-login")
    public String emailLogin(Model model, String email, RedirectAttributes redirectAttributes
    ) {

        Account byEmail = accountRepository.findByEmail(email);
        if (byEmail == null) {
            model.addAttribute("error", "입력하신 이메일은 존재하지 않습니다.");
            return ACCOUNT_EMAIL_LOGIN;
        }

        if (byEmail.canSendConfirmEmail()) {
            model.addAttribute("error", "이메일 로그인은 1시간 뒤에 사용할 수 있습니다.");
            return ACCOUNT_EMAIL_LOGIN;
        }

        accountService.sendConfirmEmail(byEmail);
        redirectAttributes.addFlashAttribute("message", "이메일 인증을 해 주세요");
        return "redirect:/email-login";
    }

    @GetMapping("/login-by-email")
    public String loginByEmail(Model model,String email,String token) {

        String view = "account/logged-in-by-email";
        Account byEmail = accountRepository.findByEmail(email);

        if (byEmail == null || !byEmail.isValidToken(token, byEmail)) {
            model.addAttribute("error", "로그인 할 수 없습니다.");
            return view;
        }

        accountService.login(byEmail);
        return view;
    }
}
