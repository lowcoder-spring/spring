package icu.lowcoder.spring.commons.util.spring;

public class BeanUtils extends org.springframework.beans.BeanUtils {
    public static <T> T instantiate(Class<T> type, Object propertiesBean, String... ignoreProperties) {
        T instantiate = instantiateClass(type);
        copyProperties(propertiesBean, instantiate, ignoreProperties);
        return instantiate;
    }

    public static <T> T instantiate(Class<T> type, Object propertiesBean) {
        T instantiate = instantiateClass(type);
        copyProperties(propertiesBean, instantiate);
        return instantiate;
    }
}
