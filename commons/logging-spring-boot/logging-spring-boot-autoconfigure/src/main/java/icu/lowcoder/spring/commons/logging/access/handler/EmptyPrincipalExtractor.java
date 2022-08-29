package icu.lowcoder.spring.commons.logging.access.handler;

public class EmptyPrincipalExtractor implements StringPrincipalExtractor {
    @Override
    public String extract() {
        return null;
    }
}
