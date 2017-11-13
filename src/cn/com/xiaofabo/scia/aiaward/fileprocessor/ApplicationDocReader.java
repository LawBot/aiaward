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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 *
 * @author 陈光曦
 */
public class ApplicationDocReader implements InputFileReader {

    static Logger logger = Logger.getLogger(ApplicationDocReader.class.getName());
    private String docText;

    public ApplicationDocReader() {
        PropertyConfigurator.configure("log/config.txt");
        logger.trace("Constructor of ApplicationDocReader");
    }

    public ArbitrationApplication read(String inputPath) throws IOException {
        logger.info("Start reading in file: " + inputPath);
        // Newer version word documents
        try {
            XWPFDocument docx = new XWPFDocument(new FileInputStream(inputPath));
            XWPFWordExtractor we = new XWPFWordExtractor(docx);
            docText = we.getText();
        } /// Old version word documents
        catch (org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException e) {
            HWPFDocument doc = new HWPFDocument(new FileInputStream(inputPath));
            WordExtractor we = new WordExtractor(doc);
            docText = we.getText();
        }

        docText = preprocess(docText);
        String lines[] = docText.split("\\r?\\n");
        ArbitrationApplication application = new ArbitrationApplication(0);

        int titleChunkStartIdx = 0;
        List<Integer> proposerChunkStartIdx = new LinkedList();
        List<Integer> respondentChunkStartIdx = new LinkedList();
        int gistChunkStartIdx = 0;
        int requestChunkStartIdx = 0;
        int factAndReasonChunkStartIdx = 0;
        int factAndReasonChunkEndIdx = 0;

        for (int lineIndex = 0; lineIndex < lines.length; ++lineIndex) {
            String line = lines[lineIndex].trim();
            String compressedLine = removeAllSpaces(line);
            if (compressedLine.contains("仲裁申请书")) {
                titleChunkStartIdx = lineIndex;
            }
            if (compressedLine.startsWith("申请人：")) {
                /// In case of signature
                if (requestChunkStartIdx == 0
                        || (requestChunkStartIdx != 0 && lineIndex < requestChunkStartIdx)) {
                    proposerChunkStartIdx.add(lineIndex);
                }
            }
            if (compressedLine.startsWith("被申请人：")) {
                respondentChunkStartIdx.add(lineIndex);
            }
            if (compressedLine.startsWith("仲裁依据：")
                    ||compressedLine.startsWith("仲裁依据")
                    ||compressedLine.startsWith("仲裁条款：")
                    ||compressedLine.startsWith("仲裁条款")) {
                gistChunkStartIdx = lineIndex;
            }
            if (compressedLine.startsWith("仲裁请求：")
                    ||compressedLine.startsWith("仲裁请求")
                    ||compressedLine.startsWith("申请请求：")
                    ||compressedLine.startsWith("申请请求")) {
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

        logger.trace("TitleChunkStartIdx: " + titleChunkStartIdx);
        proposerChunkStartIdx.forEach((i) -> {
            logger.trace("proposerChunkStartIdx: " + i);
        });
        respondentChunkStartIdx.forEach((i) -> {
            logger.trace("respondentChunkStartIdx: " + i);
        });
        logger.trace("gistChunkStartIdx: " + gistChunkStartIdx);
        logger.trace("requestChunkStartIdx: " + requestChunkStartIdx);
        logger.trace("factAndReasonChunkStartIdx: " + factAndReasonChunkStartIdx);

        String titleChunk = "";
        List<String> proposerChunk = new LinkedList<>();
        List<String> respondentChunk = new LinkedList<>();
        String gistChunk = "";
        String requestChunk = "";
        String factAndReasonChunk = "";

        titleChunk = combineLines(lines, titleChunkStartIdx, proposerChunkStartIdx.get(0));
        /// TODO: Only considered one proposer case. should consider more proposers case
        if (proposerChunkStartIdx.size() == 1) {
            int tmpStartIdx = proposerChunkStartIdx.get(0);
            int tmpEndIdx = respondentChunkStartIdx.get(0);
            proposerChunk.add(combineLines(lines, tmpStartIdx, tmpEndIdx));
        }
        /// TODO: Only considered one respondent case. should consider more respondents case
        if (respondentChunkStartIdx.size() == 1) {
            int tmpStartIdx = respondentChunkStartIdx.get(0);
            int tmpEndIdx = gistChunkStartIdx == 0 ? requestChunkStartIdx : gistChunkStartIdx;
            respondentChunk.add(combineLines(lines, tmpStartIdx, tmpEndIdx));
        }
        if (gistChunkStartIdx != 0) {
            int tmpStartIdx = gistChunkStartIdx;
            int tmpEndIdx = requestChunkStartIdx;
            /// Start index+1 to remove title
            gistChunk = combineLines(lines, tmpStartIdx+1, tmpEndIdx);
        }
        if (requestChunkStartIdx != 0) {
            int tmpStartIdx = requestChunkStartIdx;
            int tmpEndIdx = factAndReasonChunkStartIdx;
            requestChunk = combineLines(lines, tmpStartIdx+1, tmpEndIdx);
        }
        if (factAndReasonChunkStartIdx != 0) {
            int tmpStartIdx = factAndReasonChunkStartIdx;
            int tmpEndIdx = factAndReasonChunkEndIdx == 0 ? lines.length : factAndReasonChunkEndIdx;
            factAndReasonChunk = combineLines(lines, tmpStartIdx+1, tmpEndIdx);
        }

        logger.debug("标题:\n" + titleChunk);
        for (int i = 0; i < proposerChunk.size(); ++i) {
            logger.debug("申请人:\n" + proposerChunk.get(i));
        }
        for (int i = 0; i < respondentChunk.size(); ++i) {
            logger.debug("被申请人:\n" + respondentChunk.get(i));
        }
        logger.debug("仲裁依据:\n" + gistChunk);
        logger.debug("仲裁请求:\n" + requestChunk);
        logger.debug("事实与理由:\n" + factAndReasonChunk);

        String pChunk = proposerChunk.get(0);
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

        String rChunk = respondentChunk.get(0);
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
        address = "";
        representative = "";
        agency = "";
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

        ArbitrationApplication aa = new ArbitrationApplication(0);
        List<Proposer> pList = new LinkedList<>();
        List<Respondent> rList = new LinkedList<>();
        pList.add(pro);
        rList.add(res);
        aa.setProposerList(pList);
        aa.setRespondentList(rList);
        aa.setGist(gistChunk);
        aa.setRequest(requestChunk);
        aa.setFactAndReason(factAndReasonChunk);
        return aa;
    }

    private String removeAllSpaces(String input) {
        return input.replaceAll("\\s+", "");
    }

    private String combineLines(String[] lines, int startIndex, int endIndex) {
        if (startIndex >= lines.length || endIndex >= lines.length) {
            return null;
        }
        StringBuilder toReturn = new StringBuilder();
        for (int i = startIndex; i < endIndex; ++i) {
            if (!removeAllSpaces(lines[i]).isEmpty()) {
                toReturn.append(lines[i]).append("\n");
            }
        }
        return toReturn.toString();
    }

    private String preprocess(String str) {
        str = str.replaceAll(":", "：");
        str = str.replaceAll("％", "%");
        str = str.replaceAll("——", "──");
        return str;
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
