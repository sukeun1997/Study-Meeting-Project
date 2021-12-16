package com.studyforyou_retry.modules.zones;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity @EqualsAndHashCode(of = "id")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class Zone {

    @Id @GeneratedValue
    private Long id;

    private String city;
    private String localNameOfCity;
    private String province;


    @Override
    public String toString() {
        return String.format("%s(%s)/%s",city,localNameOfCity,province);
    }
}
