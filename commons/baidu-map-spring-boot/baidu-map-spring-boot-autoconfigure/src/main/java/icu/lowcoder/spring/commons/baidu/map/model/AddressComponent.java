package icu.lowcoder.spring.commons.baidu.map.model;

import lombok.Data;


@Data
public class AddressComponent {
    private String country;
    private Integer countryCode;
    private String countryCodeIso;
    private String countryCodeIso2;
    private String province;
    private String city;
    private Integer cityLevel;
    private String district;
    private String town;
    private String townCode;
    private String street;
    private String streetNumber;
    private Integer adcode;
    private String direction;
    private String distance;
}
