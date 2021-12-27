package com.studyforyou_retry.modules.notification;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (modelAndView != null && !isRedirect(request, modelAndView) && authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserAccount) {

            Account account = ((UserAccount) authentication.getPrincipal()).getAccount();
            boolean checked = notificationRepository.existsByToAndChecked(account, false);
            modelAndView.addObject("hasNotification", checked);
        }

    }

    private boolean isRedirect(HttpServletRequest request, ModelAndView modelAndView) {
        return request.getRequestURI().startsWith("redirect:") || modelAndView.getView() instanceof RedirectView;
    }

}
