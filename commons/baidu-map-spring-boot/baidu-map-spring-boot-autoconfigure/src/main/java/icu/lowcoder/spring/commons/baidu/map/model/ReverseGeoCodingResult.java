package icu.lowcoder.spring.commons.baidu.map.model;

import lombok.Data;

import java.util.List;

// http://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-geocoding-abroad
@Data
public class ReverseGeoCodingResult {
    private Location location;
    private String formattedAddress;
    private String business;
    private AddressComponent addressComponent;
    private List<Poi> pois;
    private List<Road> roads;
    private List<PoiRegion> poiRegions;
    private String sematicDescription;
    //private Integer cityCode;
}
