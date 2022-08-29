package icu.lowcoder.spring.commons.sms;

import org.springframework.util.StringUtils;

public class PhoneNumberUtils {
    public static boolean isPhoneNumber(String s) {
        return StringUtils.hasText(s) && s.matches("\\d{11}");
    }
}
