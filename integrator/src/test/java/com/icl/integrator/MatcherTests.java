package com.icl.integrator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icl.integrator.services.JsonMatcher;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e.shahmaev on 02.04.2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(
        locations = {"classpath:/applicationContext.xml", "classpath:/integrator-servlet.xml"})
public class MatcherTests {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JsonMatcher jsonMatcher;

    @Test
    public void testMatcherGeneral() throws Exception {
        TestClass reference = new TestClass();
        reference.string = "STRING";
        JsonNode referenceJson = mapper.valueToTree(reference);

        TestClass data = new TestClass();
        data.integer = 5;
        data.string = "STRING";
        data.nested = new Nested();
        JsonNode dataJson = mapper.valueToTree(data);

        Assert.assertTrue(jsonMatcher.matches(dataJson, referenceJson));

        data.string = "!F#";
        dataJson = mapper.valueToTree(data);
        Assert.assertFalse(jsonMatcher.matches(dataJson, referenceJson));
    }

    @Test
    public void testMatcherNested() throws Exception {
        TestClass reference = new TestClass();
        reference.integer = 415;

        Nested nested = new Nested();
        nested.integer = 100;
        reference.nested = nested;
        JsonNode referenceJson = mapper.valueToTree(reference);

        TestClass data = new TestClass();
        data.integer = 415;
        data.string = "STRING";
        Nested nested2 = new Nested();
        nested2.integer = 100;
        nested2.string = "WHATEVER";
        data.nested = nested2;
        JsonNode dataJson = mapper.valueToTree(data);

        Assert.assertTrue(jsonMatcher.matches(dataJson, referenceJson));

        data.integer = 150;
        dataJson = mapper.valueToTree(data);

        Assert.assertFalse(jsonMatcher.matches(dataJson, referenceJson));
    }

    @Test
    public void testMatcherList() throws Exception {
        Nested nested = new Nested();
        nested.integer = 100;

        TestClass reference = new TestClass();
        reference.string = "STRING";
        reference.nestedList = new ArrayList<>();
        reference.nestedList.add(nested);
        JsonNode referenceJson = mapper.valueToTree(reference);

        TestClass data = new TestClass();
        data.integer = 500;
        data.string = "STRING";
        data.nested = new Nested();
        data.nestedList = new ArrayList<>();
        data.nestedList.add(nested);
        JsonNode dataJson = mapper.valueToTree(data);

        Assert.assertTrue(jsonMatcher.matches(dataJson, referenceJson));

        Nested nested2 = new Nested();
        nested2.integer = 100;
        nested2.string = "WHATEVER";
        data.nestedList.add(nested2);

        dataJson = mapper.valueToTree(data);

        Assert.assertFalse(jsonMatcher.matches(dataJson, referenceJson));
    }

    private static class Nested {

        private String string;

        private Integer integer;

        private Nested() {
        }

        public Integer getInteger() {
            return integer;
        }

        public void setInteger(Integer integer) {
            this.integer = integer;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }
    }

    private static class TestClass {

        private Integer integer;

        private String string;

        private Nested nested;

        private List<Nested> nestedList;

        private TestClass() {
        }

        public Integer getInteger() {
            return integer;
        }

        public void setInteger(Integer integer) {
            this.integer = integer;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public Nested getNested() {
            return nested;
        }

        public void setNested(Nested nested) {
            this.nested = nested;
        }

        public List<Nested> getNestedList() {
            return nestedList;
        }

        public void setNestedList(List<Nested> nestedList) {
            this.nestedList = nestedList;
        }
    }
}
