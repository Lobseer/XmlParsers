package home;

import home.entity.Person;
import home.parsers.XmlDomParser;

/**
 * Class description
 *
 * @author lobseer
 * @date 22.01.2017
 */

public class Main {
    public static void main(String[] args) {
        String pathPerson = "src/data/person.xml";
        String pathBook = "src/data/books.xml";
        try {
            XmlDomParser xdp = new XmlDomParser(pathPerson);
            for (Person p :xdp.parseAll(Person.class)) {
                System.out.println(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
