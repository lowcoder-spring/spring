package icu.lowcoder.spring.cloud.authentication.dao.specs;

import icu.lowcoder.spring.cloud.authentication.entity.Account;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class AccountSpecs {
    public static Specification<Account> keywordMatch(String keyword) {
        return (root, query, builder) -> {
            if (StringUtils.hasText(keyword)) {
                String trimmed = keyword.trim();
                return builder.or(
                        builder.like(root.get("name"), trimmed),
                        builder.like(root.get("phone"), trimmed),
                        builder.like(root.get("email"), trimmed)
                );
            }
            return builder.and();
        };
    }

}
