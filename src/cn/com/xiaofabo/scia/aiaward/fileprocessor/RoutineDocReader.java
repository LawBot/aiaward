/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.fileprocessor;

import cn.com.xiaofabo.scia.aiaward.entities.Routine;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author 陈光曦
 */
public class RoutineDocReader extends DocReader {

    static Logger logger = Logger.getLogger(RoutineDocReader.class.getName());

    public RoutineDocReader() {
        PropertyConfigurator.configure("log/config.txt");
        logger.trace("Constructor of RoutineDocReader");
    }

    public Routine processRoutine(String inputPath) throws IOException {
        readWordFile(inputPath);
        preprocess(docText);

        String dateText = "";
        String routineText = "";

        String lines[] = docText.split("\\r?\\n");
        int startLineNum = 0, endLineNum = 0;
        for (int i = 0; i < lines.length; ++i) {
            String line = lines[i].trim();
            if (removeAllSpaces(line).equals("深圳")) {
                int lineIndex = i;
                while (lineIndex < lines.length) {
                    String l = lines[lineIndex++];
                    if (removeAllSpaces(l).matches("\\b.*年.*月.*日")) {
                        dateText = l;
                        logger.debug("Found award date: " + dateText);
                        break;
                    }
                }
            }
            if (line.matches("华南国仲深裁.*号")) {
                if (startLineNum != 0) {
                    logger.warn("WARN: Not the first time finding start of content. IGNORED!");
                } else {
                    startLineNum = i + 1;
                }
            }
            if (line.contains("现将本案案情、仲裁庭意见以及裁决内容分述如下。")) {
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
            logger.warn("WARN: Did not find end of the content. Last line as content end.");
        }
        logger.debug("Content start line number: " + startLineNum);
        logger.debug("Content end line number: " + endLineNum);
        routineText = processContent(lines, startLineNum, endLineNum);

        return new Routine(dateText, routineText);
    }
}
