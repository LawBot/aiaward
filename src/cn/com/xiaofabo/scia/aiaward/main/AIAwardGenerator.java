/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.main;

import cn.com.xiaofabo.scia.aiaward.entities.ArbitrationApplication;
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

        String inputPath1 = "test\\SHEN DT20170003\\仲裁资料\\申请人\\12281_AI-仲裁申请书.docx";
        String inputPath2 = "test\\SHEN DX20150363\\仲裁资料\\申请人\\12194_AI-仲裁申请书.doc";
        String inputPath3 = "test\\SHEN DX20160521\\仲裁资料\\申请人\\12382_AI-仲裁申请书.docx";
        String inputPath4 = "test\\SHEN DX20170177\\仲裁资料\\申请人\\12200_AI-仲裁申请书.doc";
        String inputPath5 = "test\\SHEN X20170211\\仲裁资料\\申请人\\12511_AI-仲裁申请书.doc";
        String inputPath6 = "test\\SHEN DF20170368-未生成\\SHEN DF20170368\\仲裁资料\\申请人\\13606_AI-仲裁申请书.docx";
        String inputPath7 = "test\\SHEN DP2014088-未生成\\SHEN DP2014088\\仲裁资料\\申请人\\13282_AI-仲裁申请书.doc";
        String outputPath1 = "test\\testoutput\\award.docx";

        String inputPath = inputPath6;
        String outputPath = outputPath1;

        ApplicationDocReader adr = new ApplicationDocReader();
        try {
            ArbitrationApplication aApplication = adr.read(inputPath);
            AwardDocGenerator awardGen = new AwardDocGenerator(outputPath);
            awardGen.generateAwardDoc(aApplication);
        } catch (IOException e) {
            logger.fatal("Cannot find application file: " + inputPath);
            logger.fatal("Please make sure the file exists!");
            logger.fatal("PROGRAM ABORTED!!!");
            return;
        }

        logger.info("AI Award Generator program finished successfully");
    }

}
