package com.studyforyou_retry.modules.account.setting;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

    @Id
    @GeneratedValue
    private Long id;

    private String bio;
    private String url;
    private String occupation;
    private String location;

    @Lob
    private String profileImage;

}
