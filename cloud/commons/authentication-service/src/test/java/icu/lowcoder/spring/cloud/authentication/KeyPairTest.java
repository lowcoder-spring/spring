package icu.lowcoder.spring.cloud.authentication;

import org.junit.jupiter.api.Test;
import org.springframework.util.Base64Utils;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KeyPairTest {

    @Test
    public void testKeyPairGenerator() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        Key publicKey = keyPair.getPublic();

        Key privateKey = keyPair.getPrivate();

        System.out.println(Base64Utils.encodeToString(privateKey.getEncoded()));
        System.out.println(Base64Utils.encodeToString(publicKey.getEncoded()));

    }
}
