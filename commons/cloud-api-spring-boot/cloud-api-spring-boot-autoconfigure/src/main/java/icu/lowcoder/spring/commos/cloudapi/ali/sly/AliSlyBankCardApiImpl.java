package icu.lowcoder.spring.commos.cloudapi.ali.sly;

import icu.lowcoder.spring.commos.cloudapi.BankCardApi;
import icu.lowcoder.spring.commos.cloudapi.CloudApiRequestException;
import icu.lowcoder.spring.commos.cloudapi.ali.sly.model.AliSlyBankCardCheckResponse;
import icu.lowcoder.spring.commos.cloudapi.model.BankCardCheckResponse;
import icu.lowcoder.spring.commos.cloudapi.model.BankCardType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
public class AliSlyBankCardApiImpl extends AliSlyProvider implements BankCardApi {

    private Map<String, String> meta;

    private RestTemplate restTemplate;

    public AliSlyBankCardApiImpl(RestTemplate restTemplate, Map<String, String> meta) {
        this.meta = meta;
        this.restTemplate = restTemplate;
    }

    @Override
    public BankCardCheckResponse bankCardOwnerCheck(String name, String idCard, String bankCardNo) {
        String url = this.getMeta().getOrDefault("url", "https://slybank234.market.alicloudapi.com/bankcard234/check");

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("idcard", idCard);
        queryParams.add("name", name);
        queryParams.add("bankcard", bankCardNo);

        BankCardCheckResponse cloudApiResponse = new BankCardCheckResponse();

        try {
            AliSlyBankCardCheckResponse result = this.call(url, HttpMethod.GET, queryParams, null, AliSlyBankCardCheckResponse.class);
            AliSlyBankCardCheckResponse.Data data = result.getData();

            cloudApiResponse.setPassed(data.getResult().equals(0));
            cloudApiResponse.setDesc(data.getDesc());
            cloudApiResponse.setAbbreviation(data.getBankInfo().getAbbreviation());
            switch (data.getBankInfo().getType()) {
                case "借记卡" -> cloudApiResponse.setType(BankCardType.DEBIT);
                case "贷记卡" -> cloudApiResponse.setType(BankCardType.CREDIT);
                default -> cloudApiResponse.setType(BankCardType.OTHER);
            }
            cloudApiResponse.setBank(data.getBankInfo().getBank());
            cloudApiResponse.setLogo(data.getBankInfo().getLogo());
        } catch (CloudApiRequestException e) {
            cloudApiResponse.setPassed(false);
            cloudApiResponse.setDesc(e.getMessage());
        }

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
