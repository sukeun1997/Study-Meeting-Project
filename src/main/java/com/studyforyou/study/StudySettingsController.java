package com.studyforyou.study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyforyou.account.CurrentAccount;
import com.studyforyou.domain.Account;
import com.studyforyou.domain.Study;
import com.studyforyou.domain.Tag;
import com.studyforyou.domain.Zone;
import com.studyforyou.dto.*;
import com.studyforyou.repository.TagRepository;
import com.studyforyou.repository.ZoneRepository;
import com.studyforyou.tag.TagService;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
    private static final String ZONES = "zones";
    private static final String STATUS = "status";
    private final StudyService studyService;
    private final TagService tagService;
    private final ModelMapper modelMapper;
    private final ZoneRepository zoneRepository;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final PathValidator pathValidator;


    @InitBinder("pathForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(pathValidator);
    }

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

        Study study = studyService.getUpdateStudy(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(modelMapper.map(study, ImageForm.class));
        return STUDY + BANNER;
    }

    @PostMapping("/banner")
    public String updateBanner(@CurrentAccount Account account, Model model, @PathVariable String path, ImageForm imageForm) {

        Study study = studyService.getUpdateStudy(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        studyService.updateBanner(study, imageForm);
        return STUDY + BANNER;
    }

    @PostMapping("/banner/enable")
    public String enableBanner(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes redirectAttributes) {
        Study study = studyService.getUpdateStudy(account, path);
        studyService.enableBanner(study);
        redirectAttributes.addFlashAttribute("message", "변경 완료");
        return "redirect:/study/" + getUrl(path) + "/settings/" + BANNER;
    }

    @PostMapping("/banner/disable")
    public String disableBanner(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes redirectAttributes) {
        Study study = studyService.getUpdateStudy(account, path);
        studyService.disableBanner(study);
        redirectAttributes.addFlashAttribute("message", "변경 완료");
        return "redirect:/study/" + getUrl(path) + "/settings/" + BANNER;
    }


    @GetMapping("/tags")
    public String studyTags(@CurrentAccount Account account, @PathVariable String path, Model model) throws JsonProcessingException {

        Study study = studyService.getUpdateStudy(account, path);
        model.addAttribute(account);
        model.addAttribute(study);

        model.addAttribute("tags", study.getTags().stream().map(Tag::getTitle).collect(Collectors.toList()));

        Set<String> whitelist = tagService.findByAllTags();
        model.addAttribute("whitelist", objectMapper.writeValueAsString(whitelist));

        return STUDY + TAGS;
    }

    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity studyTagsAdd(@CurrentAccount Account account, @RequestBody TagForm tagForm, @PathVariable String path) {

        Study study = studyService.getStudyWithTags(account, path);
        Tag tag = tagService.getTag(tagForm.getTagTitle());

        studyService.addTags(study, tag);

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
        studyService.removeTags(study, tag);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/zones")
    public String studyZones(@CurrentAccount Account account, Model model, @PathVariable String path) throws JsonProcessingException {

        Study study = studyService.getUpdateStudy(account, path);

        model.addAttribute(account);
        model.addAttribute(study);

        model.addAttribute("zones", study.getZones());
        List<String> zones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(zones));

        return STUDY + ZONES;
    }


    @PostMapping("/zones/add")
    @ResponseBody
    public ResponseEntity studyZonesAdd(@CurrentAccount Account account, @RequestBody ZoneForm zoneForm, @PathVariable String path) {

        Study study = studyService.getStudyWithZones(account, path);

        Zone zone = zoneRepository.findByCityAndLocalNameOfCity(zoneForm.getCity(), zoneForm.getLocalNameOfCity());
        studyService.addZone(study, zone);

        return ResponseEntity.ok().build();
    }


    @PostMapping("/zones/remove")
    @ResponseBody
    public ResponseEntity studyZonesRemove(@CurrentAccount Account account, @RequestBody ZoneForm zoneForm, @PathVariable String path) {

        Study study = studyService.getStudyWithZones(account, path);

        Zone zone = zoneRepository.findByCityAndLocalNameOfCity(zoneForm.getCity(), zoneForm.getLocalNameOfCity());
        studyService.removeZone(study, zone);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/study")
    public String studyStatus(@CurrentAccount Account account, Model model, @PathVariable String path) {

        Study study = studyService.getUpdateStudy(account, path);

        model.addAttribute(account);
        model.addAttribute(study);

        return STUDY + STATUS;
    }

    @PostMapping("/study/publish")
    public String studyStatusPublish(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes redirectAttributes) {

        Study study = studyService.getStudyWithManagers(account, path);
        studyService.studyPublish(study);

        redirectAttributes.addFlashAttribute("message", "상태 변경 완료");
        return "redirect:/study/" + getUrl(path) + "/settings/study";

    }

    @PostMapping("/study/close")
    public String studyStatusClose(@CurrentAccount Account account, @PathVariable String path, RedirectAttributes redirectAttributes) {

        Study study = studyService.getStudyWithManagers(account, path);
        studyService.studyClose(study);

        redirectAttributes.addFlashAttribute("message", "상태 변경 완료");
        return "redirect:/study/" + getUrl(path) + "/settings/study";

    }

    @PostMapping("/recruit/start")
    public String studyRecruitStart(@CurrentAccount Account account, Model model, @PathVariable String path, RedirectAttributes redirectAttributes) {

        Study study = studyService.getStudyWithManagers(account, path);

        if (!studyService.checkRecruitingTime(study)) {
            redirectAttributes.addFlashAttribute("message", "모집 변경은 1시간마다 가능합니다.");
            return "redirect:/study/" + getUrl(path) + "/settings/study";
        }
        studyService.recruitStart(study);

        redirectAttributes.addFlashAttribute("message", "상태 변경 완료");
        return "redirect:/study/" + getUrl(path) + "/settings/study";

    }

    @PostMapping("/recruit/stop")
    public String studyRecruitStop(@CurrentAccount Account account, Model model, @PathVariable String path, RedirectAttributes redirectAttributes) {

        Study study = studyService.getStudyWithManagers(account, path);

        if (!studyService.checkRecruitingTime(study)) {
            redirectAttributes.addFlashAttribute("message", "모집 변경은 1시간마다 가능합니다.");
            return "redirect:/study/" + getUrl(path) + "/settings/study";
        }
        studyService.recruitStop(study);

        redirectAttributes.addFlashAttribute("message", "상태 변경 완료");
        return "redirect:/study/" + getUrl(path) + "/settings/study";
    }


    @PostMapping("/study/path")
    public String studyPathUpdate(@CurrentAccount Account account, @Valid PathForm pathForm,
                                  BindingResult bindingResult, @PathVariable String path,
                                  RedirectAttributes redirectAttributes) {

        Study study = studyService.getStudyWithManagers(account, path);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("studyPathError", "해당 경로는 사용이 불가능 합니다");
            return "redirect:/study/" + getUrl(path) + "/settings/study";
        }


        String newPath = pathForm.getNewPath();
        studyService.updatePath(study, newPath);
        redirectAttributes.addFlashAttribute("message", "주소 변경 완료");
        return "redirect:/study/" + getUrl(newPath) + "/settings/study";
    }

    @PostMapping("/study/title")
    public String studyPathUpdate(@CurrentAccount Account account, @Valid TitleForm titleForm,
                                  BindingResult bindingResult, @PathVariable String path, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("studyTitleError", bindingResult.getFieldError("newTitle").getDefaultMessage());
            return "redirect:/study/" + getUrl(path) + "/settings/study";
        }

        Study study = studyService.getStudyWithManagers(account, path);

        String newTitle = titleForm.getNewTitle();
        studyService.updateTitle(study, newTitle);
        redirectAttributes.addFlashAttribute("message", "스터디 이름 변경 완료");
        return "redirect:/study/" + getUrl(path) + "/settings/study";
    }

    @PostMapping("study/remove")
    public String studyRemove(@CurrentAccount Account account, @PathVariable String path) {

        Study study = studyService.getStudyWithManagers(account, path);

        studyService.removeStudy(study);
        return "redirect:/";
    }
}


