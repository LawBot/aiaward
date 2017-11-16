/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.fileprocessor;

import static cn.com.xiaofabo.scia.aiaward.fileprocessor.ApplicationDocReader.logger;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 *
 * @author 陈光曦
 */
public abstract class DocReader implements InputFileReader{
    protected String docText;

    protected void readWordFile(String inputPath) throws IOException {
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
    }

    protected String preprocess(String str) {
        str = str.replaceAll(":", "：");
        str = str.replaceAll("％", "%");
        str = str.replaceAll("——", "──");
        return str;
    }

    protected boolean isEmptyLine(String s) {
        return (s == null || s.trim().isEmpty());
    }

    protected String removeAllSpaces(String input) {
        return input.replaceAll("\\s+", "");
    }

    protected String combineLines(String[] lines, int startIndex, int endIndex) {
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
    
    protected String processContent(String[] lines, int startLineNum, int endLineNum) {
        StringBuilder toReturn = new StringBuilder();
        for (int lineIdx = startLineNum; lineIdx <= endLineNum; ++lineIdx) {
            String line = lines[lineIdx].trim();
            if (!isEmptyLine(line)) {
                toReturn.append(line);
                toReturn.append("\n");
            }
        }
        return toReturn.toString();
    }
}
