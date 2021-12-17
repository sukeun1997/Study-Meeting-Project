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

    public Account createNewAccount(String nickname) {

        return accountRepository.save(Account.builder()
                .email(nickname + "@email.com")
                .nickname(nickname)
                .password(nickname + "asd")
                .build());
    }
}
