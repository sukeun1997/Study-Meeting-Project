package com.studyforyou_retry.modules.account.setting;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.AccountRepository;
import com.studyforyou_retry.modules.account.AccountService;
import com.studyforyou_retry.modules.account.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/settings/")
public class SettingController {


    public static final String SETTINGS = "settings/";
    public static final String PROFILE = "profile";
    public static final String PASSWORD = "password";
    public static final String NOTIFICATIONS = "notifications";
    public static final String ACCOUNT = "account";

    private final ModelMapper modelMapper;
    private final AccountService accountService;
    private final PasswordValidator passwordValidator;
    private final AccountRepository accountRepository;

    @InitBinder("passwordForm")
    private void passwordValidator(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(passwordValidator);
    }

    @GetMapping(PROFILE)
    private String updateProfile(@CurrentAccount Account account, Model model) {


        model.addAttribute("profile", modelMapper.map(account, Profile.class));
        model.addAttribute(account);

        return SETTINGS + PROFILE;
    }

    @PostMapping(PROFILE)
    private String updateProfile(@CurrentAccount Account account, @Valid Profile profile, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {


        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS + PROFILE;
        }

        accountService.updateProfile(account, profile);
        redirectAttributes.addFlashAttribute("message", "변경이 완료되었습니다.");
        return "redirect:/" + SETTINGS + PROFILE;
    }

    @GetMapping(PASSWORD)
    private String updatePassword(@CurrentAccount Account account, Model model) {

        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return SETTINGS + PASSWORD;
    }


    @PostMapping(PASSWORD)
    private String updatePassword(@CurrentAccount Account account, @Valid PasswordForm passwordForm,
                                  BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS + PASSWORD;
        }

        accountService.updatePassword(account, passwordForm);
        redirectAttributes.addFlashAttribute("message", "패스워드 변경이 완료되었습니다.");

        return "redirect:/" + SETTINGS + PASSWORD;
    }

    @GetMapping(NOTIFICATIONS)
    private String updateNotifications(@CurrentAccount Account account, Model model) {

        model.addAttribute("notifications", modelMapper.map(account, Notifications.class));
        model.addAttribute(account);

        return SETTINGS + NOTIFICATIONS;
    }

    @PostMapping(NOTIFICATIONS)
    private String updateNotifications(@CurrentAccount Account account,Notifications notifications, RedirectAttributes redirectAttributes) {

        accountService.updateNotifications(account, notifications);
        redirectAttributes.addFlashAttribute("message", "알림 설정이 변경되었습니다.");
        return "redirect:/" + SETTINGS + NOTIFICATIONS;
    }

    @GetMapping(ACCOUNT)
    private String updateAccount(@CurrentAccount Account account, Model model) {

        model.addAttribute(account);
        model.addAttribute(new NicknameForm());
        return SETTINGS + ACCOUNT;
    }

    @PostMapping(ACCOUNT)
    private String updateAccount(@CurrentAccount Account account, Model model, @Valid NicknameForm nickNameForm ,
                                 BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        String nickname = nickNameForm.getNickname();

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS + ACCOUNT;
        }

        if (accountRepository.existsByNickname(nickname)) {
            bindingResult.rejectValue("nickname", "wrong value", "현재 닉네임은 사용하실 수 없습니다.");
            model.addAttribute(account);
            return SETTINGS + ACCOUNT;
        }

        accountService.updateAccount(account, nickname);
        redirectAttributes.addFlashAttribute("message", "변경이 완료되었습니다.");

        return "redirect:/" + SETTINGS + ACCOUNT;
    }

}
