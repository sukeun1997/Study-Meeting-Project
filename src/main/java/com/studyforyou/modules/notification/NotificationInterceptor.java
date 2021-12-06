package com.studyforyou.modules.notification;

import com.studyforyou.modules.account.Account;
import com.studyforyou.modules.account.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
@RequiredArgsConstructor
public class NotificationInterceptor implements HandlerInterceptor {


    private final NotificationRepository notificationRepository;

    // handler 이후 view 처리 이전
    // modelview 가 null 이 아니면서 리다이렉트가 아니여야함 ( 리다이렉트 후 또 실행하므로 중복 수행)
    // 인증된 사용자여야 하며 익명 사용자가 아니여야함
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (modelAndView != null && !isRedirect(modelAndView) && authentication != null && authentication.getPrincipal() instanceof UserAccount) {
            Account account = ((UserAccount) authentication.getPrincipal()).getAccount();
            long count = notificationRepository.countByAccountAndChecked(account, false);
            modelAndView.addObject("hasNotification", count > 0);
        }
    }

    private boolean isRedirect(ModelAndView modelAndView) {
        // view 이름이 redirect: 로 시작하거나 getView 가 리다이렉트 뷰 일때
        return modelAndView.getViewName().startsWith("redirect:") || modelAndView.getView() instanceof RedirectView;
    }
}
