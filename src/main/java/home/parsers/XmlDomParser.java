package home.parsers;

import home.anno.XmlContainer;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class description
 *
 * @author lobseer on 01.02.2017.
 */

public class XmlDomParser {
    private static Logger log = Logger.getLogger(XmlDomParser.class);
    private Document document;
    private String resourcePath;

    public XmlDomParser(String documentPath) {
        try {
            resourcePath = documentPath;
            // Создается построитель документа
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Создается дерево DOM документа из файла
            this.document = documentBuilder.parse(documentPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Функция получить все сущности из корневого элемента
     * @param type class of entity
     * @param <T> type of entity
     * @return list of parsed entities
     */
    public <T> List<T> parseAll(Class<? extends T> type) {
        Element root = document.getDocumentElement();
        List<T> result = parse(type, root);
        return result;
    }

    /**
     * Функция для добавления в корень документа
     * @param entity the entity
     * @param <T> type of entity
     */
    public <T> void addIntoRoot(T entity) {
        Element root = document.getDocumentElement();
        addInto(entity, root);
        writeDocument(resourcePath);
    }

    /**
     * Функция получения списка сущностей из заданого корневого элемента.
     * Для задания полей класса сущности используется рефлексивный доступ(в том чисте и к закрытым полям)
     * @param type the class of the entity which must be parsed
     * @param root the root xml element
     * @param <T> the type of entity which must be parsed
     * @return list of entities which were in the root element
     */
    private <T> List<T> parse(Class<? extends T> type, Element root) {
        List<T> result = new ArrayList<>();
        try {
            for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element tempRoot = (Element) node;
                    if (type.isAnnotationPresent(XmlRootElement.class)) {
                        if (node.getNodeName().equals(type.getAnnotation(XmlRootElement.class).name())) {
                            Object entity = type.newInstance();
                            for (Field field : type.getDeclaredFields()) {
                                if (field.isAnnotationPresent(XmlElement.class)) {
                                    XmlElement xmlParam = field.getAnnotation(XmlElement.class);
                                    if (!field.isAnnotationPresent(XmlContainer.class)) {
                                        field.setAccessible(true);
                                        String value = tempRoot.getElementsByTagName(xmlParam.name()).item(0).getNodeValue();
                                        field.set(entity, field.getType().cast(value));
                                        field.setAccessible(false);
                                    } else {
                                        XmlContainer xmlContainer = field.getAnnotation(XmlContainer.class);
                                        field.setAccessible(true);
                                        List tempVal = parse(xmlContainer.containType(), (Element) tempRoot.getElementsByTagName(xmlParam.name()).item(0));
                                        field.set(entity, tempVal);
                                        field.setAccessible(false);
                                    }
                                } else if (field.isAnnotationPresent(XmlAttribute.class)) {
                                    XmlAttribute xmlParam = field.getAnnotation(XmlAttribute.class);
                                    field.setAccessible(true);
                                    Class fType = field.getType();
                                    String value = tempRoot.getAttribute(xmlParam.name());
                                    field.set(entity, fType.cast(parseStringToType(value, fType)));
                                    field.setAccessible(false);
                                }
                            }
                            result.add((T) entity);
                        }
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception pe) {
            pe.printStackTrace();
        }
        return result;
    }

    /**
     * Функция получения списка сущностей из заданого корневого элемента.
     * Для задания полей класса сущности используется конструктор.
     * Порядок и количество параметров конструктора должно соответствовать порядку и количеству считываемых полей
     * @param type the class of the entity which must be parsed
     * @param root the root xml element
     * @param <T> the type of entity which must be parsed
     * @return list of entities which were in the root element
     */
    private <T> List<T> parseWithConstructor(Class<? extends T> type, Element root) {
        List<T> result = new LinkedList<>();
        try {
            for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element tempRoot = (Element) node;
                    if (type.isAnnotationPresent(XmlRootElement.class)) {
                        if (node.getNodeName().equals(type.getAnnotation(XmlRootElement.class).name())) {
                            List<Class> params = new LinkedList<>();
                            List<Object> vals = new LinkedList<>();
                            for (Field field : type.getDeclaredFields()) {
                                if (field.isAnnotationPresent(XmlElement.class)) {
                                    XmlElement xmlParam = field.getAnnotation(XmlElement.class);
                                    if (!field.isAnnotationPresent(XmlContainer.class)) {

                                    } else {
                                        XmlContainer xmlContainer = field.getAnnotation(XmlContainer.class);
                                        params.add(field.getType());
                                        vals.add(parse(xmlContainer.containType(), (Element) tempRoot.getElementsByTagName(xmlParam.name()).item(0)));
                                    }
                                } else if (field.isAnnotationPresent(XmlAttribute.class)) {
                                    XmlAttribute xmlParam = field.getAnnotation(XmlAttribute.class);
                                    Class fType = field.getType();
                                    String value = tempRoot.getAttribute(xmlParam.name());
                                    params.add(fType);
                                    vals.add(fType.cast(parseStringToType(value, fType)));
                                }
                            }
                            Class[] p = new Class[params.size()];
                            for (int i = 0; i < params.size(); i++) {
                                p[i] = params.get(i);
                            }
                            Object entity = type.getConstructor(p).newInstance(vals.toArray());
                            result.add((T) entity);
                        }
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException mEx) {

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * Функция добавления сущности в заданый корневой элемент
     * @param entity the addable entity
     * @param root the element in which will be added the entity
     * @param <T> the type of entity
     */
    private <T> void addInto(T entity, Element root) {
        try {
            //Element root = document.getDocumentElement();
            Class<?> type = entity.getClass();
            if (type.isAnnotationPresent(XmlRootElement.class)) {
                Element mainEntityElement = document.createElement(type.getAnnotation(XmlRootElement.class).name());
                for (Field field : type.getDeclaredFields()) {
                    if (field.isAnnotationPresent(XmlElement.class)) {
                        XmlElement xmlParam = field.getAnnotation(XmlElement.class);
                        if (!field.isAnnotationPresent(XmlContainer.class)) {
                            Element el = document.createElement(xmlParam.name());
                            field.setAccessible(true);
                            el.setTextContent(field.get(entity).toString());
                            field.setAccessible(false);
                            mainEntityElement.appendChild(el);
                        } else {
                            Element rEl = document.createElement(xmlParam.name());
                            field.setAccessible(true);
                            List tempRoot = (List) field.get(entity);
                            field.setAccessible(false);
                            for (Object ob : tempRoot)
                                addInto(ob, rEl);
                            mainEntityElement.appendChild(rEl);
                        }
                    } else if (field.isAnnotationPresent(XmlAttribute.class)) {
                        XmlAttribute xmlParam = field.getAnnotation(XmlAttribute.class);
                        field.setAccessible(true);
                        mainEntityElement.setAttribute(xmlParam.name(), field.get(entity).toString());
                        field.setAccessible(false);
                    }
                }
                root.appendChild(mainEntityElement);
            }
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Функция преобразования строки в примитивы
     * @param str the original string
     * @param type type which will be converted string
     * @return the parsed object
     */
    private Object parseStringToType(String str, Class type) {

        switch (type.getSimpleName()) {
            case "Boolean":
                return Boolean.parseBoolean(str);
            case "Character":
                return str;
            case "String":
                return str;
            case "Byte":
                return Byte.parseByte(str);
            case "Short":
                return Short.parseShort(str);
            case "int":
            case "Integer":
                return Integer.parseInt(str);
            case "BigInteger":
                return BigInteger.valueOf(Long.parseLong(str));
            case "Long":
                return Long.parseLong(str);
            case "Float":
                return Float.parseFloat(str);
            case "Double":
                return Double.parseDouble(str);
            case "BigDecimal":
                return BigDecimal.valueOf(Double.parseDouble(str));
            default:
                return null;
        }
    }

    /**
     * Функция для сохранения DOM в файл
     * @param resource the path to xml resource
     */
    private void writeDocument(String resource) {
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
