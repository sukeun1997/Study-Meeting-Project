package com.studyforyou_retry.modules.account;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class AccountFactory {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    public void createNewAccount(String nickname) {

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname(nickname);
        signUpForm.setEmail(nickname +"@test.com");
        signUpForm.setPassword(nickname+"testtest");

        accountService.createNewAccount(signUpForm);
//        Account account = modelMapper.map(signUpForm, Account.class);
//        accountRepository.save(account);
    }
}
