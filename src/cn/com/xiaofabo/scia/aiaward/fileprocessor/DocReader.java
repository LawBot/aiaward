/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.fileprocessor;

import static cn.com.xiaofabo.scia.aiaward.fileprocessor.ApplicationDocReader.logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 *
 * @author 陈光曦
 */
public abstract class DocReader implements InputFileReader {

    protected String docText;
    private List<String> errorToUser;
    private List<String> warningToUser;

    public DocReader() {
        errorToUser = new LinkedList<>();
        warningToUser = new LinkedList<>();
    }

    protected void readWordFile(String inputPath) throws IOException {
        logger.info("Reading in file: " + inputPath);
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
        this.findArticleName();
    }

    protected String getDocText() {
        return docText;
    }

    protected String preprocess(String str) {
        str = str.replaceAll(":", "：");
        str = str.replaceAll("％", "%");
        str = str.replaceAll("——", "──");
        return str;
    }
    
    protected void findArticleName(){
        Pattern pattern = Pattern.compile("《.*?》");
        Matcher matcher = pattern.matcher(docText);
        
        while(matcher.find()){
            String match = matcher.group();
        }
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

    protected String combineContent(String[] lines, int startLineNum, int endLineNum) {
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

    protected void addErrorToUser(String error) {
        errorToUser.add(error);
    }

    protected void addWarningToUser(String warning) {
        warningToUser.add(warning);
    }

    public List getErrorToUser() {
        return errorToUser;
    }

    public List getWarningToUser() {
        return warningToUser;
    }
}
