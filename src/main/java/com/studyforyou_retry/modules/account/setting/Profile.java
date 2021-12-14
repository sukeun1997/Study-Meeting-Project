package com.studyforyou_retry.modules.account.setting;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;

@Data
public class Profile {


    @NotBlank(message = "필수 입력 값 입니다.")
    @Length(max = 35, message = "35자 이하로 입력 해주세요")
    private String bio;

    @URL(message = "주소 형태로 입력 해주세요.")
    private String url;

    private String occupation;


    private String location;

    @Lob
    private String profileImage;

}
