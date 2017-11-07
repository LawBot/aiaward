/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.fileprocessor;

import cn.com.xiaofabo.scia.aiaward.entities.ArbitrationApplication;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 *
 * @author cgx82
 */
public class ApplicationDocReader implements InputFileReader {

    static Logger logger = Logger.getLogger(ApplicationDocReader.class.getName());
    private String docText;

    public ApplicationDocReader() {
        PropertyConfigurator.configure("log/config.txt");
        logger.trace("Constructor of ApplicationDocReader");
    }

    public String read(String inputPath) throws IOException {
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

        String lines[] = docText.split("\\r?\\n");
        ArbitrationApplication application = new ArbitrationApplication(0);

        int titleChunkStartIdx = 0;
        List<Integer> proposerChunkStartIdx = new LinkedList();
        List<Integer> respondentChunkStartIdx = new LinkedList();
        int gistChunkStartIdx = 0;
        int requestChunkStartIdx = 0;
        int factAndReasonChunkStartIdx = 0;

        for (int lineIndex = 0; lineIndex < lines.length; ++lineIndex) {
            String line = lines[lineIndex].trim();
            String compressedLine = removeAllSpaces(line);
            if (compressedLine.contains("仲裁申请书")) {
                titleChunkStartIdx = lineIndex;
            }
            if (compressedLine.startsWith("申请人：")
                    || compressedLine.startsWith("申请人:")) {
                /// In case of signature
                if (requestChunkStartIdx == 0
                        || (requestChunkStartIdx != 0 && lineIndex < requestChunkStartIdx)) {
                    proposerChunkStartIdx.add(lineIndex);
                }
            }
            if (compressedLine.startsWith("被申请人：")
                    || compressedLine.startsWith("被申请人:")) {
                respondentChunkStartIdx.add(lineIndex);
            }
            if (compressedLine.startsWith("仲裁依据：")
                    || compressedLine.startsWith("仲裁依据:")) {
                gistChunkStartIdx = lineIndex;
            }
            if (compressedLine.startsWith("仲裁请求：")
                    || compressedLine.startsWith("仲裁请求:")) {
                requestChunkStartIdx = lineIndex;
            }
            if (compressedLine.startsWith("事实与理由：")
                    || compressedLine.startsWith("事实与理由:")) {
                factAndReasonChunkStartIdx = lineIndex;
            }
        }

        logger.debug("TitleChunkStartIdx: " + titleChunkStartIdx);
        proposerChunkStartIdx.forEach((i) -> {
            logger.debug("proposerChunkStartIdx: " + i);
        });
        respondentChunkStartIdx.forEach((i) -> {
            logger.debug("respondentChunkStartIdx: " + i);
        });
        logger.debug("gistChunkStartIdx: " + gistChunkStartIdx);
        logger.debug("requestChunkStartIdx: " + requestChunkStartIdx);
        logger.debug("factAndReasonChunkStartIdx: " + factAndReasonChunkStartIdx);

//        List<String> titleChunck = new LinkedList();
//        List<String> proposerChunk = new LinkedList();
//        List<String> RespondentChunk = new LinkedList();
//
//        int lineIndex = 0;
//        while (lineIndex < lines.length) {
//            String line = lines[lineIndex].trim();
//            if (removeAllSpaces(line).equals("仲裁申请书")) {
//                titleChunck.add(line);
//                logger.debug("TitleChunck add: " + line);
//                ++lineIndex;
//                continue;
//            }
//
//            if (removeAllSpaces(line).startsWith("申请人：")
//                    || removeAllSpaces(line).startsWith("申请人:")) {
//                proposerChunk.add(line);
//                logger.debug("ProposerChunk add: " + line);
//                while (++lineIndex < lines.length
//                        && !removeAllSpaces(lines[lineIndex]).startsWith("申请人：")
//                        && !removeAllSpaces(lines[lineIndex]).startsWith("被申请人：")) {
//                    proposerChunk.add(lines[lineIndex]);
//                    logger.debug("ProposerChunk add: " + lines[lineIndex]);
//                }
//                continue;
//            }
//
//            if (removeAllSpaces(line).startsWith("被申请人：")
//                    || removeAllSpaces(line).startsWith("被申请人:")) {
//                RespondentChunk.add(line);
//                logger.debug("RespondentChunk add: " + line);
//                while (++lineIndex < lines.length
//                        && !removeAllSpaces(lines[lineIndex]).startsWith("仲裁依据")
//                        && !removeAllSpaces(lines[lineIndex]).startsWith("仲裁请求")) {
//                    RespondentChunk.add(lines[lineIndex]);
//                    logger.debug("RespondentChunk add: " + lines[lineIndex]);
//                }
//                continue;
//            }
//            
//            
//            ++lineIndex;
//        }
//        for (int lineIndex = 0; lineIndex < lines.length; ++lineIndex) {
//            String line = lines[lineIndex].trim();
//
//            if (removeAllSpaces(line).equals("仲裁申请书")) {
//
//            }
//
//            System.out.println(line);
//            /// Process one single line
//            List<Integer> indices = new LinkedList<>();
//            int index = 0;
//            while ((index = line.indexOf("：", index)) != -1) {
//                indices.add(index++);
//            }
//
//            /// Lines with '：' character
//            if (indices.size() != 0) {
//                List<String> keys = new LinkedList<>();
//                if (indices.size() >= 2) {
//                    for (int i = 1; i < indices.size(); ++i) {
//                        int startIndex = line.lastIndexOf(" ", indices.get(i));
//                        String key = line.substring(startIndex, indices.get(i));
//                    }
//                }
//                int firstIndex = indices.get(0);
//                String key = line.substring(0, firstIndex).trim();
//            }
//            for (int j = 0; j < indices.size(); ++j) {
//                System.out.print("line " + lineIndex + ": ");
//                System.out.print(indices.get(j) + " ");
//            }
//            System.out.println("");
//        }
        return docText;
    }

    private String removeAllSpaces(String input) {
        return input.replaceAll("\\s+", "");
    }
}
