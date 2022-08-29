package icu.lowcoder.spring.commons.util.spring;

import lombok.Data;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BeanUtilsTests {
    @Data
    static class Person {
        private String name;
        private Integer age;
        private String idNo;
    }

    @Data
    static class Child extends Person {
        private String favorite;
    }

    @Test
    public void testInstantiate(){
        Person person = new Person();
        person.setName("张三");
        person.setAge(7);
        person.setIdNo("65335x");

        Child child = BeanUtils.instantiate(Child.class, person);
        child.setFavorite("watch tv");

        assertEquals(person.getName(), child.getName());
        assertEquals(person.getAge(), child.getAge());
        assertEquals(person.getIdNo(), child.getIdNo());
    }
}
