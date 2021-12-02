package com.studyforyou.modules.study;

import com.studyforyou.modules.account.CurrentAccount;
import com.studyforyou.modules.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class StudyController {

    public static final String STUDY = "study/";
    public static final String NEW_STUDY = "/new-study";
    public static final String MEMBERS = "members";
    public static final String VIEW = "view";
    public static final String FORM = "form";

    private final StudyValidator studyValidator;
    private final StudyService studyService;
    private final StudyRepository studyRepository;


    @InitBinder("studyForm")
    public void Init(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(studyValidator);
    }

    @GetMapping(NEW_STUDY)
    public String newStudyForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(new StudyForm());
        model.addAttribute(account);

        return STUDY + FORM;
    }

    @PostMapping(NEW_STUDY)
    public String NewStudyForm(@CurrentAccount Account account, Model model, @Valid StudyForm studyForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            return STUDY + FORM;
        }


        studyService.newStudy(account, studyForm);
        return "redirect:/study/" + URLEncoder.encode(studyForm.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/study/{path}")
    public String studyView(@CurrentAccount Account account, Model model, @PathVariable String path) {

        model.addAttribute(account);
        model.addAttribute(studyService.getStudy(path));
        return STUDY + VIEW;
    }


    @GetMapping("/study/{path}/members")
    public String studyMembers(@CurrentAccount Account account, Model model, @PathVariable String path) {

        model.addAttribute(account);
        model.addAttribute(studyService.getStudy(path));

        return STUDY + MEMBERS;
    }

    @GetMapping("/study/{path}/join")
    public String studyJoin(@CurrentAccount Account account, Model model, @PathVariable String path) {

        Study study = studyService.getStudy(path);

        studyService.joinStudy(account, study);

        model.addAttribute(account);
        model.addAttribute(study);
        return STUDY + MEMBERS;
    }

    @GetMapping("/study/{path}/leave")
    public String studyLeave(@CurrentAccount Account account, @PathVariable String path) {

        Study study = studyService.getStudyWithMembers(path);

        studyService.leaveStudy(account, study);

        return "redirect:/";
    }
}
