package com.studyforyou_retry.modules.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    public void createNewAccount(SignUpForm signUpForm) {
        Account account = modelMapper.map(signUpForm, Account.class);
        account.getGenerateCheckToken();
        log.info("/check-email-token?token={}&email={}", account.getEmailCheckToken(), account.getEmail());
        accountRepository.save(account);
    }
}

