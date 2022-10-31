package icu.lowcoder.spring.commos.cloudapi;

import icu.lowcoder.spring.commos.cloudapi.model.IdCardCheckResponse;

public interface RealNameApi extends CloudApi {

    IdCardCheckResponse idCardCheck(String name, String idCard);

    @Override
    default ApiName getName() {
        return ApiName.REAL_NAME;
    }
}
