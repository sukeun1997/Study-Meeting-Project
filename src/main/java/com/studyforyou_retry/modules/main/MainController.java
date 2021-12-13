package com.studyforyou_retry.modules.main;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.CurrentAccount;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    private String MainHomePage(@CurrentAccount Account account, Model model) {

        if (account != null) {
            model.addAttribute(account);
        }

        return "index";
    }

}
