/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.entities;

import java.io.File;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author gchen
 */
public class XmlParserUtil {

    public static void parse() throws Exception {
        SAXBuilder saxBuilder = new SAXBuilder();
        File input = new File("src\\cn\\com\\xiaofabo\\scia\\aiaward\\rules\\TextRules.xml");
        Document doc = saxBuilder.build(input);
        
        Element root = doc.getRootElement();
        Element in = root.getChild("InputData");
        Element general = in.getChild("General");
        Element replacementList = general.getChild("ReplacementTextList");
        List<Element> repText = replacementList.getChildren("ReplacementText");
        for(int i = 0; i < repText.size(); ++i){
            System.out.println(repText.get(i).getAttribute("name").getValue());
            System.out.println(repText.get(i).getChild("From").getValue());
            System.out.println(repText.get(i).getChild("To").getValue());
        }
    }
//    
//    public static void main(String args[]) throws Exception{
//        XmlParserUtil.parse();
//    }
}
