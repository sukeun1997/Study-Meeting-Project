package com.studyforyou.settings;

import com.studyforyou.domain.Account;
import lombok.Data;
import org.modelmapper.ModelMapper;

@Data
public class Profile {

    private String bio;

    private String url;

    private String occupation;

    private String location;


    private static ModelMapper modelMapper = new ModelMapper();

    public Profile(Account account) {
        modelMapper.map(account, this);
    }
}
