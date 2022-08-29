package icu.lowcoder.spring.commons.ali.oss.sts;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import icu.lowcoder.spring.commons.ali.oss.AliOssProperties;
import icu.lowcoder.spring.commons.ali.oss.Policy;
import icu.lowcoder.spring.commons.ali.oss.model.OssAccessSts;
import icu.lowcoder.spring.commons.util.json.JsonUtils;
import icu.lowcoder.spring.commons.util.json.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
public class AliOssStsManager {
    private final AliOssProperties ossProperties;
    private final AliOssStsProperties stsProperties;

    public AliOssStsManager(AliOssProperties aliOssProperties, AliOssStsProperties aliOssStsProperties) {
        this.ossProperties = aliOssProperties;
        this.stsProperties = aliOssStsProperties;
    }

    /**
     * @param userId 可能为 null
     * @return sts
     */
    public OssAccessSts applySts(String userId) {
        return applySts(ossProperties.getDefaultBucket(), userId);
    }
    public OssAccessSts applySts(String bucket, String userId) {
        OssAccessSts sts = new OssAccessSts();

        try {
            // 如果有用户信息则请求 sts，否则只返回 endpoint 和 bucket
            if (userId != null) {
                // 构造default profile（参数留空，无需添加region ID）
                IClientProfile profile = DefaultProfile.getProfile("", ossProperties.getAccessId(), ossProperties.getAccessKey());
                // 用profile构造client
                DefaultAcsClient client = new DefaultAcsClient(profile);
                final AssumeRoleRequest request = new AssumeRoleRequest();
                request.setSysEndpoint(stsProperties.getEndpoint());
                request.setSysMethod(MethodType.POST);
                request.setRoleArn(stsProperties.getRoleArn());
                request.setRoleSessionName(formatUserId(userId));// 因为长度不能超过32

                // Policy
                Policy policy = new Policy();
                policy.setVersion("1");
                Policy.Statement statement = new Policy.Statement();
                statement.setEffect("Allow");
                statement.setAction("oss:GetObject"); // 只允 GetObject
                statement.setResource(Arrays.asList("acs:oss:*:*:" + bucket, "acs:oss:*:*:" + bucket + "/*"));
                policy.setStatement(Collections.singletonList(statement));
                request.setPolicy(JsonUtils.toJson(policy, PropertyNamingStrategy.PASCAL_CASE));

                request.setDurationSeconds(stsProperties.getDurationSeconds());
                final AssumeRoleResponse response = client.getAcsResponse(request);
                BeanUtils.copyProperties(response.getCredentials(), sts);

                // 过期时间转换为本地时区
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date after = null;
                try {
                    after = df.parse(sts.getExpiration());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat finalDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sts.setExpiration(finalDateFormat.format(after));
            }

            sts.setBucket(bucket);
            sts.setEndpoint(ossProperties.getExtranetEndpoint());

            return sts;
        } catch (ClientException e) {
            log.warn("获取STS失败, code:{}, message:{}, requestId:{}", e.getErrCode(), e.getErrMsg(), e.getRequestId(), e);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "获取STS失败");
        }
    }

    private String formatUserId(String original) {
        if (original != null && original.length() >= 32) {
            String formatted = original.replaceAll("-", ""); // uuid 格式替换掉 ‘-’
            if (formatted.length() >= 32) {
                formatted = formatted.substring(0, 31);
            }
            return formatted;
        }
        return original;
    }
}
