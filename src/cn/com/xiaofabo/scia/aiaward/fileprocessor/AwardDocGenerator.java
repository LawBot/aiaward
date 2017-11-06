/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.fileprocessor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

/**
 *
 * @author cgx82
 */
public class AwardDocGenerator implements OutputGenerator {

    public static final Logger logger = Logger.getLogger(AwardDocGenerator.class.getName());
    private String outAwardDocUrl;
    private XWPFDocument awardDoc;
    
    //Constants
    private static final BigInteger PAGE_MARGIN_TOP = BigInteger.valueOf(2153L);
    private static final BigInteger PAGE_MARGIN_BOTTOM = BigInteger.valueOf(2493L);
    private static final BigInteger PAGE_MARGIN_LEFT = BigInteger.valueOf(1796L);
    private static final BigInteger PAGE_MARGIN_RIGHT = BigInteger.valueOf(1796L);
    private static final BigInteger PAGE_MARGIN_HEADER = BigInteger.valueOf(850L);
    private static final BigInteger PAGE_MARGIN_FOOTER = BigInteger.valueOf(2153L);
    

    public AwardDocGenerator(String outAwardDocUrl) {
        PropertyConfigurator.configure("log/config.txt");
        logger.trace("Constructor: start constructing AwardDocGenerator");
        this.outAwardDocUrl = outAwardDocUrl;

        awardDoc = new XWPFDocument();
        logger.debug("Empty award document created");

        logger.trace("Constructor: finish constructing AwardDocGenerator");
    }

    public XWPFDocument generateAwardDoc() {
        try {
            pageSetup();
            FileOutputStream fos = new FileOutputStream(outAwardDocUrl);
            awardDoc.write(fos);
            logger.debug("Award document generated.");
        } catch (FileNotFoundException e) {
            logger.fatal("Cannot create output file: " + outAwardDocUrl);
            logger.fatal("Please make sure parent folder exists!");
            logger.fatal("PROGRAM ABORTED!!!");
        } catch (IOException e) {
            logger.fatal("Cannot write to output file: " + outAwardDocUrl);
            logger.fatal("PROGRAM ABORTED!!!");
        }

        return awardDoc;
    }

    // Page setup according to requirement doc
    private void pageSetup() {
        CTSectPr sectPr = awardDoc.getDocument().getBody().addNewSectPr();
        CTPageMar pageMar = sectPr.addNewPgMar();
        pageMar.setTop(PAGE_MARGIN_TOP);
        pageMar.setBottom(PAGE_MARGIN_BOTTOM);
        pageMar.setLeft(PAGE_MARGIN_LEFT);
        pageMar.setRight(PAGE_MARGIN_RIGHT);
        pageMar.setHeader(PAGE_MARGIN_HEADER);
        pageMar.setFooter(PAGE_MARGIN_FOOTER);
        logger.trace("Page setup finished.");
    }

}
