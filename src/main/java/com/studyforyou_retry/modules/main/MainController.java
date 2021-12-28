package com.studyforyou_retry.modules.main;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.CurrentAccount;
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

@Controller
@RequiredArgsConstructor
public class MainController {

    private final StudyRepository studyRepository;

    @GetMapping("/")
    private String MainHomePage(@CurrentAccount Account account, Model model) {

        List<Study> studyList = studyRepository.findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(true, false);

        if (account != null) {
            model.addAttribute(account);
        }
        model.addAttribute("studyList", studyList);

        return "index";
    }

    //    http://localhost/search/study?keyword=gd
    @GetMapping("/search/study")
    private String searchStudy(String keyword,String sortProperty, Model model,
                               @PageableDefault(size = 9, sort = "publishedDateTime", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Study> studyPage = studyRepository.findByKeyword(keyword, pageable);

        model.addAttribute("studyPage", studyPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty", sortProperty);
        return "list";
    }
}
