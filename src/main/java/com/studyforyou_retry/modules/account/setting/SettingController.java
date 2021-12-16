package com.studyforyou_retry.modules.account.setting;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.AccountRepository;
import com.studyforyou_retry.modules.account.AccountService;
import com.studyforyou_retry.modules.account.CurrentAccount;
import com.studyforyou_retry.modules.tags.Tag;
import com.studyforyou_retry.modules.tags.TagForm;
import com.studyforyou_retry.modules.tags.TagRepository;
import com.studyforyou_retry.modules.tags.TagService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/settings/")
public class SettingController {


    public static final String SETTINGS = "settings/";
    public static final String PROFILE = "profile";
    public static final String PASSWORD = "password";
    public static final String NOTIFICATIONS = "notifications";
    public static final String ACCOUNT = "account";
    public static final String TAGS = "tags";

    private final ModelMapper modelMapper;
    private final AccountService accountService;
    private final PasswordValidator passwordValidator;
    private final ObjectMapper objectMapper;
    private final AccountRepository accountRepository;
    private final TagRepository tagRepository;
    private final TagService tagService;

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

    @GetMapping(TAGS)
    private String updateTags(@CurrentAccount Account account, Model model) throws JsonProcessingException {

        model.addAttribute(account);

        Set<String> whitelist = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toSet());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(whitelist));

        Set<String> tags = accountService.findTags(account);
        model.addAttribute("tags", tags);

        return SETTINGS + TAGS;
    }

    @PostMapping(TAGS+"/add")
    @ResponseBody
    private ResponseEntity updateTags(@CurrentAccount Account account, @RequestBody TagForm tagForm) {

        Tag tag = tagService.getTag(tagForm.getTagTitle());
        accountService.updateTags(account, tag);

        return ResponseEntity.ok().build();
    }


    @PostMapping(TAGS+"/remove")
    @ResponseBody
    private ResponseEntity removeTags(@CurrentAccount Account account, @RequestBody TagForm tagForm) {

        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());

        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.removeTags(account, tag);
        return ResponseEntity.ok().build();
    }

}
