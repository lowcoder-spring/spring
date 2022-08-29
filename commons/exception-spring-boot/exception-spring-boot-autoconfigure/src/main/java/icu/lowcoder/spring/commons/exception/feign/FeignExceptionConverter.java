package icu.lowcoder.spring.commons.exception.feign;

import icu.lowcoder.spring.commons.exception.UnifiedExceptionResponse;
import icu.lowcoder.spring.commons.exception.converter.UnifiedExceptionConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;

@Slf4j
public class FeignExceptionConverter implements UnifiedExceptionConverter<FeignException> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public UnifiedExceptionResponse convert(FeignException e) {
        String stringBody = e.contentUTF8();
        if (StringUtils.hasText(stringBody)) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> remoteResp = OBJECT_MAPPER.readValue(stringBody, Map.class);
                return UnifiedExceptionResponse.builder()
                        .timestamp(new Date((long)remoteResp.get("timestamp")))
                        .status((Integer) remoteResp.get("status"))
                        .exception((String) remoteResp.get("exception"))
                        .uri((String) remoteResp.get("uri"))
                        .message((String) remoteResp.get("message"))
                        .detail((String) remoteResp.get("detail"))
                        .build();
            } catch (Exception ignored) {
            }
        }

        return UnifiedExceptionResponse.builder()
                .message("请求服务出现未知错误")
                .status(e.status())
                .exception(e.getClass().getName())
                .build();
    }

    @Override
    public boolean support(Class<?> clazz) {
        return FeignException.class.isAssignableFrom(clazz);
    }
}
