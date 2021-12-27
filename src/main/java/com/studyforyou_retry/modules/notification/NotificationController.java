package com.studyforyou_retry.modules.notification;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.CurrentAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    @GetMapping("/notifications")
    private String notificationList(@CurrentAccount Account account, Model model) {


        return "notification/list";
    }

}
