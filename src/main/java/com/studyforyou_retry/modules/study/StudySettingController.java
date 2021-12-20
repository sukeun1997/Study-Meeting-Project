package com.studyforyou_retry.modules.study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.CurrentAccount;
import com.studyforyou_retry.modules.tags.Tag;
import com.studyforyou_retry.modules.tags.TagForm;
import com.studyforyou_retry.modules.tags.TagRepository;
import com.studyforyou_retry.modules.tags.TagService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/study/{path}/settings/")
@RequiredArgsConstructor
public class StudySettingController {

    public static final String STUDY_DESCRIPTION = "study/description";
    public static final String STUDY_BANNER = "study/banner";
    public static final String STUDY_TAGS = "study/tags";
    private final TagRepository tagRepository;
    private final StudyService studyService;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final TagService tagService;

    @GetMapping("description")
    private String updateDescription(@CurrentAccount Account account, @PathVariable String path, Model model) {

        Study study = studyService.getStudyWithManagers(account,path);

        model.addAttribute(study);
        model.addAttribute(modelMapper.map(study, StudyDescriptionForm.class));
        model.addAttribute(account);

        return STUDY_DESCRIPTION;
    }

    @PostMapping("description")
    private String updateDescription(@CurrentAccount Account account, @PathVariable String path,
                                     @Valid StudyDescriptionForm studyDescriptionForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        Study study = studyService.getStudyWithManagers(account,path);

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return STUDY_DESCRIPTION;
        }

        studyService.updateDescription(study, studyDescriptionForm);
        redirectAttributes.addFlashAttribute("message", "소개가 변경 되었습니다.");

        return "redirect:/study/"+study.getEncodePath(path)+"/settings/description";
    }

    @GetMapping("banner")
    private String updateBanner(@CurrentAccount Account account, Model model, @PathVariable String path) {

        Study study = studyService.getStudyWithManagers(account, path);

        model.addAttribute(account);
        model.addAttribute(study);

        return STUDY_BANNER;
    }

    @PostMapping("banner")
    private String updateBanner(@CurrentAccount Account account, @PathVariable String path, String image, RedirectAttributes redirectAttributes) {

        Study study = studyService.getStudyWithManagers(account, path);

        studyService.updateBanner(study, image);
        redirectAttributes.addFlashAttribute("message", "배너 이미지가 수정 되었습니다.");

        return "redirect:/study/" + study.getEncodePath(path) + "/settings/banner";
    }

    @PostMapping("banner/enable")
    private String enableBanner(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes redirectAttributes) {

        Study study = studyService.getStudyWithManagers(account, path);

        studyService.enableBanner(study);
        redirectAttributes.addFlashAttribute("message", "배너 설정이 변경되었습니다.");
        return "redirect:/study/" + study.getEncodePath(path) + "/settings/banner";
    }

    @PostMapping("banner/disable")
    private String disableBanner(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes redirectAttributes) {

        Study study = studyService.getStudyWithManagers(account, path);

        studyService.disableBanner(study);
        redirectAttributes.addFlashAttribute("message", "배너 설정이 변경되었습니다.");
        return "redirect:/study/" + study.getEncodePath(path) + "/settings/banner";
    }

    @GetMapping("tags")
    private String tagsView(@CurrentAccount Account account, Model model,@PathVariable String path) throws JsonProcessingException {

        Study study = studyService.getStudyWithManagers(account, path);

        model.addAttribute(account);
        model.addAttribute(study);

        Set<String> whitelist = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toSet());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(whitelist));

        Set<String> tags = study.getTags().stream().map(Tag::getTitle).collect(Collectors.toSet());
        model.addAttribute("tags", tags);

        return STUDY_TAGS;
    }

    @PostMapping("tags/add")
    @ResponseBody
    private ResponseEntity addTags(@CurrentAccount Account account, @RequestBody TagForm tagForm, @PathVariable String path) {

        Study study = studyService.getStudyWithManagersAndTags(account, path);
        Tag tag = tagService.getTag(tagForm.getTagTitle());

        studyService.addTag(study, tag);

        return ResponseEntity.ok().build();
    }

    @PostMapping("tags/remove")
    @ResponseBody
    private ResponseEntity removeTags(@CurrentAccount Account account, @RequestBody TagForm tagForm, @PathVariable String path) {

        Study study = studyService.getStudyWithManagersAndTags(account, path);
        Tag tag = tagService.getTag(tagForm.getTagTitle());

        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }

        studyService.removeTags(study, tag);

        return ResponseEntity.ok().build();
    }
}
