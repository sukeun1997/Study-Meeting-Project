package com.studyforyou_retry.modules.account.setting;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.AccountService;
import com.studyforyou_retry.modules.account.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
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

    private final ModelMapper modelMapper;
    private final AccountService accountService;

    @GetMapping(PROFILE)
    private String updateProfile(@CurrentAccount Account account , Model model) {


        model.addAttribute("profile", modelMapper.map(account, Profile.class));
        model.addAttribute(account);

        return SETTINGS + PROFILE;
    }

    @PostMapping(PROFILE)
    private String updateProfile(@CurrentAccount Account account , @Valid Profile profile, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {


        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS + PROFILE;
        }

        accountService.updateProfile(account, profile);
        redirectAttributes.addFlashAttribute("message", "변경이 완료되었습니다.");
        return "redirect:/"+SETTINGS+PROFILE;
    }
}
