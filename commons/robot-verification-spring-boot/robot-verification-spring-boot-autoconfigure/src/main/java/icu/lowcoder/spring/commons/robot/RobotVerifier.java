package icu.lowcoder.spring.commons.robot;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface RobotVerifier {
    boolean allow(HttpServletRequest request, Map<String, String> params);
}
