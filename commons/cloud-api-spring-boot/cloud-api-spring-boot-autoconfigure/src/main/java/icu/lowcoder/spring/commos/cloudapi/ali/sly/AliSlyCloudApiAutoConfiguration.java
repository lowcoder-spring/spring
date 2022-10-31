package icu.lowcoder.spring.commos.cloudapi.ali.sly;


import icu.lowcoder.spring.commos.cloudapi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(CloudApiProperties.class)
@ConditionalOnProperty(prefix = "icu.lowcoder.spring.commons.cloudapi.apis.BANK_CARD.providers", name = "aliSly")
@AutoConfigureBefore(CloudApiAutoConfiguration.CloudApiDelegateConfig.class)
public class AliSlyCloudApiAutoConfiguration {

    @Bean("aliSlyRealNameApi")
    RealNameApi aliSlyRealNameApiImpl(CloudApiProperties cloudApiProperties,
                                      @Autowired @Qualifier("cloudApiRestTemplate")RestTemplate restTemplate) {
        return new AliSlyRealNameApiImpl(restTemplate, cloudApiProperties.getApis().get(ApiName.REAL_NAME).getProviders().get(AliSlyProvider.NAME).getMeta());
    }

    @Bean("aliSlyBankCardApi")
    BankCardApi aliSlyBankCardApiImpl(CloudApiProperties cloudApiProperties,
                                      @Autowired @Qualifier("cloudApiRestTemplate")RestTemplate restTemplate) {
        return new AliSlyBankCardApiImpl(restTemplate, cloudApiProperties.getApis().get(ApiName.BANK_CARD).getProviders().get(AliSlyProvider.NAME).getMeta());
    }

}
