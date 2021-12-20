package com.studyforyou_retry.modules.study;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.CurrentAccount;
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

@Controller
@RequestMapping("/study/{path}/settings/")
@RequiredArgsConstructor
public class StudySettingController {

    public static final String STUDY_DESCRIPTION = "study/description";
    private final StudyRepository studyRepository;
    private final StudyService studyService;
    private final ModelMapper modelMapper;

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
}
