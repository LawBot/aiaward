/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.main;

import cn.com.xiaofabo.scia.aiaward.fileprocessor.ApplicationDocReader;
import cn.com.xiaofabo.scia.aiaward.fileprocessor.AwardDocGenerator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
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
    
    private static Logger logger = Logger.getLogger(AIAwardGenerator.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log/config.txt");
        logger.info("AI Award Generator program started...");
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

        String inputPath1 = "test\\SHEN DT20170003\\仲裁资料\\申请人\\12281_AI-仲裁申请书.docx";
        String inputPath2 = "test\\SHEN DX20150363\\仲裁资料\\申请人\\12194_AI-仲裁申请书.doc";
        String outputPath1 = "test\\SHEN DT20170003\\testoutput\\award.docx";
        
        String inputPath = inputPath2;
        String outputPath = outputPath1;

        ApplicationDocReader adr = new ApplicationDocReader();
        try {
            adr.read(inputPath);
        } catch (IOException e) {
            logger.fatal("Cannot find application file: " + inputPath);
            logger.fatal("Please make sure the file exists!");
            logger.fatal("PROGRAM ABORTED!!!");
            return;
        }

        AwardDocGenerator awardGen = new AwardDocGenerator(outputPath);
        awardGen.generateAwardDoc();
        
        logger.info("AI Award Generator program finished successfully");
    }

}
