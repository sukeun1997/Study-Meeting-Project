package com.studyforyou.study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyforyou.account.CurrentAccount;
import com.studyforyou.domain.Account;
import com.studyforyou.domain.Study;
import com.studyforyou.domain.Tag;
import com.studyforyou.dto.ImageForm;
import com.studyforyou.dto.StudyDescriptionForm;
import com.studyforyou.dto.TagForm;
import com.studyforyou.repository.StudyRepository;
import com.studyforyou.repository.TagRepository;
import com.studyforyou.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
public class StudySettingsController {

    private static final String STUDY = "study/";
    private static final String DESCRIPTION = "description";
    private static final String BANNER = "banner";
    private static final String TAGS = "tags";
    private final StudyService studyService;
    private final TagService tagService;
    private final ModelMapper modelMapper;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;

    @GetMapping("/description")
    public String studySettingDescription(@CurrentAccount Account account, Model model, @PathVariable String path) {

        Study study = studyService.getUpdateStudy(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(modelMapper.map(study, StudyDescriptionForm.class));

        return STUDY + DESCRIPTION;
    }

    @PostMapping("/description")
    public String studySettingDescription(@CurrentAccount Account account, @Valid StudyDescriptionForm studyDescriptionForm
            , BindingResult bindingResult, Model model, @PathVariable String path, RedirectAttributes redirectAttributes) {

        Study study = studyService.getStudy(path);

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return STUDY + DESCRIPTION;
        }

        studyService.updateDescription(study, studyDescriptionForm);
        redirectAttributes.addFlashAttribute("message", "소개 변경 완료");
        return "redirect:/study/" + getUrl(path) + "/settings/" + DESCRIPTION;
    }

    private String getUrl(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    @GetMapping("/banner")
    public String updateBanner(@CurrentAccount Account account, Model model, @PathVariable String path) {

        Study study = studyService.getUpdateStudy(account,path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(modelMapper.map(study,ImageForm.class));
        return STUDY + BANNER;
    }

    @PostMapping("/banner")
    public String updateBanner(@CurrentAccount Account account, Model model, @PathVariable String path, ImageForm imageForm) {

        Study study = studyService.getUpdateStudy(account,path);
        model.addAttribute(account);
        model.addAttribute(study);
        studyService.updateBanner(study, imageForm);
        return STUDY + BANNER;
    }

    @PostMapping("/banner/enable")
    public String enableBanner(@CurrentAccount Account account, @PathVariable String path,RedirectAttributes redirectAttributes) {
        Study study = studyService.getUpdateStudy(account, path);
        studyService.enableBanner(study);
        redirectAttributes.addFlashAttribute("message", "변경 완료");
        return "redirect:/study/" + getUrl(path) + "/settings/" + BANNER;
    }

    @PostMapping("/banner/disable")
    public String disableBanner(@CurrentAccount Account account, @PathVariable String path,RedirectAttributes redirectAttributes) {
        Study study = studyService.getUpdateStudy(account, path);
        studyService.disableBanner(study);
        redirectAttributes.addFlashAttribute("message", "변경 완료");
        return "redirect:/study/" + getUrl(path) + "/settings/" + BANNER;
    }


    @GetMapping("/tags")
    public String studyTags(@CurrentAccount Account account, @PathVariable String path, Model model) throws JsonProcessingException {

        Study study = studyService.getStudyWithTags(account, path);
        model.addAttribute(account);
        model.addAttribute(study);

        model.addAttribute("tags", study.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));

        Set<String> whitelist = tagService.findByAllTags();
        model.addAttribute("whitelist", objectMapper.writeValueAsString(whitelist));

        return STUDY+TAGS;
    }

    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity studyTagsAdd(@CurrentAccount Account account, @RequestBody TagForm tagForm, @PathVariable String path) {

        Study study = studyService.getStudyWithTags(account, path);
        Tag tag = tagService.getTag(tagForm.getTagTitle());

        studyService.addTags(study,tag);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    @ResponseBody
    public ResponseEntity studyTagsRemove(@CurrentAccount Account account, @RequestBody TagForm tagForm, @PathVariable String path) {

        Study study = studyService.getStudyWithTags(account, path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());

        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }
        studyService.RemoveTags(study,tag);

        return ResponseEntity.ok().build();
    }

}
