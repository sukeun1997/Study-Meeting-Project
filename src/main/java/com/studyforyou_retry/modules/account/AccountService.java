package com.studyforyou_retry.modules.account;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    public void createNewAccount(SignUpForm signUpForm) {
        Account account = modelMapper.map(signUpForm, Account.class);
//        account.getGenerateCheckToken();
        accountRepository.save(account);
    }
}

