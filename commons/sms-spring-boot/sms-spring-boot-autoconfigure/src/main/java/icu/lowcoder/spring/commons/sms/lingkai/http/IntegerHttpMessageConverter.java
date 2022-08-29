package icu.lowcoder.spring.commons.sms.lingkai.http;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.NumberUtils;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class IntegerHttpMessageConverter extends AbstractHttpMessageConverter<Integer> {
    private boolean writeAcceptCharset;
    private volatile List<Charset> availableCharsets;

    public static final Charset DEFAULT_CHARSET = StandardCharsets.ISO_8859_1;

    public IntegerHttpMessageConverter(Charset defaultCharset) {
        super(defaultCharset, MediaType.valueOf("text/html;charset=gb2312"), MediaType.valueOf("text/html;charset=UTF-8"));
        this.writeAcceptCharset = true;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return Integer.class == clazz;
    }

    @Override
    protected Integer readInternal(Class<? extends Integer> clazz, HttpInputMessage inputMessage) throws HttpMessageNotReadableException, IOException {
        Charset charset = this.getContentTypeCharset(inputMessage.getHeaders().getContentType());
        return NumberUtils.parseNumber(StreamUtils.copyToString(inputMessage.getBody(), charset), Integer.class);
    }

    @Override
    protected void writeInternal(Integer integer, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (this.writeAcceptCharset) {
            outputMessage.getHeaders().setAcceptCharset(this.getAcceptedCharsets());
        }

        Charset charset = this.getContentTypeCharset(outputMessage.getHeaders().getContentType());
        StreamUtils.copy(integer.toString(), charset, outputMessage.getBody());
    }

    protected List<Charset> getAcceptedCharsets() {
        if (this.availableCharsets == null) {
            this.availableCharsets = new ArrayList<>(Charset.availableCharsets().values());
        }

        return this.availableCharsets;
    }

    private Charset getContentTypeCharset(MediaType contentType) {
        return contentType != null && contentType.getCharset() != null ? contentType.getCharset() : DEFAULT_CHARSET;
    }


}
