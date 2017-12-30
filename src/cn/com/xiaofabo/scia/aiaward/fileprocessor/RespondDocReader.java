/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.fileprocessor;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author 陈光曦
 */
public class RespondDocReader extends DocReader {

    static Logger logger = Logger.getLogger(RespondDocReader.class.getName());

    public RespondDocReader() {
        super();
        PropertyConfigurator.configure("log/config.txt");
        logger.trace("Constructor of RespondDocReader");
    }

    public String processRespond(String inputPath) throws IOException {
        if (inputPath == null || inputPath.isEmpty()) {
            return null;
        }
        readWordFile(inputPath);
        preprocess(docText);

        String lines[] = docText.split("\\r?\\n");
        int startLineNum = 0, endLineNum = 0;
        for (int i = 0; i < lines.length; ++i) {
            String line = lines[i].trim();
            if (line.matches(".*答辩如下："
                    + "|" + ".*事实和理由如下："
                    + "|" + ".*如下答辩：")) {
                if (startLineNum != 0) {
                    logger.warn("WARN: Not the first time finding start of content. IGNORED!");
                } else {
                    startLineNum = i + 1;
                }
            }
            if (line.contains("此致")) {
                if (endLineNum != 0) {
                    logger.warn("WARN: Not the first time finding end of content. IGNORED!");
                } else {
                    endLineNum = i;
                }
            }
        }
        if (startLineNum == 0) {
            logger.warn("WARN: Did not find start of the content. First line as content start.");
        }
        if (endLineNum == 0) {
            endLineNum = lines.length - 1;
            logger.warn("WARN: Did not find end of the content. Last line as content end.");
        }

        if (startLineNum == 0 && endLineNum == lines.length - 1) {
            logger.error("ERROR: Respond document content is NOT correct. Please check!");
        }

        logger.debug("Content start line number: " + startLineNum);
        logger.debug("Content end line number: " + endLineNum);
        String content = combineContent(lines, startLineNum, endLineNum - 1);
        content = replacePhrases(content);
        if (content.isEmpty()) {
            logger.error("content.isEmpty()");
            addErrorToUser("在答辩书中未找到答辩人意见");
        }
        return content;
    }

    private String replacePhrases(String str) {
        str = str.replaceAll("被答辩人", "申请人");
        str = str.replaceAll("答辩人", "被申请人");
        return str;
    }
}
