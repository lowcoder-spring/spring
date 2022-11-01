package icu.lowcoder.spring.commos.cloudapi.ali.sly;

import icu.lowcoder.spring.commos.cloudapi.ApiName;
import icu.lowcoder.spring.commos.cloudapi.CloudApi;
import icu.lowcoder.spring.commos.cloudapi.CloudApiException;
import icu.lowcoder.spring.commos.cloudapi.ali.sly.model.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
public abstract class AliSlyProvider implements CloudApi {
    public static final String NAME = "aliSly";

    public String getProvider() {
        return NAME;
    }

    abstract protected Map<String, String> getMeta();
    abstract protected RestTemplate getRestTemplate();

    public <R extends CommonResponse> R call(String url, HttpMethod method, MultiValueMap<String, String> queryParams, HttpHeaders headers, Class<R> rClass) {
        String appCode = this.getMeta().get("appCode");

        if (StringUtils.hasText(appCode)) {
            throw new CloudApiException(String.format("cloud-api[%s, %s] mete data[appCode] must not be empty", this.getName(), this.getProvider()));
        }

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromUriString(url);
        if (queryParams != null) {
            uriComponentsBuilder = uriComponentsBuilder.queryParams(queryParams);
        }

        HttpHeaders finalHeaders = new HttpHeaders();
        if (headers != null) {
            finalHeaders.addAll(headers);
        }
        finalHeaders.set(HttpHeaders.AUTHORIZATION, "APPCODE " + appCode);

        HttpEntity<R> request = new HttpEntity<>(finalHeaders);

        ResponseEntity<R> response = this.getRestTemplate().exchange(
                uriComponentsBuilder.build().encode().toUri(),
                method,
                request,
                rClass
        );
        log.debug("cloud-api[{}, {}] response : {}", this.getName(), this.getProvider(), response);

        R aliSlyResponse = null;
        if (response.getStatusCode().is2xxSuccessful()) {
            R result = response.getBody();
            if (result != null && Boolean.TRUE.equals(result.getSuccess()) && result.getCode().equals(200)) {
                aliSlyResponse = result;
            }
        }

        if (aliSlyResponse == null) {
            throw new CloudApiException(String.format("cloud-api[%s, %s] result: %s",  this.getName(), this.getProvider(), response.getStatusCodeValue()));
        }

        return aliSlyResponse;
    }


}
