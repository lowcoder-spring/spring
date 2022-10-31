package icu.lowcoder.spring.commos.cloudapi;

import icu.lowcoder.spring.commos.cloudapi.model.BankCardCheckResponse;
import icu.lowcoder.spring.commos.cloudapi.model.IdCardCheckResponse;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

public class CloudApiDelegate implements BankCardApi, RealNameApi {

    public CloudApiDelegate(CloudApiProperties cloudApiProperties, List<CloudApi> cloudApis) {
        this.apis = cloudApis;
        this.cloudApiProperties = cloudApiProperties;
    }

    private List<CloudApi> apis;

    private CloudApiProperties cloudApiProperties;

    private String selectProvider(ApiName apiName) {
        String provider = null;
        if (cloudApiProperties.getApis().containsKey(apiName)) {
            CloudApiProperties.Api apiConfig = cloudApiProperties.getApis().get(apiName);
            if (apiConfig == null) {
                throw new CloudApiException("There is no cloud-api configured: " + apiName);
            }

            if (StringUtils.hasText(apiConfig.getDefaultProvider())) {
                provider = apiConfig.getDefaultProvider();
            } else {
                if (!apiConfig.getProviders().isEmpty()) {
                    provider = apiConfig.getProviders().entrySet()
                            .stream()
                            .filter(e -> e.getValue().getEnabled())
                            .findFirst()
                            .map(Map.Entry::getKey)
                            .orElse(null);
                }
            }
        }

        if (provider == null) {
            throw new CloudApiException("There is no cloud-api provider configured: " + apiName);
        }

        return provider;
    }

    private CloudApi selectApi(ApiName apiName) {
        String provider = selectProvider(apiName);
        CloudApi api = apis.stream().filter(a -> a.getName().equals(apiName) && a.getProvider().equalsIgnoreCase(provider))
                .findFirst()
                .orElse(null);
        if (api == null) {
            throw new CloudApiException("There is no cloud-api impl: " + apiName);
        }

        return api;
    }

    @Override
    public BankCardCheckResponse bankCardOwnerCheck(String name, String idCard, String bankCardNo) {
        BankCardApi bankCardApi = (BankCardApi) selectApi(ApiName.BANK_CARD);
        return bankCardApi.bankCardOwnerCheck(name, idCard, bankCardNo);
    }


    @Override
    public IdCardCheckResponse idCardCheck(String name, String idCard) {
        RealNameApi realNameApi = (RealNameApi) selectApi(ApiName.REAL_NAME);
        return realNameApi.idCardCheck(name, idCard);
    }

    @Override
    public ApiName getName() {
        return null;
    }
    @Override
    public String getProvider() {
        return "Delegate";
    }

}
