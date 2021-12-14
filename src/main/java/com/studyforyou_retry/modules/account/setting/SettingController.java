package com.studyforyou_retry.modules.account.setting;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/settings/")
public class SettingController {


    public static final String SETTINGS = "settings/";
    public static final String PROFILE = "profile";

    @GetMapping(PROFILE)
    private String updateProfile(@CurrentAccount Account account , Model model) {

        model.addAttribute("profile",new ProfileForm());
        model.addAttribute(account);

        return SETTINGS + PROFILE;
    }
}
