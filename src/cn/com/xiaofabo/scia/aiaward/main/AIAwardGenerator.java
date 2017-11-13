/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.main;

import cn.com.xiaofabo.scia.aiaward.entities.ArbitrationApplication;
import cn.com.xiaofabo.scia.aiaward.entities.Pair;
import cn.com.xiaofabo.scia.aiaward.fileprocessor.ApplicationDocReader;
import cn.com.xiaofabo.scia.aiaward.fileprocessor.AwardDocGenerator;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author 陈光曦
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
        String inputPath8 = "test\\testinput\\test1.docx";
        String outputPath1 = "test\\testoutput\\award1.docx";
        String outputPath2 = "test\\testoutput\\award2.docx";
        String outputPath3 = "test\\testoutput\\award3.docx";
        String outputPath4 = "test\\testoutput\\award4.docx";
        String outputPath5 = "test\\testoutput\\award5.docx";
        String outputPath6 = "test\\testoutput\\award6.docx";
        String outputPath7 = "test\\testoutput\\award7.docx";
        String outputPath8 = "test\\testoutput\\award8.docx";

        List<Pair> inOutPathList = new LinkedList<>();
        inOutPathList.add(new Pair(inputPath1, outputPath1));
        inOutPathList.add(new Pair(inputPath2, outputPath2));
        inOutPathList.add(new Pair(inputPath3, outputPath3));
        inOutPathList.add(new Pair(inputPath4, outputPath4));
        inOutPathList.add(new Pair(inputPath5, outputPath5));
        inOutPathList.add(new Pair(inputPath6, outputPath6));
        inOutPathList.add(new Pair(inputPath7, outputPath7));
        inOutPathList.add(new Pair(inputPath8, outputPath8));

        try {
            for (int i = 0; i < inOutPathList.size(); ++i) {
                String inputPath = inOutPathList.get(i).getKey();
                String outputPath = inOutPathList.get(i).getValue();
                ApplicationDocReader adr = new ApplicationDocReader();
                ArbitrationApplication aApplication = adr.read(inputPath);
                AwardDocGenerator awardGen = new AwardDocGenerator(outputPath);
                awardGen.generateAwardDoc(aApplication);
            }
        } catch (IOException e) {
            logger.fatal("Cannot find application file.");
            logger.fatal("Please make sure the file exists!");
            logger.fatal("PROGRAM ABORTED!!!");
            return;
        }

        logger.info("AI Award Generator program finished successfully");
    }

}
