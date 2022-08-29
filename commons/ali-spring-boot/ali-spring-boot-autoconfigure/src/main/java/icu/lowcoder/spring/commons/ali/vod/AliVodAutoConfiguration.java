package icu.lowcoder.spring.commons.ali.vod;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "icu.lowcoder.spring.commons.ali.vod", name = {"access-id", "access-key"})
@EnableConfigurationProperties(AliVodProperties.class)
public class AliVodAutoConfiguration {

    @Bean
    public DefaultAcsClient aliAcsClient(AliVodProperties aliVodProperties) {
        DefaultProfile profile = DefaultProfile.getProfile(aliVodProperties.getRegionId(), aliVodProperties.getAccessId(), aliVodProperties.getAccessKey());
        return new DefaultAcsClient(profile);
    }

    @Bean
    public AliVodClient aliVodClient(DefaultAcsClient acsClient, AliVodProperties aliVodProperties) {
        return new AliVodClient(acsClient, aliVodProperties);
    }

}
