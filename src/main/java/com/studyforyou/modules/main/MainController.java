package com.studyforyou.modules.main;

import com.studyforyou.modules.account.AccountRepository;
import com.studyforyou.modules.account.CurrentAccount;
import com.studyforyou.modules.account.Account;
import com.studyforyou.modules.event.Enrollment;
import com.studyforyou.modules.event.EnrollmentRepository;
import com.studyforyou.modules.study.Study;
import com.studyforyou.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final EnrollmentRepository enrollmentRepository;

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {


        if (account != null) {
            Account accountWithTagsAndZone = accountRepository.findAccountWithTagsAndZonesById(account.getId());
            List<Enrollment> enrollmentList = enrollmentRepository.findEnrollmentWithEventAndStudyByAccepted(true);
            List<Study> studyList = studyRepository.findByAccount(accountWithTagsAndZone.getTags(), accountWithTagsAndZone.getZones());
            List<Study> managers = studyRepository.findFirst5ByManagersContainingAndClosedOrderByPublishedDateTime(account,false);
            List<Study> members = studyRepository.findFirst5ByMembersContainingAndClosedOrderByPublishedDateTime(account,false);
            model.addAttribute("account",accountWithTagsAndZone);
            model.addAttribute("enrollmentList", enrollmentList);
            model.addAttribute("studyList", studyList);
            model.addAttribute("studyManagerOf", managers);
            model.addAttribute("studyMemberOf", members);
            return "index-after-login";
        } else {
            List<Study> studyList = studyRepository.findHomeStudyList();
            model.addAttribute("studyList", studyList);
            return "index";
        }

    }


    @GetMapping("/search/study")
    public String SearchList(String keyword, Model model,
                             @PageableDefault(size = 9, sort = "publishedDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Study> studyPage = studyRepository.findByKeyword(keyword, pageable);
        model.addAttribute("keyword", keyword);
        model.addAttribute("studyPage", studyPage);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("publishedDateTime") ? "publishedDateTime" : "memberCount");
        return "list";
    }


}
