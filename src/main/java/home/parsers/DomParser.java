package home.parsers;

import home.entity.Person;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Class description
 *
 * @author lobseer
 * @version 19.12.2016
 */

public class DomParser {

    public static Person parsePerson(String path) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(path);

        Element rootElement = document.getDocumentElement();
        Person person = new Person();
        person.setId(Integer.parseInt(rootElement.getAttribute("id")));

        for (Node node = rootElement.getFirstChild(); node != null; node = node.getNextSibling())
        {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if ("name".equals(node.getNodeName())) {
                    for (Node nameNode = node.getFirstChild(); nameNode != null; nameNode = nameNode.getNextSibling()) {

                        if (nameNode.getNodeType() == Node.ELEMENT_NODE) {
                            if ("firstName".equals(nameNode.getNodeName())) {
                                person.setFirstName(nameNode.getFirstChild().getNodeValue());
                            } else if ("lastName".equals(nameNode.getNodeName())) {
                                person.setLastName(nameNode.getFirstChild().getNodeValue());
                            }
                        }
                    }
                } else if ("login".equals(node.getNodeName())) {
                    person.setLogin(node.getFirstChild().getNodeValue());
                } else if ("permission".equals(node.getNodeName())) {
                    for (Node nameNode = node.getFirstChild(); nameNode != null; nameNode = nameNode.getNextSibling()) {
                        if (nameNode.getNodeType() == Node.ELEMENT_NODE) {
                            if ("document".equals(nameNode.getNodeName())) {
                                person.setDocumentName(nameNode.getFirstChild().getNodeValue());
                            } else if ("pages".equals(nameNode.getNodeName())) {
                                person.setPages(Integer.parseInt(nameNode.getFirstChild().getNodeValue()));
                            }
                        }
                    }
                } else if ("documents".equals(node.getNodeName())) {
                    ArrayList<String> docs = new ArrayList<>();
                    for (Node nameNode = node.getFirstChild(); nameNode != null; nameNode = nameNode.getNextSibling()) {
                        if (nameNode.getNodeType() == Node.ELEMENT_NODE) {
                            if ("doc".equals(nameNode.getNodeName())) {
                                docs.add(nameNode.getFirstChild().getNodeValue());
                            }
                        }
                    }
                    person.setDocs(docs);
                }
            }
        }
        return person;
    }

    private static String searchBookXPath(Document document, String author, float maxCost) {
        XPath path = XPathFactory.newInstance().newXPath();
        try {
            XPathExpression ext = path.compile(String.format("//Book[Author='%s' and Cost<%s]/Title", author, maxCost));
            NodeList result = (NodeList) ext.evaluate(document, XPathConstants.NODESET);
            return result.item(0).getTextContent();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String searchBook(Document document, String author, float maxCost) {
        NodeList tempAuthor = document.getDocumentElement().getElementsByTagName("Author");
        for(int i =0; i< tempAuthor.getLength(); i++) {
            if(tempAuthor.item(i).getTextContent().equals(author)) {
                Element book = (Element) tempAuthor.item(i).getParentNode();
                if (Float.parseFloat(book.getElementsByTagName("Cost").item(0).getTextContent()) <= maxCost)
                    return book.getElementsByTagName("Title").item(0).getTextContent();
            }
        }
        return null;
    }

    private static String parseAll(Document document) {
        StringBuilder result = new StringBuilder();
        Element root = document.getDocumentElement();
        for(Node node = root.getFirstChild(); node!=null; node = node.getNextSibling()) {
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                result.append(node);
            }
        }
        return result.toString();
    }

    // Функция добавления новой книги и записи результата в файл
    private static void addNewBook(Document document) throws TransformerFactoryConfigurationError, DOMException {
        // Получаем корневой элемент
        Node root = document.getDocumentElement();

        // Создаем новую книгу по элементам
        // Сама книга <Book>
        Element book = document.createElement("Book");
        // <Title>
        Element title = document.createElement("Title");
        // Устанавливаем значение текста внутри тега
        title.setTextContent("Incredible book about Java");
        // <Author>
        Element author = document.createElement("Author");
        author.setTextContent("Saburov Anton");
        // <Date>
        Element date = document.createElement("Date");
        date.setTextContent("2015");
        // <ISBN>
        Element isbn = document.createElement("ISBN");
        isbn.setTextContent("0-06-999999-9");
        // <Publisher>
        Element publisher = document.createElement("Publisher");
        publisher.setTextContent("Java-Course publisher");
        // <Cost>
        Element cost = document.createElement("Cost");
        cost.setTextContent("499");
        // Устанавливаем атрибут
        cost.setAttribute("currency", "RUB");

        // Добавляем внутренние элементы книги в элемент <Book>
        book.appendChild(title);
        book.appendChild(author);
        book.appendChild(date);
        book.appendChild(isbn);
        book.appendChild(publisher);
        book.appendChild(cost);
        // Добавляем книгу в корневой элемент
        root.appendChild(book);
    }


    // Функция для сохранения DOM в файл
    private static void writeDocument(Document document, String resource) throws TransformerFactoryConfigurationError {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(document);
            FileOutputStream fos = new FileOutputStream(resource);
            StreamResult result = new StreamResult(fos);
            tr.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
