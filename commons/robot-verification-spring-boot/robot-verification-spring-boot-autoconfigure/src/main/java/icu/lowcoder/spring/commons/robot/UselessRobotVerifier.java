package icu.lowcoder.spring.commons.robot;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class UselessRobotVerifier implements RobotVerifier {
    @Override
    public boolean allow(HttpServletRequest request, Map<String, String> params) {
        // 该实现始终返回为true
        return true;
    }

    @Override
    public String getName() {
        return "USELESS";
    }
}
