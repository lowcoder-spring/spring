package icu.lowcoder.spring.commos.cloudapi.ali.sly;

import icu.lowcoder.spring.commos.cloudapi.RealNameApi;
import icu.lowcoder.spring.commos.cloudapi.ali.sly.model.AliSlyIdCardCheckResponse;
import icu.lowcoder.spring.commos.cloudapi.model.IdCardCheckResponse;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class AliSlyRealNameApiImpl extends AliSlyProvider implements RealNameApi {

    private Map<String, String> meta;

    private RestTemplate restTemplate;

    public AliSlyRealNameApiImpl(RestTemplate restTemplate, Map<String, String> meta) {
        this.meta = meta;
        this.restTemplate = restTemplate;
    }

    @Override
    public IdCardCheckResponse idCardCheck(String name, String idCard) {
        String url = this.getMeta().getOrDefault("url", "https://slyidcard.market.alicloudapi.com/get/idcard/check");

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("idcard", idCard);
        queryParams.add("name", name);

        IdCardCheckResponse cloudApiResponse = new IdCardCheckResponse();

        AliSlyIdCardCheckResponse result = this.call(url, HttpMethod.GET, queryParams, null, AliSlyIdCardCheckResponse.class);
        AliSlyIdCardCheckResponse.Data data = result.getData();

        cloudApiResponse.setPassed(data.getResult().equals(0));
        cloudApiResponse.setDesc(data.getDesc());

        return cloudApiResponse;
    }

    @Override
    protected Map<String, String> getMeta() {
        return this.meta;
    }

    @Override
    protected RestTemplate getRestTemplate() {
        return this.restTemplate;
    }

}
