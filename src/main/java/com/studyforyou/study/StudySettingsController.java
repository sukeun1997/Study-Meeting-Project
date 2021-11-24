package com.studyforyou.study;

import com.studyforyou.account.CurrentAccount;
import com.studyforyou.domain.Account;
import com.studyforyou.domain.Study;
import com.studyforyou.dto.StudyDescriptionForm;
import com.studyforyou.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class StudySettingsController {

    private static final String STUDY = "study/";
    private static final String DESCRIPTION = "description";
    private final StudyService studyService;
    private final ModelMapper modelMapper;

    @GetMapping("/study/{path}/settings/description")
    public String studySettingDescription(@CurrentAccount Account account, Model model, @PathVariable String path) {

        Study study = studyService.getUpdateStudy(account, path);
        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(modelMapper.map(study, StudyDescriptionForm.class));

        return STUDY + DESCRIPTION;
    }

    @PostMapping("/study/{path}/settings/description")
    public String studySettingDescription(@CurrentAccount Account account, @Valid StudyDescriptionForm studyDescriptionForm
            , BindingResult bindingResult, Model model, @PathVariable String path, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            return STUDY + DESCRIPTION;
        }

        Study study = studyService.getStudy(path);
        studyService.updateDescription(study, studyDescriptionForm);
        redirectAttributes.addFlashAttribute("message", "소개 변경 완료");
        return "redirect:/study/" + getUrl(path) + "/settings/" + DESCRIPTION;
    }

    private String getUrl(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

}
