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
import cn.com.xiaofabo.scia.aiaward.fileprocessor.EvidenceDocReader;
import cn.com.xiaofabo.scia.aiaward.fileprocessor.RespondDocReader;
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
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log/config.txt");
        logger.info("AI Award Generator program started...");

        String routineIn1 = "test\\Case1\\仲裁资料\\award\\12290_AI-裁决书-程序部分.doc";
        String routineIn2 = "test\\Case2\\仲裁资料\\award\\12195_AI-裁决书-程序部分.doc";
        String routineIn3 = "test\\Case3\\仲裁资料\\award\\12385_AI-裁决书-程序部分.docx";
        String routineIn4 = "test\\Case4\\仲裁资料\\award\\12203_AI-裁决书-程序部分.doc";
        String routineIn5 = "test\\Case5\\仲裁资料\\award\\12518_AI-裁决书-程序部分.doc";
        String routineIn6 = "test\\Case6\\SHEN DF20170368\\仲裁资料\\award\\13608_AI-裁决书-程序部分.doc";
        String routineIn7 = "test\\Case7\\SHEN DP2014088\\仲裁资料\\award\\13291_AI-裁决书-程序部分.doc";
        String routineIn8 = "test\\testinput\\routine1.doc";

        String appIn1 = "test\\Case1\\仲裁资料\\申请人\\12281_AI-仲裁申请书.docx";
        String appIn2 = "test\\Case2\\仲裁资料\\申请人\\12194_AI-仲裁申请书.doc";
        String appIn3 = "test\\Case3\\仲裁资料\\申请人\\12382_AI-仲裁申请书.docx";
        String appIn4 = "test\\Case4\\仲裁资料\\申请人\\12200_AI-仲裁申请书.doc";
        String appIn5 = "test\\Case5\\仲裁资料\\申请人\\12511_AI-仲裁申请书.doc";
        String appIn6 = "test\\Case6\\SHEN DF20170368\\仲裁资料\\申请人\\13606_AI-仲裁申请书.docx";
        String appIn7 = "test\\Case7\\SHEN DP2014088\\仲裁资料\\申请人\\13282_AI-仲裁申请书.doc";
        String appIn8 = "test\\testinput\\award1.docx";

        String resIn1 = "test\\Case1\\仲裁资料\\被申请人\\12285_AI-答辩状.doc";
        String resIn2 = "test\\Case2\\仲裁资料\\被申请人\\12196_AI-答辩状.docx";
        String resIn3 = "test\\Case3\\仲裁资料\\被申请人\\12384_AI-答辩状.docx";
        String resIn4 = "test\\Case4\\仲裁资料\\被申请人\\12201_AI-答辩状.docx";
        String resIn5 = "test\\Case5\\仲裁资料\\被申请人\\12519_AI-答辩状.doc";
        String resIn6 = "test\\Case6\\SHEN DF20170368\\仲裁资料\\被申请人\\13609_AI-答辩状.docx";
        String resIn7 = "test\\Case7\\SHEN DP2014088\\仲裁资料\\被申请人\\13284_AI-答辩状.doc";
        String resIn8 = "test\\testinput\\respond1.doc";

        String outputPath1 = "test\\testoutput\\award1.docx";
        String outputPath2 = "test\\testoutput\\award2.docx";
        String outputPath3 = "test\\testoutput\\award3.docx";
        String outputPath4 = "test\\testoutput\\award4.docx";
        String outputPath5 = "test\\testoutput\\award5.docx";
        String outputPath6 = "test\\testoutput\\award6.docx";
        String outputPath7 = "test\\testoutput\\award7.docx";
        String outputPath8 = "test\\testoutput\\award8.docx";

        String eAppInPath1 = "test\\Case1\\仲裁资料\\申请人\\12282_AI-证据目录.doc";
        String eAppInPath2 = "test\\Case1\\仲裁资料\\申请人\\12283_AI-证据目录.doc";
        String eAppInPath3 = "test\\Case1\\仲裁资料\\申请人\\12284_AI-证据目录.doc";
        String eAppInPath4 = "test\\Case1\\仲裁资料\\申请人\\12288_AI-证据目录.doc";
        List<String> eAppPathList1 = new LinkedList<>();
        eAppPathList1.add(eAppInPath1);
        eAppPathList1.add(eAppInPath2);
        eAppPathList1.add(eAppInPath3);
        eAppPathList1.add(eAppInPath4);

        List<DocProcess> inOutPathList = new LinkedList<>();
        inOutPathList.add(new DocProcess(routineIn1, appIn1, eAppPathList1, resIn1, outputPath1));
        inOutPathList.add(new DocProcess(routineIn2, appIn2, eAppPathList1, resIn2, outputPath2));
        inOutPathList.add(new DocProcess(routineIn3, appIn3, eAppPathList1, resIn3, outputPath3));
        inOutPathList.add(new DocProcess(routineIn4, appIn4, eAppPathList1, resIn4, outputPath4));
        inOutPathList.add(new DocProcess(routineIn5, appIn5, eAppPathList1, resIn5, outputPath5));
        inOutPathList.add(new DocProcess(routineIn6, appIn6, eAppPathList1, resIn6, outputPath6));
        inOutPathList.add(new DocProcess(routineIn7, appIn7, eAppPathList1, resIn7, outputPath7));
        inOutPathList.add(new DocProcess(routineIn8, appIn8, eAppPathList1, resIn8, outputPath8));

        try {
            for (int i = 0; i < inOutPathList.size(); ++i) {
                DocProcess dp = inOutPathList.get(i);

                String inRoutinePath = dp.getInRoutineDocUrl();
                String inAppPath = dp.getInApplicationDocUrl();
                List inEvidencePathList = dp.getInEvidenceDocUrlList();
                String inResPath = dp.getInRespondDocUrl();
                String outAwardPath = dp.getOutAwardDocUrl();

                RoutineDocReader rdr = new RoutineDocReader();
                String routineContent = rdr.processRoutine(inRoutinePath);
                
                ApplicationDocReader adr = new ApplicationDocReader();
                ArbitrationApplication aApplication = adr.processApplication(inAppPath);
                AwardDocGenerator awardGen = new AwardDocGenerator(outAwardPath);
                
                EvidenceDocReader edr = new EvidenceDocReader();
                List eAppList = edr.getEvidenceList(eAppPathList1);

                RespondDocReader resdr = new RespondDocReader();
                String respondContent = resdr.processRespond(inResPath);

                awardGen.generateAwardDoc(routineContent, aApplication, eAppList, respondContent);
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
