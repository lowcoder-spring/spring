package icu.lowcoder.spring.commons.baidu.map.model;

import lombok.Data;


@Data
public class Poi {
    private String addr;
    //private String cp;
    private String direction;
    private String distance;
    private String name;
    private String tag;
    private Location point;
    private String tel;
    private String uid;
    private Integer zip;
    private Poi parentPoi;
}
