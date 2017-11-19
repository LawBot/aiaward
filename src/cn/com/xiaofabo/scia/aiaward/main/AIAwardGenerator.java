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

        /// Routine
        String routineIn1 = "test\\Case1\\仲裁资料\\award\\12290_AI-裁决书-程序部分.doc";
        String routineIn2 = "test\\Case2\\仲裁资料\\award\\12195_AI-裁决书-程序部分.doc";
        String routineIn3 = "test\\Case3\\仲裁资料\\award\\12385_AI-裁决书-程序部分.docx";
        String routineIn4 = "test\\Case4\\仲裁资料\\award\\12203_AI-裁决书-程序部分.doc";
        String routineIn5 = "test\\Case5\\仲裁资料\\award\\12518_AI-裁决书-程序部分.doc";
        String routineIn6 = "test\\Case6\\SHEN DF20170368\\仲裁资料\\award\\13608_AI-裁决书-程序部分.doc";
        String routineIn7 = "test\\Case7\\SHEN DP2014088\\仲裁资料\\award\\13291_AI-裁决书-程序部分.doc";
        String routineIn8 = "test\\testinput\\routine1.doc";

        /// Application
        String appIn1 = "test\\Case1\\仲裁资料\\申请人\\12281_AI-仲裁申请书.docx";
        String appIn2 = "test\\Case2\\仲裁资料\\申请人\\12194_AI-仲裁申请书.doc";
        String appIn3 = "test\\Case3\\仲裁资料\\申请人\\12382_AI-仲裁申请书.docx";
        String appIn4 = "test\\Case4\\仲裁资料\\申请人\\12200_AI-仲裁申请书.doc";
        String appIn5 = "test\\Case5\\仲裁资料\\申请人\\12511_AI-仲裁申请书.doc";
        String appIn6 = "test\\Case6\\SHEN DF20170368\\仲裁资料\\申请人\\13606_AI-仲裁申请书.docx";
        String appIn7 = "test\\Case7\\SHEN DP2014088\\仲裁资料\\申请人\\13282_AI-仲裁申请书.doc";
        String appIn8 = "test\\testinput\\award1.docx";

        /// Evidence list
        List<String> eAppPathList1 = new LinkedList<>();
        List<String> eAppPathList2 = new LinkedList<>();
        List<String> eAppPathList3 = new LinkedList<>();
        List<String> eAppPathList4 = new LinkedList<>();
        List<String> eAppPathList5 = new LinkedList<>();
        List<String> eAppPathList6 = new LinkedList<>();
        List<String> eAppPathList7 = new LinkedList<>();
        List<String> eAppPathList8 = new LinkedList<>();

        String eAppInCase1Path1 = "test\\Case1\\仲裁资料\\申请人\\12282_AI-证据目录.doc";
        String eAppInCase1Path2 = "test\\Case1\\仲裁资料\\申请人\\12283_AI-证据目录.doc";
        String eAppInCase1Path3 = "test\\Case1\\仲裁资料\\申请人\\12284_AI-证据目录.doc";
        String eAppInCase1Path4 = "test\\Case1\\仲裁资料\\申请人\\12288_AI-证据目录.doc";
        eAppPathList1.add(eAppInCase1Path1);
        eAppPathList1.add(eAppInCase1Path2);
        eAppPathList1.add(eAppInCase1Path3);
        eAppPathList1.add(eAppInCase1Path4);

        String eAppInCase2Path1 = "test\\Case2\\仲裁资料\\申请人\\12193_AI-证据目录.docx";
        eAppPathList2.add(eAppInCase2Path1);

        String eAppInCase3Path1 = "test\\Case3\\仲裁资料\\申请人\\12382_AI-仲裁申请书.docx";
        eAppPathList3.add(eAppInCase3Path1);

        String eAppInCase4Path1 = "test\\Case4\\仲裁资料\\申请人\\12199_AI-证据目录.docx";
        eAppPathList4.add(eAppInCase4Path1);

        String eAppInCase5Path1 = "test\\Case5\\仲裁资料\\申请人\\12841_AI-证据目录.docx";
        eAppPathList5.add(eAppInCase5Path1);

        String eAppInCase6Path1 = "test\\Case6\\SHEN DF20170368\\仲裁资料\\申请人\\13607_AI-证据目录.docx";
        eAppPathList6.add(eAppInCase6Path1);

        String eAppInCase7Path1 = "test\\Case7\\SHEN DP2014088\\仲裁资料\\申请人\\13283_AI-证据目录.docx";
        eAppPathList7.add(eAppInCase7Path1);

        /// Respond
        String resIn1 = "test\\Case1\\仲裁资料\\被申请人\\12285_AI-答辩状.doc";
        String resIn2 = "test\\Case2\\仲裁资料\\被申请人\\12196_AI-答辩状.docx";
        String resIn3 = "test\\Case3\\仲裁资料\\被申请人\\12384_AI-答辩状.docx";
        String resIn4 = "test\\Case4\\仲裁资料\\被申请人\\12201_AI-答辩状.docx";
        String resIn5 = "test\\Case5\\仲裁资料\\被申请人\\12519_AI-答辩状.doc";
        String resIn6 = "test\\Case6\\SHEN DF20170368\\仲裁资料\\被申请人\\13609_AI-答辩状.docx";
        String resIn7 = "test\\Case7\\SHEN DP2014088\\仲裁资料\\被申请人\\13284_AI-答辩状.doc";
        String resIn8 = "test\\testinput\\respond1.doc";

        /// Respond evidence list
        List<String> eResPathList2 = new LinkedList<>();
        List<String> eResPathList4 = new LinkedList<>();
        List<String> eResPathList7 = new LinkedList<>();

        String eResInCase2Path1 = "test\\Case2\\仲裁资料\\被申请人\\12198_AI-证据.doc";
        eResPathList2.add(eResInCase2Path1);

        String eResInCase4Path1 = "test\\Case4\\仲裁资料\\被申请人\\12202_AI-举证.doc";
        eResPathList4.add(eResInCase4Path1);

        String eResInCase7Path1 = "test\\Case7\\SHEN DP2014088\\仲裁资料\\被申请人\\13285_AI-证据目录.doc";
        eResPathList7.add(eResInCase7Path1);

        /// Generated doc
        String outputPath1 = "test\\testoutput\\award1.docx";
        String outputPath2 = "test\\testoutput\\award2.docx";
        String outputPath3 = "test\\testoutput\\award3.docx";
        String outputPath4 = "test\\testoutput\\award4.docx";
        String outputPath5 = "test\\testoutput\\award5.docx";
        String outputPath6 = "test\\testoutput\\award6.docx";
        String outputPath7 = "test\\testoutput\\award7.docx";
        String outputPath8 = "test\\testoutput\\award8.docx";

        /// Preparation
        List<DocProcess> inOutPathList = new LinkedList<>();
        inOutPathList.add(new DocProcess(routineIn1, appIn1, eAppPathList1, resIn1, null, outputPath1));
        inOutPathList.add(new DocProcess(routineIn2, appIn2, eAppPathList2, resIn2, eResPathList2, outputPath2));
        inOutPathList.add(new DocProcess(routineIn3, appIn3, eAppPathList3, resIn3, null, outputPath3));
        inOutPathList.add(new DocProcess(routineIn4, appIn4, eAppPathList4, resIn4, eResPathList4, outputPath4));
        inOutPathList.add(new DocProcess(routineIn5, appIn5, eAppPathList5, resIn5, null, outputPath5));
        inOutPathList.add(new DocProcess(routineIn6, appIn6, eAppPathList6, resIn6, null, outputPath6));
        inOutPathList.add(new DocProcess(routineIn7, appIn7, eAppPathList7, resIn7, eResPathList7, outputPath7));
        inOutPathList.add(new DocProcess(routineIn8, appIn8, eAppPathList1, resIn8, null, outputPath8));

        try {
            for (int i = 0; i < inOutPathList.size(); ++i) {
                DocProcess dp = inOutPathList.get(i);

                String inRoutinePath = dp.getInRoutineDocUrl();
                String inAppPath = dp.getInApplicationDocUrl();
                List inAppEvidencePathList = dp.getInAppEvidenceDocUrlList();
                String inResPath = dp.getInRespondDocUrl();
                List inResEvidencePathList = dp.getInResEvidenceDocUrlList();
                String outAwardPath = dp.getOutAwardDocUrl();

                RoutineDocReader rdr = new RoutineDocReader();
                String routineContent = rdr.processRoutine(inRoutinePath);

                ApplicationDocReader adr = new ApplicationDocReader();
                ArbitrationApplication aApplication = adr.processApplication(inAppPath);
                AwardDocGenerator awardGen = new AwardDocGenerator(outAwardPath);

                EvidenceDocReader aedr = new EvidenceDocReader();
                List aeAppList = aedr.getEvidenceList(inAppEvidencePathList);

                RespondDocReader resdr = new RespondDocReader();
                String respondContent = resdr.processRespond(inResPath);
                
                EvidenceDocReader redr = new EvidenceDocReader();
                List reAppList = redr.getEvidenceList(inResEvidencePathList);

                awardGen.generateAwardDoc(routineContent, aApplication, aeAppList, respondContent, reAppList);
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
