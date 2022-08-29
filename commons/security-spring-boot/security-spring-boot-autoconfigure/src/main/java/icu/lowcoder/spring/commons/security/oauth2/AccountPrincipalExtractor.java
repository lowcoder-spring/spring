package icu.lowcoder.spring.commons.security.oauth2;

import icu.lowcoder.spring.commons.security.AccountModel;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class AccountPrincipalExtractor implements PrincipalExtractor {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final String[] PRINCIPAL_KEYS = new String[]{
            "id",
            "account",
            "account_id",
            "user",
            "username",
            "userid",
            "user_id",
            "login",
            "name"
    };

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        AccountModel principal = null;
        try {
            Object authorities = map.remove("authorities");

            String jsonStr = OBJECT_MAPPER.writeValueAsString(map);
            principal = OBJECT_MAPPER.readValue(jsonStr, AccountModel.class);

            if (principal.getId() == null) {
                principal = null;
            }

            if (authorities != null) {
                map.put("authorities", authorities);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (principal != null) {
            return principal;
        }

        // default
        for (String key : PRINCIPAL_KEYS) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }

        return null;
    }

}
