package icu.lowcoder.spring.commons.util.json;

import java.util.List;

public interface JsonProcessor {
    String serialize(Object javaObject);

    String serialize(Object javaObject, PropertyNamingStrategy propertyNamingStrategy);

    <T> T deserialize(String json, Class<T> type);

    <T> T deserialize(String json, Class<T> type, PropertyNamingStrategy propertyNamingStrategy);

    <T> List<T> deserializeArray(String json, Class<T> itemType);

    <T> List<T> deserializeArray(String json, Class<T> itemType, PropertyNamingStrategy propertyNamingStrategy);
}
