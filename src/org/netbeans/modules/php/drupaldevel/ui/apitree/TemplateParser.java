/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.netbeans.modules.php.drupaldevel.ui.apitree;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Jamie Holly <jamie@hollyit.net>
 */
public class TemplateParser {

    private Element element;

    public TemplateParser(String path) {

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(path);
            NodeList nodes = doc.getElementsByTagName("item");
            for (int i = 0; i < nodes.getLength(); i++) {
                element = (Element) nodes.item(i);


            }
        } catch (Exception e) {
        }
    }

    public String getTag(String item) {
        NodeList title = element.getElementsByTagName(item);
        Element line = (Element) title.item(0);
        return getCharacterDataFromElement(line);
    }

    private static String getCharacterDataFromElement(Element e) {
        try {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        } catch (Exception ex){
            
        }
        return "";
    }
}
