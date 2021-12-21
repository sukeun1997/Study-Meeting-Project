package com.studyforyou_retry.modules.study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.CurrentAccount;
import com.studyforyou_retry.modules.tags.Tag;
import com.studyforyou_retry.modules.tags.TagForm;
import com.studyforyou_retry.modules.tags.TagRepository;
import com.studyforyou_retry.modules.tags.TagService;
import com.studyforyou_retry.modules.zones.Zone;
import com.studyforyou_retry.modules.zones.ZoneForm;
import com.studyforyou_retry.modules.zones.ZoneRepository;
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
    public static final String STUDY_ZONES = "study/zones";
    public static final String STUDY_STATUS = "study/status";
    private final TagRepository tagRepository;
    private final StudyService studyService;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final TagService tagService;
    private final ZoneRepository zoneRepository;

    @GetMapping("description")
    private String updateDescription(@CurrentAccount Account account, @PathVariable String path, Model model) {

        Study study = studyService.getStudyWithManagers(account, path);

        model.addAttribute(study);
        model.addAttribute(modelMapper.map(study, StudyDescriptionForm.class));
        model.addAttribute(account);

        return STUDY_DESCRIPTION;
    }

    @PostMapping("description")
    private String updateDescription(@CurrentAccount Account account, @PathVariable String path,
                                     @Valid StudyDescriptionForm studyDescriptionForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        Study study = studyService.getStudyWithManagers(account, path);

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return STUDY_DESCRIPTION;
        }

        studyService.updateDescription(study, studyDescriptionForm);
        redirectAttributes.addFlashAttribute("message", "소개가 변경 되었습니다.");

        return "redirect:/study/" + study.getEncodePath(path) + "/settings/description";
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
    private String tagsView(@CurrentAccount Account account, Model model, @PathVariable String path) throws JsonProcessingException {

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


    @GetMapping("zones")
    private String zonesView(@CurrentAccount Account account, @PathVariable String path, Model model) throws JsonProcessingException {

        Study study = studyService.getStudyWithManagers(account, path);

        model.addAttribute(account);
        model.addAttribute(study);

        Set<String> whitelist = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toSet());

        model.addAttribute("whitelist", objectMapper.writeValueAsString(whitelist));
        Set<String> zones = study.getZones().stream().map(Zone::toString).collect(Collectors.toSet());
        model.addAttribute("zones", zones);
        return STUDY_ZONES;
    }

    @PostMapping("zones/add")
    @ResponseBody
    private ResponseEntity addZones(@CurrentAccount Account account, @RequestBody ZoneForm zoneForm, @PathVariable String path) {

        Study study = studyService.getStudyWithManagersAndZones(account, path);
        Zone zone = zoneRepository.findByCityAndLocalNameOfCity(zoneForm.getCity(), zoneForm.getLocalNameOfCity());

        studyService.addZones(study, zone);

        return ResponseEntity.ok().build();
    }

    @PostMapping("zones/remove")
    @ResponseBody
    private ResponseEntity removeZones(@CurrentAccount Account account, @RequestBody ZoneForm zoneForm, @PathVariable String path) {

        Study study = studyService.getStudyWithManagersAndZones(account, path);
        Zone zone = zoneRepository.findByCityAndLocalNameOfCity(zoneForm.getCity(), zoneForm.getLocalNameOfCity());

        studyService.removeZones(study, zone);

        return ResponseEntity.ok().build();
    }

    @GetMapping("study")
    private String statusView(@CurrentAccount Account account, Model model, @PathVariable String path) {

        Study study = studyService.getStudyWithManagers(account, path);

        model.addAttribute(account);
        model.addAttribute(study);

        return STUDY_STATUS;
    }

    @PostMapping("study/publish")
    private String studyPublish(@CurrentAccount Account account,@PathVariable String path, RedirectAttributes redirectAttributes) {

        Study study = studyService.getStudyWithManagers(account, path);

        studyService.publishStudy(study);

        redirectAttributes.addFlashAttribute("message", "스터디 공개 설정이 변경이 완료되었습니다.");
        return "redirect:/study/" + study.getEncodePath(path) + "/settings/study";
    }

    @PostMapping("study/close")
    private String studyClose(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes redirectAttributes) {

        Study study = studyService.getStudyWithManagers(account, path);

        studyService.closeStudy(study);
        redirectAttributes.addFlashAttribute("message", "스터디 공개 설정이 변경이 완료되었습니다.");
        return "redirect:/study/" + study.getEncodePath(path) + "/settings/study";
    }

}

