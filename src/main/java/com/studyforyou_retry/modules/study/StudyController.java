package com.studyforyou_retry.modules.study;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class StudyController {

    public static final String NEW_STUDY = "/new-study";
    public static final String STUDY_FORM = "study/form";
    public static final String STUDY_VIEW = "study/view";
    public static final String STUDY_MEMBERS = "study/members";

    private final StudyService studyService;
    private final StudyRepository studyRepository;

    @GetMapping(NEW_STUDY)
    private String studyCreate(@CurrentAccount Account account, Model model) {

        model.addAttribute(account);
        model.addAttribute(new StudyForm());

        return STUDY_FORM;
    }

    @PostMapping(NEW_STUDY)
    private String studyCreate(@CurrentAccount Account account, @Valid StudyForm studyForm , BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            return STUDY_FORM;
        }

        if (studyRepository.existsByPath(studyForm.getPath())) {
            model.addAttribute(account);
            bindingResult.rejectValue("path", "wrong value", "해당 경로는 사용할 수 없습니다.");
            return STUDY_FORM;
        }


        studyService.createStudy(account, studyForm);
        return "redirect:/study/"+ URLEncoder.encode(studyForm.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("study/{path}")
    private String studyView(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyAll(path);

        model.addAttribute(account);
        model.addAttribute(study);

        return STUDY_VIEW;
    }


    @GetMapping("study/{path}/members")
    private String studyMemberView(@CurrentAccount Account account, @PathVariable String path, Model model) {

        Study study = studyService.getStudyAll(path);

        model.addAttribute(account);
        model.addAttribute(study);

        return STUDY_MEMBERS;
    }


    @GetMapping("study/{path}/join")
    private String joinStudy(@CurrentAccount Account account, @PathVariable String path, Model model) {

        Study study = studyService.getStudyAll(path);

        studyService.joinStudy(study, account);

        model.addAttribute(account);
        model.addAttribute(study);

        return STUDY_MEMBERS;
    }

    @GetMapping("study/{path}/leave")
    private String leaveStudy(@CurrentAccount Account account, @PathVariable String path, Model model) {

        Study study = studyService.getStudyAll(path);

        studyService.leaveStudy(study, account);
        model.addAttribute(account);
        model.addAttribute(study);

        return STUDY_MEMBERS;
    }

}
