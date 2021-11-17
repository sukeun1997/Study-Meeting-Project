package com.studyforyou.settings;

import com.studyforyou.domain.Account;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

@Data
public class Notifications {

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb;

    private static ModelMapper modelMapper = new ModelMapper();


    public static Notifications createNotifications(Account account) {
       return modelMapper.map(account, Notifications.class);
    }
}
