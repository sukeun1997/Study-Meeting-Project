package com.studyforyou_retry.modules.zones;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ZoneForm {

    private String zoneName;

    public String getCity() {
        return zoneName.substring(0, zoneName.indexOf('('));
    }

    public String getLocalNameOfCity() {
        return zoneName.substring(zoneName.indexOf('(') + 1, zoneName.indexOf(')'));
    }

    public String getProvince() {
        return zoneName.substring(zoneName.indexOf('/'));
    }
}
