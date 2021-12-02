package com.studyforyou.modules.zone;


import lombok.Data;


@Data
public class ZoneForm {

    private String zoneName;

    public String getCity() {
        return zoneName.substring(0, zoneName.indexOf("("));
    }

    public String getLocalNameOfCity(){
        return zoneName.substring(zoneName.indexOf("(")+1,zoneName.indexOf(")"));
    }

    private String getProvince() {
     return zoneName.substring(zoneName.indexOf("/"));
    }


    public Zone getZone() {
        return Zone.builder().city(getCity()).localNameOfCity(getLocalNameOfCity()).province(getProvince()).build();
    }

}
