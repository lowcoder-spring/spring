package icu.lowcoder.spring.commons.ali.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.*;
import icu.lowcoder.spring.commons.ali.oss.model.UploadPolicy;
import lombok.SneakyThrows;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class AliOssClient {
    private final AliOssProperties aliOssProperties;
    private final OSS oss;
    private RestTemplate restTemplate = new RestTemplate();

    public AliOssClient(AliOssProperties aliOssProperties, OSS ossBackend) {
        oss = ossBackend;
        this.aliOssProperties = aliOssProperties;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BucketInfo getBucketInfo(String bucket) {
        // bucket info
        if (!oss.doesBucketExist(bucket)) {
            throw new RuntimeException("bucket[" + bucket + "]不存在");
        }
        return oss.getBucketInfo(bucket);
    }

    public String getTempDir() {
        return aliOssProperties.getDefDir();
    }

    public UploadPolicy uploadPolicy(String dir, String userId) {
        if (dir == null || StringUtils.isEmpty(dir.trim())) {
            dir = aliOssProperties.getDefDir();
        }

        return uploadPolicy(aliOssProperties.getDefaultBucket(), dir, userId);
    }

    @SneakyThrows
    public UploadPolicy uploadPolicy(String bucket, String dir, String userId) {
        BucketInfo bucketInfo = getBucketInfo(bucket);

        if (dir != null && dir.length() > 0 && !dir.endsWith("/")) {
            dir = dir + "/";
        }
        if (dir == null) {
            dir = "";
        }

        UploadPolicy uploadPolicy = new UploadPolicy();

        long expireEndTime = System.currentTimeMillis() + aliOssProperties.getUploadPolicyExpireSecond() * 1000;
        Date expiration = new Date(expireEndTime);

        PolicyConditions policyConditions = new PolicyConditions();
        policyConditions.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
        policyConditions.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

        String postPolicy = oss.generatePostPolicy(expiration, policyConditions);
        byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
        String encodedPolicy = BinaryUtil.toBase64String(binaryData);
        String postSignature = oss.calculatePostSignature(postPolicy);

        uploadPolicy.setAccessId(aliOssProperties.getAccessId());
        uploadPolicy.setPolicy(encodedPolicy);
        uploadPolicy.setSignature(postSignature);
        uploadPolicy.setDir(dir);
        uploadPolicy.setHost(getBucketAccessHost(bucketInfo));
        uploadPolicy.setExpire(expireEndTime / 1000);

        JSONObject jasonCallback = new JSONObject();
        jasonCallback.put("callbackUrl", aliOssProperties.getCallbackUrl());
        jasonCallback.put("callbackBody",
                "bucket=${bucket}&object=${object}&etag=${etag}&size=${size}&mimeType=${mimeType}"+
                        //"&imageHeight=${imageInfo.height}&imageWidth=${imageInfo.width}&imageFormat=${imageInfo.format}" +
                        "&userId=${x:user_id}&dir=${x:dir}&originalName=${x:original_name}&host=${x:host}"  // 自定义参数
        );
        jasonCallback.put("callbackBodyType", "application/x-www-form-urlencoded");
        String base64CallbackBody = BinaryUtil.toBase64String(jasonCallback.toString().getBytes());
        uploadPolicy.setCallback(base64CallbackBody);

        // 自定义参数列表
        Map<String, String> customParams = new HashMap<>();
        customParams.put("x:user_id", userId);
        customParams.put("x:dir", dir);
        customParams.put("x:original_name", ""); // 需要前端填充
        customParams.put("x:host", getBucketAccessHost(bucketInfo));
        uploadPolicy.setCustomParams(customParams);

        return uploadPolicy;
    }

    private String getBucketAccessHost(BucketInfo bucketInfo) {
        return "https://" + bucketInfo.getBucket().getName() + "." + bucketInfo.getBucket().getExtranetEndpoint();
    }

    public boolean verifyCallbackRequest(String requestUri, String queryString, String authorization, String pubKeyUrl, String callbackBodyStr) {
        byte[] authorizationByte = Base64Utils.decodeFromString(authorization);
        byte[] pubKey = Base64Utils.decodeFromString(pubKeyUrl);
        String pubKeyAddr = new String(pubKey);
        if (!pubKeyAddr.startsWith("http://gosspublic.alicdn.com/") && !pubKeyAddr.startsWith("https://gosspublic.alicdn.com/")) {
            throw new RuntimeException("pub key addr must be oss address");
        }

        // requestUri
        requestUri = aliOssProperties.getCallbackServicePrefix() + requestUri;

        // pubKey resp
        String publicKey = requestPublicKey(pubKeyAddr);
        publicKey = publicKey.replace("-----BEGIN PUBLIC KEY-----", "");
        publicKey = publicKey.replace("-----END PUBLIC KEY-----", "");
        publicKey = publicKey.replace("\n", "");

        String decodeUri;
        try {
            decodeUri = java.net.URLDecoder.decode(requestUri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        String authStr = decodeUri;
        if (!StringUtils.isEmpty(queryString)) {
            authStr += "?" + queryString;
        }

        authStr += "\n" + callbackBodyStr;
        return checkRequest(authStr, authorizationByte, publicKey);
    }

    private boolean checkRequest(String content, byte[] sign, String publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64Utils.decodeFromString(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            java.security.Signature signature = java.security.Signature.getInstance("MD5withRSA");
            signature.initVerify(pubKey);
            signature.update(content.getBytes());

            return signature.verify(sign);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String requestPublicKey(String pubKeyUrl) {
        return restTemplate.getForObject(pubKeyUrl, String.class);
    }

    public void deleteObject(String sourceKey) {
        if (oss.doesObjectExist(aliOssProperties.getDefaultBucket(), sourceKey)) {
            oss.deleteObject(aliOssProperties.getDefaultBucket(), sourceKey);
        } else {
            throw new OSSException("源文件不存在");
        }
    }

    public void copyObject(String sourceKey, String destinationKey, Map<String, String> headers) {
        if (oss.doesObjectExist(aliOssProperties.getDefaultBucket(), sourceKey)) {
            ObjectMetadata objectMetadata = oss.getObjectMetadata(aliOssProperties.getDefaultBucket(), sourceKey);
            if (objectMetadata.getContentLength() >= 8589934592L) {
                try {
                    // 分片
                    long contentLength = objectMetadata.getContentLength();
                    long partSize = 1024 * 1024 * 10;
                    // 计算分片总数。
                    int partCount = (int) (contentLength / partSize);
                    if (contentLength % partSize != 0) {
                        partCount++;
                    }
                    // 初始化拷贝任务。可以通过InitiateMultipartUploadRequest指定目标文件元信息。
                    InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(aliOssProperties.getDefaultBucket(), destinationKey);
                    // 处理header
                    if (headers != null && !headers.isEmpty()) {
                        headers.forEach(initiateMultipartUploadRequest::addHeader);
                    }

                    InitiateMultipartUploadResult initiateMultipartUploadResult = oss.initiateMultipartUpload(initiateMultipartUploadRequest);
                    String uploadId = initiateMultipartUploadResult.getUploadId();
                    List<PartETag> partETags = new ArrayList<>();
                    for (int i = 0; i < partCount; i++) {
                        // 计算每个分片的大小。
                        long skipBytes = partSize * i;
                        long size = Math.min(partSize, contentLength - skipBytes);

                        // 创建UploadPartCopyRequest。可以通过UploadPartCopyRequest指定限定条件。
                        UploadPartCopyRequest uploadPartCopyRequest =
                                new UploadPartCopyRequest(aliOssProperties.getDefaultBucket(), sourceKey, aliOssProperties.getDefaultBucket(), destinationKey);
                        uploadPartCopyRequest.setUploadId(uploadId);
                        uploadPartCopyRequest.setPartSize(size);
                        uploadPartCopyRequest.setBeginIndex(skipBytes);
                        uploadPartCopyRequest.setPartNumber(i + 1);
                        UploadPartCopyResult uploadPartCopyResult = oss.uploadPartCopy(uploadPartCopyRequest);

                        // 将返回的分片ETag保存到partETags中。
                        partETags.add(uploadPartCopyResult.getPartETag());
                    }

                    // 提交分片拷贝任务。
                    CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(aliOssProperties.getDefaultBucket(), destinationKey, uploadId, partETags);
                    oss.completeMultipartUpload(completeMultipartUploadRequest);
                } catch (Exception e) {
                    throw new OSSException("Ali oss copy object failed, " + e.getMessage());
                }
            } else {
                // 简单拷贝
                try {
                    CopyObjectRequest copyObjectRequest = new CopyObjectRequest(aliOssProperties.getDefaultBucket(), sourceKey, aliOssProperties.getDefaultBucket(), destinationKey);
                    // 处理header
                    if (headers != null && !headers.isEmpty()) {
                        headers.forEach(copyObjectRequest::addHeader);
                    }
                    oss.copyObject(copyObjectRequest);
                } catch (Exception e) {
                    throw new OSSException("Ali oss copy object failed, " + e.getMessage());
                }
            }
        } else {
            throw new OSSException("源文件不存在");
        }
    }
    public void copyObject(String sourceKey, String destinationKey) {
        copyObject(sourceKey, destinationKey, null);
    }

    public OSSObject getObject(String object) {
        if (oss.doesObjectExist(aliOssProperties.getDefaultBucket(), object)) {
            return oss.getObject(aliOssProperties.getDefaultBucket(), object);
        } else {
            throw new OSSException("文件不存在");
        }
    }

    public boolean exist(String object) {
        return oss.doesObjectExist(aliOssProperties.getDefaultBucket(), object);
    }

    public void putObject(String objectName, InputStream inputStream) {
        oss.putObject(aliOssProperties.getDefaultBucket(), objectName, inputStream);
    }

    public OSS getBackendClient() {
        return oss;
    }
}
