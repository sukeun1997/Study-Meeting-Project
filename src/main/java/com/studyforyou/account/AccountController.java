package com.studyforyou.account;


import com.studyforyou.dto.SignupDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String showSignup(Model model) {

        model.addAttribute("dto", new SignupDto());
        return "account/sign-up";
    }

}
