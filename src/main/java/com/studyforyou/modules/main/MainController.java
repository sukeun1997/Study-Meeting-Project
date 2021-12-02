package com.studyforyou.modules.main;

import com.studyforyou.modules.account.CurrentAccount;
import com.studyforyou.modules.account.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {

        if (account != null) {
            model.addAttribute(account);
        }

        return "index";

    }
}
