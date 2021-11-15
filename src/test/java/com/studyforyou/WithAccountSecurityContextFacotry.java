package com.studyforyou;

import com.studyforyou.account.AccountService;
import com.studyforyou.dto.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Annotation;

@RequiredArgsConstructor
public class WithAccountSecurityContextFacotry implements WithSecurityContextFactory<WithAccount> {

    private final AccountService accountService;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {
        String name = withAccount.value();

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail(name+"@email.com");
        signUpForm.setPassword("12345678");
        signUpForm.setNickname(name);
        accountService.processNewAccount(signUpForm);

        UserDetails principal = accountService.loadUserByUsername(name);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticationToken);

        return context;
    }
}
