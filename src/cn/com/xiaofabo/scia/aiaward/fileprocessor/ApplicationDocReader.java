/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.fileprocessor;

import cn.com.xiaofabo.scia.aiaward.entities.ArbitrationApplication;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author 陈光曦
 */
public class ApplicationDocReader extends DocReader {

    public static Logger logger = Logger.getLogger(ApplicationDocReader.class.getName());
    private final String IDENTIFIER_PATTERN_GIST = "^(仲裁依据)|(仲裁条款)";
    private final String IDENTIFIER_PATTERN_REQUEST = "^(仲裁请求)|(申请请求)|(申请事项).?";
    private final String IDENTIFIER_PATTERN_FACT = "^事实.理由";
    private final String IDENTIFIER_PATTERN_END = "^此\\s*致";

    public ApplicationDocReader() {
        super();
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
//            String compressedLine = removeAllSpaces(line);
            Pattern pattern = Pattern.compile(IDENTIFIER_PATTERN_GIST);
            Matcher matcher = pattern.matcher(line);
            if(matcher.find()){
                gistChunkStartIdx = lineIndex;
            }
            pattern = Pattern.compile(IDENTIFIER_PATTERN_REQUEST);
            matcher = pattern.matcher(line);
            if(matcher.find()){
                requestChunkStartIdx = lineIndex;
            }
            pattern = Pattern.compile(IDENTIFIER_PATTERN_FACT);
            matcher = pattern.matcher(line);
            if(matcher.find()){
                factAndReasonChunkStartIdx = lineIndex;
            }
            pattern = Pattern.compile(IDENTIFIER_PATTERN_END);
            matcher = pattern.matcher(line);
            if(matcher.find()){
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

//        if (aApplication.getGist() == null || aApplication.getGist().isEmpty()) {
//            logger.warn("WARN: aApplication.getGist()== null|| aApplication.getGist().isEmpty()");
//            addWarningToUser("在仲裁申请书中未找到仲裁依据");
//        }

        if (aApplication.getRequest() == null || aApplication.getRequest().isEmpty()) {
            logger.error("ERROR: aApplication.getRequest()== null|| aApplication.getRequest().isEmpty()");
            addErrorToUser("在仲裁申请书中未找到仲裁请求");
        }
        if (aApplication.getFactAndReason() == null || aApplication.getFactAndReason().isEmpty()) {
            logger.error("ERROR: aApplication.getFactAndReason() == null || aApplication.getFactAndReason().isEmpty()");
            addErrorToUser("在仲裁申请书中未找到事实与理由");
        }

        return aApplication;
    }
}
