package com.studyforyou.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyforyou.account.AccountService;
import com.studyforyou.account.CurrentUser;
import com.studyforyou.account.SignUpFormValidator;
import com.studyforyou.domain.Account;
import com.studyforyou.domain.Tag;
import com.studyforyou.dto.PasswordForm;
import com.studyforyou.dto.TagForm;
import com.studyforyou.repository.TagRepository;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    public static final String SETTINGS_PROFILE = "settings/profile";
    public static final String SETTINGS_PASSWORD = "settings/password";
    public static final String SETTINGS_NOTIFICATIONS = "settings/notifications";
    public static final String SETTINGS_ACCOUNT = "settings/account";
    public static final String SETTINGS_TAGS = "settings/tags";
    private final AccountService accountService;
    private final NicknameValidator nicknameValidator;
    private final PasswordFormValidator passwordFormValidator;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(passwordFormValidator);
    }

    @InitBinder("nicknameForm")
    public void initBinderNickName(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameValidator);
    }

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

        accountService.completeProfileUpdate(account, profile);
        model.addAttribute(account);
        return "redirect:/profile/" + account.getNickname();
    }

    @GetMapping("/settings/password")
    public String passwordUpdate(@CurrentUser Account account, Model model) {

        model.addAttribute(account);
        model.addAttribute("passwordForm", new PasswordForm());
        return SETTINGS_PASSWORD;
    }

    @PostMapping("/settings/password")
    public String passwordUpdate(@Valid PasswordForm passwordForm, BindingResult bindingResult,
                                 Model model, @CurrentUser Account account, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PASSWORD;
        }
        accountService.updatePassword(passwordForm, account);

        redirectAttributes.addFlashAttribute("message", "패스워드 변경이 완료 되었습니다.");
        return "redirect:/" + SETTINGS_PASSWORD;
    }


    @GetMapping("/settings/notifications")
    public String notificationsSetting(@CurrentUser Account account, Model model) {

        model.addAttribute(account);
        model.addAttribute(Notifications.createNotifications(account));
        return SETTINGS_NOTIFICATIONS;
    }

    @PostMapping("/settings/notifications")
    public String notificationsSetting(@CurrentUser Account account, @Valid Notifications notifications, Model model, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_NOTIFICATIONS;
        }
        accountService.updateNotifications(account, notifications);

        redirectAttributes.addFlashAttribute("message", "알림설정 변경 완료");
        return "redirect:/" + SETTINGS_NOTIFICATIONS;

    }

    @GetMapping("/settings/account")
    public String nickNameUpdate(Model model, @CurrentUser Account account) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return SETTINGS_ACCOUNT;
    }

    @PostMapping("/settings/account")
    public String nickNameUpdate(@Valid NicknameForm nicknameForm, BindingResult bindingResult, Model model, @CurrentUser Account account,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_ACCOUNT;
        }

        accountService.updateNickName(account, nicknameForm);
        redirectAttributes.addFlashAttribute("message", "닉네임 변경이 완료되었습니다.");
        return "redirect:/" + SETTINGS_ACCOUNT;
    }

    @GetMapping("/settings/tags")
    public String tagsUpdate(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account); //프로필 이미지

        Set<Tag> tags = accountService.getTags(account);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toSet()));

        List<String> list = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(list));

        return SETTINGS_TAGS;
    }

    @PostMapping("/settings/tags/add")
    @ResponseBody
    public ResponseEntity tagsAdd(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();

        Tag tag = tagRepository.findByTitle(title);

        if (tag == null) {
            tag = tagRepository.save(Tag.builder().title(tagForm.getTagTitle()).build());
        }

        accountService.addTag(account, tag);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/settings/tags/remove")
    @ResponseBody
    public ResponseEntity tagsRemove(@CurrentUser Account account, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();

        Tag tag = tagRepository.findByTitle(title);

        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }

        accountService.removeTag(account, tag);

        return ResponseEntity.ok().build();
    }
}
