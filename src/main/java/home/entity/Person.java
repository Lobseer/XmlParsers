package home.entity;

import home.anno.XmlContainer;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * Class description
 *
 * @author lobseer
 * @version 19.12.2016
 */

@XmlRootElement(name = "person")
public class Person {
    //public static final Logger LOG = Logger.getLogger(Person.class);
    @XmlAttribute(name = "id")
    private int id;
    @XmlElement(name = "firstName")
    private String firstName;
    @XmlElement(name = "lastName")
    private String lastName;
    @XmlElement(name = "login")
    private String login;
    @XmlElement(name = "documentName")
    private String documentName;
    @XmlElement(name = "pages")
    private int pages;

    @XmlContainer(containType = String.class)
    @XmlElement(name = "docs")
    private ArrayList<String> docs;

    public Person() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public ArrayList<String> getDocs() {
        return docs;
    }

    public void setDocs(ArrayList<String> docs) {
        this.docs = docs;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==this) return true;
        if(obj == null || !(obj instanceof Person)) return false;
        Person temp = (Person)obj;
        if(this.id != temp.id) return false;
        if(!this.firstName.equals(temp.firstName)) return false;
        if(!this.lastName.equals(temp.lastName)) return false;
        if(!this.login.equals(temp.login)) return false;
        if(!this.documentName.equals(temp.documentName)) return false;
        if(this.pages != temp.pages) return false;
        if(this.docs.size() != temp.docs.size()) return false;
        else {
            for(int i = 0; i<this.docs.size(); i++) {
                if(!this.docs.get(i).equals(temp.docs.get(i))) return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "home.entity.Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", login='" + login + '\'' +
                ", documentName='" + documentName + '\'' +
                ", pages=" + pages +
                ", docs=" + docs +
                '}';
    }
}
