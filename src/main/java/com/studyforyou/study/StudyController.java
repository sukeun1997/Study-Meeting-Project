package com.studyforyou.study;

import com.studyforyou.account.CurrentAccount;
import com.studyforyou.domain.Account;
import com.studyforyou.dto.StudyForm;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.Acceleration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class StudyController {

    public static final String STUDY = "/study/study";
    public static final String NEW_STUDY = "/new-study";

    private final StudyValidator studyValidator;
    private final StudyService studyService;


    @InitBinder("studyForm")
    public void Init(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(studyValidator);
    }

    @GetMapping(NEW_STUDY)
    public String newStudyForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(new StudyForm());
        model.addAttribute(account);

        return STUDY;
    }

    @PostMapping(NEW_STUDY)
    public String NewStudyForm(@CurrentAccount Account account,Model model, @Valid StudyForm studyForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            return STUDY;
        }


        studyService.newStudy(account, studyForm);
        return "redirect:/study/" + URLEncoder.encode(studyForm.getPath(), StandardCharsets.UTF_8);
    }

}
