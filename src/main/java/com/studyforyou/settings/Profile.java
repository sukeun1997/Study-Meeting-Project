package com.studyforyou.settings;

import com.studyforyou.domain.Account;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

@Data
public class Profile {

    @Length(min = 1,max = 35, message = "35자 이내로 입력 해 주세요")
    private String bio;

    @URL(message = "옳바른 URL이 아닙니다.")
    private String url;


    private String occupation;

    private String location;


    private String profileImage;


    private static ModelMapper modelMapper = new ModelMapper();

    public static Profile createProfile(Account account) {
        Profile profile = new Profile();
        modelMapper.map(account, profile);
        return profile;
    }
}
