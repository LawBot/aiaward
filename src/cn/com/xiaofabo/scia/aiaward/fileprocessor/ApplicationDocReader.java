/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.fileprocessor;

import cn.com.xiaofabo.scia.aiaward.entities.ArbitrationApplication;
import cn.com.xiaofabo.scia.aiaward.entities.Pair;
import cn.com.xiaofabo.scia.aiaward.entities.Proposer;
import cn.com.xiaofabo.scia.aiaward.entities.Respondent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author 陈光曦
 */
public class ApplicationDocReader extends DocReader {

    public static Logger logger = Logger.getLogger(ApplicationDocReader.class.getName());
    private final List<Proposer> proposerList;
    private final List<Respondent> respondentList;

    public ApplicationDocReader() {
        PropertyConfigurator.configure("log/config.txt");
        logger.trace("Constructor of ApplicationDocReader");
        proposerList = new LinkedList<>();
        respondentList = new LinkedList<>();
    }

    public ArbitrationApplication processApplication(String inAppPath, String inRoutinePath) throws IOException {
        readProAndRes(inRoutinePath);

        readWordFile(inAppPath);
        docText = preprocess(docText);

        String lines[] = docText.split("\\r?\\n");
        ArbitrationApplication application = new ArbitrationApplication(0);

        int gistChunkStartIdx = 0;
        int requestChunkStartIdx = 0;
        int factAndReasonChunkStartIdx = 0;
        int factAndReasonChunkEndIdx = 0;

        for (int lineIndex = 0; lineIndex < lines.length; ++lineIndex) {
            String line = lines[lineIndex].trim();
            String compressedLine = removeAllSpaces(line);
            if (compressedLine.startsWith("仲裁依据：")
                    || compressedLine.startsWith("仲裁依据")
                    || compressedLine.startsWith("仲裁条款：")
                    || compressedLine.startsWith("仲裁条款")) {
                gistChunkStartIdx = lineIndex;
            }
            if (compressedLine.startsWith("仲裁请求：")
                    || compressedLine.startsWith("仲裁请求")
                    || compressedLine.startsWith("申请请求：")
                    || compressedLine.startsWith("申请请求")) {
                requestChunkStartIdx = lineIndex;
            }
            if (compressedLine.startsWith("事实与理由：")
                    || compressedLine.startsWith("事实与理由")
                    || compressedLine.startsWith("事实理由：")
                    || compressedLine.startsWith("事实理由")) {
                factAndReasonChunkStartIdx = lineIndex;
            }
            if (compressedLine.startsWith("此致")) {
//                String nextLine = lines[lineIndex + 1].trim();
//                if (removeAllSpaces(nextLine).startsWith("深圳国际仲裁院")
//                        ||removeAllSpaces(nextLine).startsWith("华南国际经济贸易仲裁委员会")) {
//                    factAndReasonChunkEndIdx = lineIndex;
//                }
                factAndReasonChunkEndIdx = lineIndex;
            }
        }

        logger.trace("gistChunkStartIdx: " + gistChunkStartIdx);
        logger.trace("requestChunkStartIdx: " + requestChunkStartIdx);
        logger.trace("factAndReasonChunkStartIdx: " + factAndReasonChunkStartIdx);

        String gistChunk = "";
        String requestChunk = "";
        String factAndReasonChunk = "";

        if (gistChunkStartIdx != 0) {
            int tmpStartIdx = gistChunkStartIdx;
            int tmpEndIdx = requestChunkStartIdx;
            /// Start index+1 to remove title
            gistChunk = combineLines(lines, tmpStartIdx + 1, tmpEndIdx);
        }
        if (requestChunkStartIdx != 0) {
            int tmpStartIdx = requestChunkStartIdx;
            int tmpEndIdx = factAndReasonChunkStartIdx;
            requestChunk = combineLines(lines, tmpStartIdx + 1, tmpEndIdx);
        }
        if (factAndReasonChunkStartIdx != 0) {
            int tmpStartIdx = factAndReasonChunkStartIdx;
            int tmpEndIdx = factAndReasonChunkEndIdx == 0 ? lines.length : factAndReasonChunkEndIdx;
            factAndReasonChunk = combineLines(lines, tmpStartIdx + 1, tmpEndIdx);
        }
        logger.debug("仲裁依据:\n" + gistChunk);
        logger.debug("仲裁请求:\n" + requestChunk);
        logger.debug("事实与理由:\n" + factAndReasonChunk);

        ArbitrationApplication aApplication = new ArbitrationApplication(0);
        aApplication.setProposerList(proposerList);
        aApplication.setRespondentList(respondentList);
        aApplication.setGist(gistChunk);
        aApplication.setRequest(requestChunk);
        aApplication.setFactAndReason(factAndReasonChunk);
        return aApplication;
    }

