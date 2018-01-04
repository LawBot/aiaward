/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.fileprocessor;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/**
 *
 * @author 陈光曦
 */
public class EvidenceDocReader extends DocReader {

    private final List evidenceList;
    Logger logger = Logger.getLogger(EvidenceDocReader.class.getName());

    public EvidenceDocReader() {
        super();
        evidenceList = new LinkedList();
        PropertyConfigurator.configure("log/config.txt");
    }

    public List getEvidenceList(List inputPathList) throws IOException {
        if (inputPathList == null) {
            return null;
        }

        for (int i = 0; i < inputPathList.size(); ++i) {
            String inputPath = (String) inputPathList.get(i);
//            logger.info(inputPath + ":");
            readWordFile(inputPath);
            List eList = getEvidences(inputPath);
            if (eList != null && !eList.isEmpty()) {
                evidenceList.add(eList);
            }
        }

        return evidenceList;
    }

    private List getEvidences(String inputPath) {

        List<String> eList = new LinkedList<>();
        int eChunk = -1;

        if (docx != null) {
            List tableList = docx.getTables();
            logger.debug("Found " + tableList.size() + " table(s) in document");
            for (int nTables = 0; nTables < tableList.size(); ++nTables) {
                XWPFTable table = (XWPFTable) tableList.get(nTables);
                List rowList = table.getRows();
                XWPFTableRow firstRow = (XWPFTableRow) rowList.get(0);
                List firstRowCellList = firstRow.getTableCells();
                for (int i = 0; i < firstRowCellList.size(); ++i) {
                    XWPFTableCell cell = (XWPFTableCell) firstRowCellList.get(i);
                    String tmpText = cell.getText().trim();
                    if (tmpText.startsWith("证据名称") || tmpText.startsWith("证据材料")) {
                        eChunk = i;
                        break;
                    }
                }
                /// Process from 2nd table row
                for (int i = 1; i < rowList.size(); ++i) {
                    XWPFTableRow row = (XWPFTableRow) rowList.get(i);
                    List cellList = row.getTableCells();

                    if (cellList != null) {
                        if (cellList.size() <= eChunk) {
                            continue;
                        }
                        XWPFTableCell cell = (XWPFTableCell) cellList.get(eChunk);
                        String evidenceText = cell.getText();
                        if (!isEmptyLine(evidenceText)) {
                            eList.add(evidenceText);
                        }
                    }
                }
            }
        } else {
            String lines[] = docText.split("\\r?\\n");
            int tableStart = 0;
            int tableEnd = 0;
            for (int i = 0; i < lines.length; ++i) {
                String line = lines[i];
                String chunks[] = line.split("\\t");

                if (chunks.length >= 3) {
                    /// Table title line
                    if (tableStart == 0) {
                        tableStart = i;
                        for (int c = 0; c < chunks.length; ++c) {
                            String chunk = chunks[c];
                            if (chunk.equals("证据名称") || chunk.equals("证据材料")) {
                                eChunk = c;
                            }
                        }
                    } else {
                        if (eChunk != -1) {
                            String evidence = chunks[eChunk];
                            if (evidence != null && !evidence.isEmpty()) {
                                String c[] = evidence.split("[\\s：]");
                                if (c.length == 1) {
                                    evidence = c[0];
                                } else {
                                    evidence = c[1];
                                }
                                eList.add(evidence);
                                logger.debug("Evidence added: " + evidence);
                            }
                        }
                    }
                    tableEnd = i;
                }
            }

            logger.debug("Table start: " + tableStart);
            logger.debug("Table end: " + tableEnd);
            if (tableStart + tableEnd == 0) {
                logger.error("Did not find evidence list in file. Please check file format!!!");
            }
        }

        if (eList.isEmpty()) {
            logger.warn("evidenceList.isEmpty()");
            addWarningToUser("在证据列表中未找到证据: " + inputPath);
        }
        return eList;
    }
}
