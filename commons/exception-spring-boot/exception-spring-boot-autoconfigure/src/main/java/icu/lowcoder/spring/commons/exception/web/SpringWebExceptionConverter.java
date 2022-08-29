package icu.lowcoder.spring.commons.exception.web;

import icu.lowcoder.spring.commons.exception.UnifiedExceptionResponse;
import icu.lowcoder.spring.commons.exception.converter.UnifiedExceptionConverter;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SpringWebExceptionConverter implements UnifiedExceptionConverter<Exception> {
    @SuppressWarnings("rawtypes")
    private static final List<Class> supports = Arrays.asList(
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            HttpMediaTypeNotAcceptableException.class,
            MissingPathVariableException.class,
            MissingServletRequestParameterException.class,
            ServletRequestBindingException.class,
            ConversionNotSupportedException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            MethodArgumentNotValidException.class,
            MissingServletRequestPartException.class,
            BindException.class,
            NoHandlerFoundException.class,
            AsyncRequestTimeoutException.class,
            HttpStatusCodeException.class
    );

    @Override
    public UnifiedExceptionResponse convert(Exception ex) {
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
            return handleHttpRequestMethodNotSupported((HttpRequestMethodNotSupportedException) ex, status);
        } else if (ex instanceof HttpMediaTypeNotSupportedException) {
            HttpStatus status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
            return handleHttpMediaTypeNotSupported((HttpMediaTypeNotSupportedException) ex, status);
        } else if (ex instanceof HttpMediaTypeNotAcceptableException) {
            HttpStatus status = HttpStatus.NOT_ACCEPTABLE;
            return handleHttpMediaTypeNotAcceptable((HttpMediaTypeNotAcceptableException) ex, status);
        } else if (ex instanceof MissingPathVariableException) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleMissingPathVariable((MissingPathVariableException) ex, status);
        } else if (ex instanceof MissingServletRequestParameterException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleMissingServletRequestParameter((MissingServletRequestParameterException) ex, status);
        } else if (ex instanceof UnsatisfiedServletRequestParameterException) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            return handleUnsatisfiedServletRequestParameterException((ServletRequestBindingException) ex, status);
        } else if (ex instanceof ServletRequestBindingException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleServletRequestBindingException((ServletRequestBindingException) ex, status);
        } else if (ex instanceof ConversionNotSupportedException) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleConversionNotSupported((ConversionNotSupportedException) ex, status);
        } else if (ex instanceof TypeMismatchException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleTypeMismatch((TypeMismatchException) ex, status);
        } else if (ex instanceof HttpMessageNotReadableException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleHttpMessageNotReadable((HttpMessageNotReadableException) ex, status);
        } else if (ex instanceof HttpMessageNotWritableException) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleHttpMessageNotWritable((HttpMessageNotWritableException) ex, status);
        } else if (ex instanceof MethodArgumentNotValidException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleMethodArgumentNotValid((MethodArgumentNotValidException) ex, status);
        } else if (ex instanceof MissingServletRequestPartException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleMissingServletRequestPart((MissingServletRequestPartException) ex, status);
        } else if (ex instanceof BindException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return handleBindException((BindException) ex, status);
        } else if (ex instanceof NoHandlerFoundException) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            return handleNoHandlerFoundException((NoHandlerFoundException) ex, status);
        } else if (ex instanceof AsyncRequestTimeoutException) {
            HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
            return handleAsyncRequestTimeoutException((AsyncRequestTimeoutException) ex, status);
        } else if (ex instanceof HttpStatusCodeException) {
            return handleHttpStatusCodeExceptionConverter((HttpStatusCodeException) ex);
        }

        return UnifiedExceptionResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .exception(ex.getClass().getName())
                .message("未处理的异常")
                .build();
    }

    private UnifiedExceptionResponse handleUnsatisfiedServletRequestParameterException(ServletRequestBindingException ex, HttpStatus status) {
        return convertInternal(ex, status, "访问的接口不存在");
    }

    private UnifiedExceptionResponse handleHttpStatusCodeExceptionConverter(HttpStatusCodeException ex) {
        return convertInternal(ex, ex.getStatusCode(), ex.getStatusText());
    }

    protected UnifiedExceptionResponse handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpStatus status) {
        return convertInternal(ex, status, "不支持的方法：" + ex.getMethod());
    }

    protected UnifiedExceptionResponse handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpStatus status) {

        return convertInternal(ex, status, "不支持的类型：" + Objects.requireNonNull(ex.getContentType()).getType());
    }

    protected UnifiedExceptionResponse handleHttpMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException ex, HttpStatus status) {

        return convertInternal(ex, status, "不可接受的类型");
    }

    protected UnifiedExceptionResponse handleMissingPathVariable(
            MissingPathVariableException ex, HttpStatus status) {

        return convertInternal(ex, status, "缺少路径参数:" + ex.getVariableName());
    }

    protected UnifiedExceptionResponse handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpStatus status) {

        return convertInternal(ex, status, "缺少参数:" + ex.getParameterName());
    }

    protected UnifiedExceptionResponse handleServletRequestBindingException(
            ServletRequestBindingException ex, HttpStatus status) {
        // TODO 什么时候抛出这类异常
        return convertInternal(ex, status, "请求绑定异常");
    }

    protected UnifiedExceptionResponse handleConversionNotSupported(
            ConversionNotSupportedException ex, HttpStatus status) {

        return convertInternal(ex, status, "不支持的类型转换:" + ex.getPropertyName());
    }

    protected UnifiedExceptionResponse handleTypeMismatch(
            TypeMismatchException ex, HttpStatus status) {

        return convertInternal(ex, status, "类型不匹配:" + ex.getPropertyName());
    }

    protected UnifiedExceptionResponse handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpStatus status) {
        String message = "无法解析的请求";
        if (ex.getCause() != null && ex.getCause() instanceof InvalidFormatException) {
            message = "无法解析的json请求";
        }
        return convertInternal(ex, status, message);
    }

    protected UnifiedExceptionResponse handleHttpMessageNotWritable(
            HttpMessageNotWritableException ex, HttpStatus status) {

        return convertInternal(ex, status, "HTTP消息无法写入");
    }

    protected UnifiedExceptionResponse handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpStatus status) {

        String message = "参数验证不通过";
        BindingResult bindingResult = ex.getBindingResult();
        if (bindingResult.hasErrors()) {
            List<String> messages = new ArrayList<>();
            List<ObjectError> list = bindingResult.getAllErrors();

            for (ObjectError error : list) {
                if (error instanceof FieldError) {
                    messages.add(((FieldError) error).getField() + error.getDefaultMessage());
                } else {
                    messages.add(error.getObjectName() + error.getDefaultMessage());
                }
            }


            message = StringUtils.arrayToDelimitedString(messages.toArray(new String[0]), ";");
        }

        return convertInternal(ex, status, message);
    }

    protected UnifiedExceptionResponse handleMissingServletRequestPart(
            MissingServletRequestPartException ex, HttpStatus status) {

        return convertInternal(ex, status, "缺少请求片段:" + ex.getRequestPartName());
    }

    protected UnifiedExceptionResponse handleBindException(
            BindException ex, HttpStatus status) {
        String message = "参数验证不通过";
        BindingResult bindingResult = ex.getBindingResult();
        if (bindingResult.hasErrors()) {
            List<String> messages = new ArrayList<>();
            List<ObjectError> list = bindingResult.getAllErrors();

            for (ObjectError error : list) {
                if (error instanceof FieldError) {
                    messages.add(((FieldError) error).getField() + error.getDefaultMessage());
                } else {
                    messages.add(error.getObjectName() + error.getDefaultMessage());
                }
            }


            message = StringUtils.arrayToDelimitedString(messages.toArray(new String[0]), ";");
        }

        return convertInternal(ex, status, message);
    }

    protected UnifiedExceptionResponse handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpStatus status) {

        return convertInternal(ex, status, "请求的资源不存在");
    }

    @Nullable
    protected UnifiedExceptionResponse handleAsyncRequestTimeoutException(
            AsyncRequestTimeoutException ex, HttpStatus status) {

        return convertInternal(ex, status, "异步请求超时");
    }

    private UnifiedExceptionResponse convertInternal(Exception e, HttpStatus status, String message) {
        return UnifiedExceptionResponse.builder()
                .status(status.value())
                .exception(e.getClass().getName())
                .message(message)
                .build();
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean support(Class<?> clazz) {
        for (Class support : supports) {
            if (support.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }
}
