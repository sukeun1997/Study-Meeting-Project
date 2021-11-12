package com.studyforyou.settings;

import com.studyforyou.account.AccountService;
import com.studyforyou.account.CurrentUser;
import com.studyforyou.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    public static final String SETTINGS_PROFILE = "settings/profile";
    private final AccountService accountService;

    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model) {

        model.addAttribute(account);
        Profile profile = Profile.createProfile(account);
        model.addAttribute(profile);
        return SETTINGS_PROFILE;
    }

    @PostMapping("/settings/profile")
    public String profileUpdate(@CurrentUser Account account, @Valid Profile profile, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PROFILE;
        }

        accountService.completeProfileUpdate(account,profile);
        model.addAttribute(account);
        return "redirect:/profile/"+account.getNickname();
    }
}
