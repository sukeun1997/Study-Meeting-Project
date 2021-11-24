package com.studyforyou.study;

import com.studyforyou.account.CurrentAccount;
import com.studyforyou.domain.Account;
import com.studyforyou.domain.Study;
import com.studyforyou.dto.ImageForm;
import com.studyforyou.dto.StudyDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
public class StudySettingsController {

    private static final String STUDY = "study/";
    private static final String DESCRIPTION = "description";
    private static final String BANNER = "banner";
    private final StudyService studyService;
    private final ModelMapper modelMapper;

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
}
