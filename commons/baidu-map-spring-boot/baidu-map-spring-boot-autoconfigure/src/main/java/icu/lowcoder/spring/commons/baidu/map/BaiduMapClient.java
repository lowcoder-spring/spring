package icu.lowcoder.spring.commons.baidu.map;

import icu.lowcoder.spring.commons.baidu.map.model.ReverseGeoCodingResponse;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class BaiduMapClient {
    private final BaiduMapProperties baiduMapProperties;
    private RestTemplate restTemplate = new RestTemplate();

    public BaiduMapClient(BaiduMapProperties baiduMapProperties) {
        this.baiduMapProperties = baiduMapProperties;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ReverseGeoCodingResponse reverseGeoCoding(double lat, double lng) {
        Map<String, Object> uriVars = new HashMap<>();
        uriVars.put("ak", baiduMapProperties.getAk());
        uriVars.put("location", lat + "," + lng);

        return restTemplate.getForObject(Apis.REVERSE_GEO_CODING, ReverseGeoCodingResponse.class, uriVars);
    }

}

