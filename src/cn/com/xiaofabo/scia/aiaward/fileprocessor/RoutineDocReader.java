/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.fileprocessor;

import java.io.FileInputStream;
import java.io.IOException;
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
public class RoutineDocReader implements InputFileReader {

    static Logger logger = Logger.getLogger(RoutineDocReader.class.getName());
    private String docText;

    public RoutineDocReader() {
        PropertyConfigurator.configure("log/config.txt");
        logger.trace("Constructor of RoutineDocReader");
    }

    public String read(String inputPath) throws IOException {
        logger.info("Start reading file: " + inputPath);
        /// Newer version word documents
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

        return getContentTxt();
    }

    public String getContentTxt() {
        String content = "";
        String lines[] = docText.split("\\r?\\n");
        int startLineNum = 0, endLineNum = 0;
        for (int i = 0; i < lines.length; ++i) {
            String line = lines[i].trim();
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
        return processContent(lines, startLineNum, endLineNum);
    }

    private String processContent(String[] lines, int startLineNum, int endLineNum) {
        StringBuilder toReturn = new StringBuilder();
        for (int lineIdx = startLineNum; lineIdx <= endLineNum; ++lineIdx) {
            String line = lines[lineIdx].trim();
            if(!isEmptyLine(line)){
                toReturn.append(line);
                toReturn.append("\n");
            }
        }
        return toReturn.toString();
    }
    
    private boolean isEmptyLine(String s){
        return (s==null||s.trim().isEmpty());
    }

    private String preprocess(String str) {
        str = str.replaceAll(":", "：");
        str = str.replaceAll("％", "%");
        str = str.replaceAll("——", "──");
        return str;
    }
}
