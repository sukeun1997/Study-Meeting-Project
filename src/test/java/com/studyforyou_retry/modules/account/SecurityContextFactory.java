package com.studyforyou_retry.modules.account;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Annotation;

@RequiredArgsConstructor
public class SecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final AccountService accountService;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {

        String nickname = withAccount.value();

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname(nickname);
        signUpForm.setEmail(nickname +"@email.com");
        signUpForm.setPassword("testtest");

        accountService.createNewAccount(signUpForm);

        UserDetails userDetailsService = accountService.loadUserByUsername(nickname);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userDetailsService, userDetailsService.getPassword(), userDetailsService.getAuthorities()
        );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(token);
        return securityContext;
    }
}
