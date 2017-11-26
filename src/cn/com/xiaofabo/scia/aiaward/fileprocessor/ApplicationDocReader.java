/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.fileprocessor;

import cn.com.xiaofabo.scia.aiaward.entities.ArbitrationApplication;
import cn.com.xiaofabo.scia.aiaward.entities.Pair;
import cn.com.xiaofabo.scia.aiaward.entities.ArbitrationProposer;
import cn.com.xiaofabo.scia.aiaward.entities.ArbitrationRespondent;
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
    

    public ApplicationDocReader() {
        PropertyConfigurator.configure("log/config.txt");
        logger.trace("Constructor of ApplicationDocReader");
    }

    public ArbitrationApplication processApplication(String inAppPath) throws IOException {
        readWordFile(inAppPath);
        docText = preprocess(docText);

        String lines[] = docText.split("\\r?\\n");

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
        aApplication.setGist(gistChunk);
        aApplication.setRequest(requestChunk);
        aApplication.setFactAndReason(factAndReasonChunk);
        return aApplication;
    }
}
