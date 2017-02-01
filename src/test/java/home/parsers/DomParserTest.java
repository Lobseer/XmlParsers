package home.parsers;

import home.entity.Person;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class DomParserTest {

    @Test
    public void parsePerson() throws Exception {
        Person expected = new Person();
        expected.setId(1);
        expected.setFirstName("Test");
        expected.setLastName("Testing");
        expected.setLogin("testLogin");
        expected.setDocumentName("TestDocument");
        expected.setPages(50);
        ArrayList<String> docs = new ArrayList<>();
        docs.add("1.txt");
        docs.add("2.txt");
        docs.add("3.txt");
        docs.add("4.txt");
        expected.setDocs(docs);
        Person actually = DomParser.parsePerson("src/test/resources/test1.xml");
        Assert.assertNotNull(actually);
        Assert.assertEquals(expected, actually);
    }

    @Test(expected = IOException.class)
    public void docNotFound() throws Exception{
        DomParser.parsePerson("notExist.xml");
    }

    @Test(expected = IllegalArgumentException.class)
    public void docNotCorrect() throws Exception {
        DomParser.parsePerson("src/test/resources/test2.xml");
    }
}