    private void readProAndRes(String routineDocPath) throws IOException {
        readWordFile(routineDocPath);
        docText = preprocess(docText);

        List<Integer> proposerChunkStartIdx = new LinkedList();
        List<Integer> respondentChunkStartIdx = new LinkedList();

        String lines[] = docText.split("\\r?\\n");

        int lastIdx = 0;
        for (int lineIndex = 0; lineIndex < lines.length; ++lineIndex) {
            String line = lines[lineIndex].trim();
            String compressedLine = removeAllSpaces(line);
            if (compressedLine.startsWith("申请人：")) {
                proposerChunkStartIdx.add(lineIndex);
            }
            if (compressedLine.startsWith("被申请人：")) {
                respondentChunkStartIdx.add(lineIndex);
            }
            if (compressedLine.equals("深圳")) {
                lastIdx = lineIndex;
                break;
            }
        }

        if (proposerChunkStartIdx.isEmpty()) {
            logger.error("ERROR: Did not find any proposer in routine document!!!");
        }
        if (respondentChunkStartIdx.isEmpty()) {
            logger.error("ERROR: Did not find any respondent in routine document!!!");
        }
        if (lastIdx == 0) {
            logger.error("ERROR: First page in routine document is not correctly formatted!!!");
        }

        List<String> proposerChunk = new LinkedList<>();
        List<String> respondentChunk = new LinkedList<>();

        for (int i = 0; i < proposerChunkStartIdx.size(); ++i) {
            int startIdx = proposerChunkStartIdx.get(i);
            int endIdx = 0;
            if (i != proposerChunkStartIdx.size() - 1) {
                endIdx = proposerChunkStartIdx.get(i + 1);
            } else {
                endIdx = respondentChunkStartIdx.get(0) - 1;
            }
            proposerChunk.add(combineLines(lines, startIdx, endIdx));
        }

        for (int i = 0; i < respondentChunkStartIdx.size(); ++i) {
            int startIdx = respondentChunkStartIdx.get(i);
            int endIdx = 0;
            if (i != respondentChunkStartIdx.size() - 1) {
                endIdx = respondentChunkStartIdx.get(i + 1);
            } else {
                endIdx = lastIdx - 1;
            }
            respondentChunk.add(combineLines(lines, startIdx, endIdx));
        }

        for (int i = 0; i < proposerChunk.size(); ++i) {
            String pChunk = proposerChunk.get(i);
            proposerList.add(createProposer(pChunk));
        }

        for (int i = 0; i < respondentChunk.size(); ++i) {
            String rChunk = respondentChunk.get(i);
            respondentList.add(createRespondent(rChunk));
        }
    }

    private Proposer createProposer(String pChunk) {
        pChunk = pAndrProcess(pChunk);
        String plines[] = pChunk.split("\\r?\\n");
        List<Pair> proposerPairList = new LinkedList<>();
        for (int pLineIndex = 0; pLineIndex < plines.length; ++pLineIndex) {
            String pline = plines[pLineIndex].trim();
            List<Integer> indices = new LinkedList<>();
            int index = 0;
            while ((index = pline.indexOf("：", index)) != -1) {
                indices.add(index++);
            }

            int keyStartIdx = 0;
            int keyEndIdx = 0;
            int valueStartIdx = 0;
            int valueEndIdx = 0;
            for (int i = 0; i < indices.size(); ++i) {
                keyEndIdx = indices.get(i);
                String key = pline.substring(keyStartIdx, keyEndIdx);
                valueStartIdx = keyEndIdx + 1;
                if ((i + 1) != indices.size()) {
                    int tmpIdx = indices.get(i + 1);
                    valueEndIdx = pline.lastIndexOf(" ", tmpIdx);
                } else {
                    valueEndIdx = -1;
                }
                String value = valueEndIdx == -1 ? pline.substring(valueStartIdx) : pline.substring(valueStartIdx, valueEndIdx);
                keyStartIdx = valueEndIdx + 1;
                proposerPairList.add(new Pair(removeAllSpaces(key), removeAllSpaces(value)));
            }
        }

        Proposer pro = new Proposer();
        String proposer = "";
        String address = "";
        String representative = "";
        String agency = "";
        for (int i = 0; i < proposerPairList.size(); ++i) {
            String key = proposerPairList.get(i).getKey();
            String value = proposerPairList.get(i).getValue();

            if (key.equals("申请人")) {
                proposer = value;
            }
            if (key.equals("住址") || key.equals("地址") || key.equals("住所")) {
                address = value;
            }
            if (key.equals("法定代表人")) {
                representative = value;
            }
            if (key.equals("代理人")) {
                agency = value;
            }
            pro.setProposer(proposer);
            pro.setAddress(address);
            pro.setRepresentative(representative);
            pro.setAgency(agency);
        }
        logger.debug("申请人：" + pro.getProposer());
        logger.debug("地址：" + pro.getAddress());
        logger.debug("法定代表人：" + pro.getRepresentative());
        logger.debug("代理人：" + pro.getAgency());
        return pro;
    }

