package icu.lowcoder.spring.commons.wechat.util;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Provider;
import java.security.Security;

@Slf4j
public class WeChatAESInfoDecoder {
    // 算法名称
    private static final String KEY_ALGORITHM = "AES";
    private static final String PROVIDER_NAME = "BC";
    private static final BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();

    // 加解密算法/模式/填充方式
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";

    public static String decrypt(String encryptedDataStr, String keyBytesStr, String ivStr) {
        byte[] encryptedText;
        byte[] encryptedData;
        byte[] sessionKey;
        byte[] iv;

        try {
            sessionKey = Base64Utils.decodeFromString(keyBytesStr);
            encryptedData = Base64Utils.decodeFromString(encryptedDataStr);
            iv = Base64Utils.decodeFromString(ivStr);

            Provider provider = Security.getProvider(PROVIDER_NAME);
            if (provider == null) {
                Security.addProvider(bouncyCastleProvider);
            }

            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM, PROVIDER_NAME);
            Key keySpec = new SecretKeySpec(sessionKey, KEY_ALGORITHM);

            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
            encryptedText = cipher.doFinal(encryptedData);
            return new String(encryptedText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("wechat AES Info decoder decrypt exception", e);
        }

        return null;
    }
}
