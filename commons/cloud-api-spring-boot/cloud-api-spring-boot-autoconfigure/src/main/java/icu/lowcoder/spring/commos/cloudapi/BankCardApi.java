package icu.lowcoder.spring.commos.cloudapi;

import icu.lowcoder.spring.commos.cloudapi.model.BankCardCheckResponse;

public interface BankCardApi extends CloudApi {
    BankCardCheckResponse bankCardOwnerCheck(String name, String idCard, String bankCardNo);

    @Override
    default ApiName getName() {
        return ApiName.BANK_CARD;
    }
}
