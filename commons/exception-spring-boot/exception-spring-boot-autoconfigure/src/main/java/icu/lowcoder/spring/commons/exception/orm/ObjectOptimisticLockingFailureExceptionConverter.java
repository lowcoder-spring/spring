package icu.lowcoder.spring.commons.exception.orm;

import icu.lowcoder.spring.commons.exception.UnifiedExceptionResponse;
import icu.lowcoder.spring.commons.exception.converter.UnifiedExceptionConverter;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

public class ObjectOptimisticLockingFailureExceptionConverter implements UnifiedExceptionConverter<ObjectOptimisticLockingFailureException> {
    @Override
    public UnifiedExceptionResponse convert(ObjectOptimisticLockingFailureException e) {
        return UnifiedExceptionResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("系统繁忙，请稍后重试")
                .exception(e.getClass().getName())
                .build();
    }

    @Override
    public boolean support(Class<?> clazz) {
        return ObjectOptimisticLockingFailureException.class.isAssignableFrom(clazz);
    }
}
