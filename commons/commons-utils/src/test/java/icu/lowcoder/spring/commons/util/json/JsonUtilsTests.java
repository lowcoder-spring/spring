package icu.lowcoder.spring.commons.util.json;

import lombok.Data;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class JsonUtilsTests {
    @Data
    public static class Person {
        private String name;
        private Integer age;
        private String idNo;
    }

    @Test
    public void test(){
        Person person = new Person();
        person.setName("张三");
        person.setAge(32);
        person.setIdNo("65335x");

        String json = JsonUtils.toJson(person);
        System.out.println(json);

        String snakeCaseJson = JsonUtils.toJson(person, PropertyNamingStrategy.SNAKE_CASE);
        System.out.println(snakeCaseJson);

        Person parsed = JsonUtils.parse(json, Person.class);

        assertEquals(person.getName(), parsed.getName());
        assertEquals(person.getAge(), parsed.getAge());
        assertEquals(person.getIdNo(), parsed.getIdNo());

        Person parsedFromSnakeCase = JsonUtils.parse(snakeCaseJson, Person.class, PropertyNamingStrategy.SNAKE_CASE);
        assertEquals(person.getName(), parsedFromSnakeCase.getName());
        assertEquals(person.getAge(), parsedFromSnakeCase.getAge());
        assertEquals(person.getIdNo(), parsedFromSnakeCase.getIdNo());
    }

    @Test
    public void testParseMap(){
        Person person = new Person();
        person.setName("张三");
        person.setAge(32);
        person.setIdNo("65335x");

        String snakeCaseJson = JsonUtils.toJson(person, PropertyNamingStrategy.SNAKE_CASE);
        System.out.println(snakeCaseJson);

        Map parsed = JsonUtils.parse(snakeCaseJson, Map.class, PropertyNamingStrategy.SNAKE_CASE);

        assertEquals(person.getName(), parsed.get("name"));
        assertEquals(person.getAge(), parsed.get("age"));
        assertEquals(person.getIdNo(), parsed.get("id_no"));
    }

    @Test
    public void testParseArray(){
        Person p1 = new Person();
        p1.setName("张三");
        p1.setAge(32);
        p1.setIdNo("65335x");
        Person p2 = new Person();
        p2.setName("李四");
        p2.setAge(26);
        p2.setIdNo("51235x");

        List<Person> people = new ArrayList<>();
        people.add(p1);
        people.add(p2);

        String snakeCaseJson = JsonUtils.toJson(people, PropertyNamingStrategy.SNAKE_CASE);
        System.out.println(snakeCaseJson);

        List<Person> parsed = JsonUtils.parseArray(snakeCaseJson, Person.class, PropertyNamingStrategy.SNAKE_CASE);
        assertEquals(parsed.size(), 2);
        assertEquals(parsed.get(0).getName(), p1.getName());
        assertEquals(parsed.get(1).getName(), p2.getName());
        assertEquals(parsed.get(0).getIdNo(), p1.getIdNo());
    }
}