    private Respondent createRespondent(String rChunk) {
        rChunk = pAndrProcess(rChunk);
        String rlines[] = rChunk.split("\\r?\\n");
        List<Pair> respondentPairList = new LinkedList<>();
        for (int rLineIndex = 0; rLineIndex < rlines.length; ++rLineIndex) {
            String rline = rlines[rLineIndex].trim();
            List<Integer> indices = new LinkedList<>();
            int index = 0;
            while ((index = rline.indexOf("：", index)) != -1) {
                indices.add(index++);
            }

            int keyStartIdx = 0;
            int keyEndIdx = 0;
            int valueStartIdx = 0;
            int valueEndIdx = 0;
            for (int i = 0; i < indices.size(); ++i) {
                keyEndIdx = indices.get(i);
                String key = rline.substring(keyStartIdx, keyEndIdx);
                valueStartIdx = keyEndIdx + 1;
                if ((i + 1) != indices.size()) {
                    int tmpIdx = indices.get(i + 1);
                    valueEndIdx = rline.lastIndexOf(" ", tmpIdx);
                } else {
                    valueEndIdx = -1;
                }
                String value = valueEndIdx == -1 ? rline.substring(valueStartIdx) : rline.substring(valueStartIdx, valueEndIdx);
                keyStartIdx = valueEndIdx + 1;
                respondentPairList.add(new Pair(removeAllSpaces(key), removeAllSpaces(value)));
            }
        }

        Respondent res = new Respondent();
        String respondent = "";
        String address = "";
        String representative = "";
        String agency = "";
        for (int i = 0; i < respondentPairList.size(); ++i) {
            String key = respondentPairList.get(i).getKey();
            String value = respondentPairList.get(i).getValue();

            if (key.equals("被申请人")) {
                respondent = value;
            }
            if (key.equals("住址") || key.equals("地址") || key.equals("住所")) {
                address = value;
            }
            if (key.equals("法定代表人")) {
                representative = value;
            }
            if (key.equals("代理人")) {
                agency = value;
            }
            res.setRespondent(respondent);
            res.setAddress(address);
            res.setRepresentative(representative);
            res.setAgency(agency);
        }

        logger.debug("被申请人：" + res.getRespondent());
        logger.debug("地址：" + res.getAddress());
        logger.debug("法定代表人：" + res.getRepresentative());
        logger.debug("代理人：" + res.getAgency());

        return res;
    }

    private String pAndrProcess(String proposerStr) {
        proposerStr = proposerStr.replaceAll("，", "   ");
        proposerStr = proposerStr.replaceAll(",", "   ");
        proposerStr = proposerStr.replaceAll("。", "   ");
        if (proposerStr.contains("性别") && !proposerStr.contains("性别：")) {
            proposerStr = proposerStr.replaceAll("性别", "性别：");
        }
        if (proposerStr.contains("身份证号码") && !proposerStr.contains("身份证号码：")) {
            proposerStr = proposerStr.replaceAll("身份证号码", "身份证号码：");
        }
        if (proposerStr.contains("身份号码") && !proposerStr.contains("身份号码：")) {
            proposerStr = proposerStr.replaceAll("身份号码", "身份号码：");
        }
        if (proposerStr.contains("住址") && !proposerStr.contains("住址：")) {
            proposerStr = proposerStr.replaceAll("住址", "住址：");
        }
        return proposerStr;
    }
}
