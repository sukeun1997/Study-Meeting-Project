package com.studyforyou_retry.modules.main;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.AccountRepository;
import com.studyforyou_retry.modules.account.CurrentAccount;
import com.studyforyou_retry.modules.event.Enrollment;
import com.studyforyou_retry.modules.event.EnrollmentRepository;
import com.studyforyou_retry.modules.study.Study;
import com.studyforyou_retry.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final StudyRepository studyRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AccountRepository accountRepository;

    @GetMapping("/")
    private String MainHomePage(@CurrentAccount Account account, Model model) {


        if (account != null) {
            account = accountRepository.findAccountWithTagsAndZonesById(account.getId());
            Set<Enrollment> enrollments = enrollmentRepository.findFirst4ByAccountOrderByEnrolledAtDesc(account);
            Set<Study> studyManagerOf = studyRepository.findFirst10ByManagersContainingOrderByPublishedDateTimeDesc(account);
            Set<Study> studyMemberOf = studyRepository.findFirst10ByMembersContainingOrderByPublishedDateTimeDesc(account);
            model.addAttribute(account);
            model.addAttribute("studyManagerOf", studyManagerOf);
            model.addAttribute("studyMemberOf", studyMemberOf);
            model.addAttribute("enrollmentList", enrollments);

            return "index-after-login";
        } else {
            Set<Study> studyList = studyRepository.findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(true, false);

            model.addAttribute("studyList", studyList);
            return "index";
        }
    }

    @GetMapping("/search/study")
    private String searchStudy(String keyword, Model model,
                               @PageableDefault(size = 9, sort = "publishedDateTime", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Study> studyPage = studyRepository.findByKeyword(keyword, pageable);

        model.addAttribute("studyPage", studyPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("publishedDateTime") ? "publishedDateTime" : "memberCount");
        return "list";
    }
}
