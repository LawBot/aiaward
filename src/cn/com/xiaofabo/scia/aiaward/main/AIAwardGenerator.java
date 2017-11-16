/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.main;

import cn.com.xiaofabo.scia.aiaward.entities.ArbitrationApplication;
import cn.com.xiaofabo.scia.aiaward.entities.DocProcess;
import cn.com.xiaofabo.scia.aiaward.fileprocessor.ApplicationDocReader;
import cn.com.xiaofabo.scia.aiaward.fileprocessor.AwardDocGenerator;
import cn.com.xiaofabo.scia.aiaward.fileprocessor.RoutineDocReader;
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

        String appIn1 = "test\\Case1\\仲裁资料\\申请人\\12281_AI-仲裁申请书.docx";
        String appIn2 = "test\\Case2\\仲裁资料\\申请人\\12194_AI-仲裁申请书.doc";
        String appIn3 = "test\\Case3\\仲裁资料\\申请人\\12382_AI-仲裁申请书.docx";
        String appIn4 = "test\\Case4\\仲裁资料\\申请人\\12200_AI-仲裁申请书.doc";
        String appIn5 = "test\\Case5\\仲裁资料\\申请人\\12511_AI-仲裁申请书.doc";
        String appIn6 = "test\\Case6\\SHEN DF20170368\\仲裁资料\\申请人\\13606_AI-仲裁申请书.docx";
        String appIn7 = "test\\Case7\\SHEN DP2014088\\仲裁资料\\申请人\\13282_AI-仲裁申请书.doc";
        String appIn8 = "test\\testinput\\award1.docx";

        String routineIn1 = "test\\Case1\\仲裁资料\\award\\12290_AI-裁决书-程序部分.doc";
        String routineIn2 = "test\\Case2\\仲裁资料\\award\\12195_AI-裁决书-程序部分.doc";
        String routineIn3 = "test\\Case3\\仲裁资料\\award\\12385_AI-裁决书-程序部分.docx";
        String routineIn4 = "test\\Case4\\仲裁资料\\award\\12203_AI-裁决书-程序部分.doc";
        String routineIn5 = "test\\Case5\\仲裁资料\\award\\12518_AI-裁决书-程序部分.doc";
        String routineIn6 = "test\\Case6\\SHEN DF20170368\\仲裁资料\\award\\13608_AI-裁决书-程序部分.doc";
        String routineIn7 = "test\\Case7\\SHEN DP2014088\\仲裁资料\\award\\13291_AI-裁决书-程序部分.doc";
        String routineIn8 = "test\\testinput\\routine1.doc";

        String outputPath1 = "test\\testoutput\\award1.docx";
        String outputPath2 = "test\\testoutput\\award2.docx";
        String outputPath3 = "test\\testoutput\\award3.docx";
        String outputPath4 = "test\\testoutput\\award4.docx";
        String outputPath5 = "test\\testoutput\\award5.docx";
        String outputPath6 = "test\\testoutput\\award6.docx";
        String outputPath7 = "test\\testoutput\\award7.docx";
        String outputPath8 = "test\\testoutput\\award8.docx";

        List<DocProcess> inOutPathList = new LinkedList<>();
        inOutPathList.add(new DocProcess(routineIn1, appIn1, null, outputPath1));
        inOutPathList.add(new DocProcess(routineIn2, appIn2, null, outputPath2));
        inOutPathList.add(new DocProcess(routineIn3, appIn3, null, outputPath3));
        inOutPathList.add(new DocProcess(routineIn4, appIn4, null, outputPath4));
        inOutPathList.add(new DocProcess(routineIn5, appIn5, null, outputPath5));
        inOutPathList.add(new DocProcess(routineIn6, appIn6, null, outputPath6));
        inOutPathList.add(new DocProcess(routineIn7, appIn7, null, outputPath7));
        inOutPathList.add(new DocProcess(routineIn8, appIn8, null, outputPath8));

        try {
            for (int i = 0; i < inOutPathList.size(); ++i) {
                DocProcess dp = inOutPathList.get(i);
                String inAppPath = dp.getInApplicationDocUrl();
                String inRoutinePath = dp.getInRoutineDocUrl();
                String outAwardPath = dp.getOutAwardDocUrl();
                
                ApplicationDocReader adr = new ApplicationDocReader();
                ArbitrationApplication aApplication = adr.read(inAppPath);
                AwardDocGenerator awardGen = new AwardDocGenerator(outAwardPath);
                
                RoutineDocReader rdr = new RoutineDocReader();
                String routineContent = rdr.read(inRoutinePath);
                
                awardGen.generateAwardDoc(aApplication, routineContent);
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
