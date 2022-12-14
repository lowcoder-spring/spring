package icu.lowcoder.spring.commons.robot;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class DelegateRobotVerifier implements RobotVerifier {

    private final Map<String, RobotVerifier> verifierMap = new HashMap<>();

    private String defaultTester;

    public DelegateRobotVerifier() {
    }

    public DelegateRobotVerifier(Map<String, RobotVerifier> testers, String defaultName) {
        this();
        this.verifierMap.putAll(testers);
        this.setDefaultTester(defaultName);
    }

    @Override
    public boolean allow(HttpServletRequest request, Map<String, String> params) {
        String testerName = params.get("captcha_type");
        if (StringUtils.hasText(testerName)) {
            RobotVerifier tester = verifierMap.get(testerName.toUpperCase());
            if (tester != null) {
                return tester.allow(request, params);
            } else {
                return false;
            }
        } else if (StringUtils.hasText(defaultTester) && verifierMap.containsKey(defaultTester.toUpperCase())) {
            // use default verifier
            return verifierMap.get(defaultTester).allow(request, params);
        }

        return false;
    }

    @Override
    public String getName() {
        return "DELEGATE";
    }

    public void setDefaultTester(String tester) {
        this.defaultTester = tester.toUpperCase();
    }

    public void addTester(String name, RobotVerifier tester) {
        if (tester instanceof DelegateRobotVerifier) {
            throw new RuntimeException("emm..");
        }

        verifierMap.put(name.toUpperCase(), tester);
    }
}
