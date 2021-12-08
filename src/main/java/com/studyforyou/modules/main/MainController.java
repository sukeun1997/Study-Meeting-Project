package com.studyforyou.modules.main;

import com.studyforyou.modules.account.CurrentAccount;
import com.studyforyou.modules.account.Account;
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

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {

        if (account != null) {
            model.addAttribute(account);
        }

        return "index";

    }

    @GetMapping("/search/study")
    public String SearchList(String keyword, Model model,
                             @PageableDefault(size = 9) Pageable pageable) {
        Page<Study> studyPage = studyRepository.findByKeyword(keyword, pageable);
        model.addAttribute("keyword", keyword);
        model.addAttribute("studyPage", studyPage);
        return "list";
    }
}
