/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aiawardgenerator;

import cn.com.xiaofabo.scia.aiaward.fileprocessor.ApplicationDocReader;
import cn.com.xiaofabo.scia.aiaward.fileprocessor.AwardDocGenerator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 *
 * @author cgx82
 */
public class AIAwardGenerator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        //Blank Document
//        XWPFDocument document = new XWPFDocument();
//
//        //Write the Document in file system
//        FileOutputStream out = new FileOutputStream(new File("fontstyle.docx"));
//
//        //create paragraph
//        XWPFParagraph paragraph = document.createParagraph();
//
//        //Set Bold an Italic
//        XWPFRun paragraphOneRunOne = paragraph.createRun();
//        paragraphOneRunOne.setBold(true);
//        paragraphOneRunOne.setItalic(true);
//        paragraphOneRunOne.setText("Font Style");
//        paragraphOneRunOne.addBreak();
//
//        //Set text Position
//        XWPFRun paragraphOneRunTwo = paragraph.createRun();
//        paragraphOneRunTwo.setText("Font Style two");
//        paragraphOneRunTwo.setTextPosition(100);
//
//        //Set Strike through and Font Size and Subscript
//        XWPFRun paragraphOneRunThree = paragraph.createRun();
//        paragraphOneRunThree.setStrike(true);
//        paragraphOneRunThree.setFontSize(20);
//        paragraphOneRunThree.setSubscript(VerticalAlign.SUBSCRIPT);
//        paragraphOneRunThree.setText(" Different Font Styles");
//
//        document.write(out);
//        out.close();
//        System.out.println("fontstyle.docx written successully");

        String inputPath = "D:\\OneDrive\\小法博科技\\产品\\AI裁决书\\SHEN DP2014088-未生成\\SHEN DP2014088\\仲裁资料\\申请人\\13282_AI-仲裁申请书.doc";
        String outputPath = "test\\SHEN DT20170003\\testoutput\\award.docx";

        //XWPFDocument docx = null;
        try {
            XWPFDocument docx = new XWPFDocument(new FileInputStream(inputPath));
            //using XWPFWordExtractor Class
            XWPFWordExtractor we = new XWPFWordExtractor(docx);
            System.out.println(we.getText());
        } catch (org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException e) {
            HWPFDocument doc = new HWPFDocument(new FileInputStream(inputPath));
            WordExtractor we = new WordExtractor(doc);
            System.out.println(we.getText());
            //System.out.println("Input file has a format which is too old!");
        }

        AwardDocGenerator awardGen = new AwardDocGenerator(outputPath);
        awardGen.generateAwardDoc();
    }

}
