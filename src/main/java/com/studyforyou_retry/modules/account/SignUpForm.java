package com.studyforyou_retry.modules.account;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class SignUpForm {

    @NotBlank(message = "닉네임은 필수 입력 값 입니다.")
    @Pattern(regexp = "^[a-zA-zㄱ-ㅎ가-힣]*$", message = "영어 또는 한글로만 입력이 가능합니다.")
    @Length(min = 3, max = 20, message = "3글자 이상 20글자 이하로 입력 해 주세요")
    private String nickname;

    @Email(message = "이메일 형태로 입력 해 주세요.")
    @NotBlank(message = "필수 입력 값 입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값 입니다.")
    @Length(min = 8, max = 50, message = "8자 이상 50자 이내로 입력 해주세요")
    private String password;
}
