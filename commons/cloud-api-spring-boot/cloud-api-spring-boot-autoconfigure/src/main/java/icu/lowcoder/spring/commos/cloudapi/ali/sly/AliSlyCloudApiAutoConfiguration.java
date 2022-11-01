package icu.lowcoder.spring.commos.cloudapi.ali.sly;


import icu.lowcoder.spring.commos.cloudapi.ApiName;
import icu.lowcoder.spring.commos.cloudapi.BankCardApi;
import icu.lowcoder.spring.commos.cloudapi.CloudApiProperties;
import icu.lowcoder.spring.commos.cloudapi.RealNameApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(CloudApiProperties.class)
public class AliSlyCloudApiAutoConfiguration {

    @Bean("aliSlyRealNameApi")
    @ConditionalOnProperty(
            prefix = "icu.lowcoder.spring.commons.cloudapi.apis.REAL_NAME.providers.aliSly",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    RealNameApi aliSlyRealNameApiImpl(CloudApiProperties cloudApiProperties,
                                      @Autowired @Qualifier("cloudApiRestTemplate") RestTemplate restTemplate) {
        return new AliSlyRealNameApiImpl(restTemplate,
                cloudApiProperties.getApis()
                        .get(ApiName.REAL_NAME)
                        .getProviders()
                        .get(AliSlyProvider.NAME)
                        .getMeta()
        );
    }

    @Bean("aliSlyBankCardApi")
    @ConditionalOnProperty(
            prefix = "icu.lowcoder.spring.commons.cloudapi.apis.BANK_CARD.providers.aliSly",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    BankCardApi aliSlyBankCardApiImpl(CloudApiProperties cloudApiProperties,
                                      @Autowired @Qualifier("cloudApiRestTemplate") RestTemplate restTemplate) {
        return new AliSlyBankCardApiImpl(restTemplate,
                cloudApiProperties.getApis()
                        .get(ApiName.BANK_CARD)
                        .getProviders()
                        .get(AliSlyProvider.NAME)
                        .getMeta()
        );
    }

}
