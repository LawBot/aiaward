/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.fileprocessor;

import cn.com.xiaofabo.scia.aiaward.entities.ArbitrationApplication;
import cn.com.xiaofabo.scia.aiaward.entities.ArbitrationProposer;
import cn.com.xiaofabo.scia.aiaward.entities.ArbitrationRespondent;
import cn.com.xiaofabo.scia.aiaward.entities.ArbitrationRoutine;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFNum;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocDefaults;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPrDefault;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;

/**
 *
 * @author 陈光曦
 */
public class AwardDocGenerator extends DocGenerator {

    public static final Logger logger = Logger.getLogger(AwardDocGenerator.class.getName());
    private final String outAwardDocUrl;
    private final XWPFDocument awardDoc;

    /// Constants
    private static final BigInteger PAGE_MARGIN_TOP = BigInteger.valueOf(2153L);
    private static final BigInteger PAGE_MARGIN_BOTTOM = BigInteger.valueOf(2493L);
    private static final BigInteger PAGE_MARGIN_LEFT = BigInteger.valueOf(1796L);
    private static final BigInteger PAGE_MARGIN_RIGHT = BigInteger.valueOf(1796L);
    private static final BigInteger PAGE_MARGIN_HEADER = BigInteger.valueOf(850L);
    private static final BigInteger PAGE_MARGIN_FOOTER = BigInteger.valueOf(2153L);

    private static final BigInteger PAGE_NUMBER_START = BigInteger.valueOf(0L);

    private static final BigInteger TEXT_LINE_SPACING = BigInteger.valueOf(500L);

    private static final BigInteger TABLE_KEY_WIDTH = BigInteger.valueOf(2267L);    /// ~4.0cm
    private static final BigInteger TABLE_VALUE_WIDTH = BigInteger.valueOf(5950L);  /// ~10.5cm

    private static final BigInteger DEFAULT_FONT_SIZE_HALF_16 = BigInteger.valueOf(32L);
    private static final BigInteger DEFAULT_FONT_SIZE_HALF_9 = BigInteger.valueOf(18L);

    private static final int FONT_SIZE_FOOTER = 10;

    public AwardDocGenerator(String outAwardDocUrl) {
        PropertyConfigurator.configure("log/config.txt");
        logger.trace("Constructor: start constructing AwardDocGenerator");
        this.outAwardDocUrl = outAwardDocUrl;

        awardDoc = new XWPFDocument();
        logger.trace("Empty award document created");

        logger.trace("Constructor: finish constructing AwardDocGenerator");
    }

    public XWPFDocument generateAwardDoc(ArbitrationRoutine routine,
            ArbitrationApplication aApplication,
            List appEvidenceList,
            String respondConent,
            List resEvidenceList) {
        logger.info("Generating award to file: " + outAwardDocUrl);

        pageSetup();
        generateFirstPage(aApplication, routine);
        generateContentPages(routine,
                aApplication,
                appEvidenceList,
                respondConent,
                resEvidenceList);
//        generateSignaturePage();
        try {
            int lastIdx1 = outAwardDocUrl.lastIndexOf("/");
            int lastIdx2 = outAwardDocUrl.lastIndexOf("\\");
            int lastIdx = lastIdx1 > lastIdx2 ? lastIdx1 : lastIdx2;
            String directoryName = outAwardDocUrl.substring(0, lastIdx);
            File directory = new File(directoryName);
            if (!directory.exists()) {
                directory.mkdir();
                logger.info("Creating folder:" + directoryName);
            }
            FileOutputStream fos = new FileOutputStream(outAwardDocUrl);
            awardDoc.write(fos);
            fos.close();
            logger.debug("SUCCESS: Award document generated.");
        } catch (FileNotFoundException e) {
            logger.fatal("Cannot create output file: " + outAwardDocUrl);
            logger.fatal("Please make sure parent folder exists!");
            logger.fatal("PROGRAM ABORTED!!!");
            addErrorToUser("无法生成裁决书文件，请确认文件上层目录存在");
            return null;
        } catch (IOException e) {
            logger.fatal("Cannot write to output file: " + outAwardDocUrl);
            logger.fatal("PROGRAM ABORTED!!!");
            addErrorToUser("无法写入裁决书文件");
            return null;
        }

        logger.info("Award successfully generated!");
        return awardDoc;
    }

    // Page setup according to requirement doc
    private void pageSetup() {
        CTDocument1 document = awardDoc.getDocument();

        CTBody body = document.getBody();

        if (!body.isSetSectPr()) {
            body.addNewSectPr();
        }
        CTSectPr section = body.getSectPr();

        if (!section.isSetPgSz()) {
            section.addNewPgSz();
        }

        CTPageSz pageSize = section.getPgSz();

        pageSize.setW(PAGE_A4_WIDTH);
        pageSize.setH(PAGE_A4_HEIGHT);
        pageSize.setOrient(STPageOrientation.PORTRAIT);

        CTSectPr sectPr = body.addNewSectPr();
        CTPageMar pageMar = sectPr.addNewPgMar();
        pageMar.setTop(PAGE_MARGIN_TOP);
        pageMar.setBottom(PAGE_MARGIN_BOTTOM);
        pageMar.setLeft(PAGE_MARGIN_LEFT);
        pageMar.setRight(PAGE_MARGIN_RIGHT);
        pageMar.setHeader(PAGE_MARGIN_HEADER);
        pageMar.setFooter(PAGE_MARGIN_FOOTER);

        XWPFStyles styles = awardDoc.createStyles();
        CTFonts fonts = CTFonts.Factory.newInstance();
        fonts.setEastAsia(FONT_FAMILY_SONG);
        fonts.setAscii(FONT_FAMILY_TIME_NEW_ROMAN);
        styles.setDefaultFonts(fonts);

        CTStyles ctStyles = CTStyles.Factory.newInstance();

        if (!ctStyles.isSetDocDefaults()) {
            ctStyles.addNewDocDefaults();
        }

        CTDocDefaults ctDocDefaults = ctStyles.getDocDefaults();

        if (!ctDocDefaults.isSetRPrDefault()) {
            ctDocDefaults.addNewRPrDefault();
        }

        CTRPrDefault ctRprDefault = ctDocDefaults.getRPrDefault();

        if (!ctRprDefault.isSetRPr()) {
            ctRprDefault.addNewRPr();
        }

        CTRPr ctRpr = ctRprDefault.getRPr();

        if (!ctRpr.isSetSz()) {
            ctRpr.addNewSz();
        }

        if (!ctRpr.isSetSzCs()) {
            ctRpr.addNewSzCs();
        }

        CTHpsMeasure sz = ctRpr.getSz();
        sz.setVal(DEFAULT_FONT_SIZE_HALF_16);

        CTHpsMeasure szCs = ctRpr.getSzCs();
        szCs.setVal(DEFAULT_FONT_SIZE_HALF_16);

        styles.setStyles(ctStyles);

        generateFooter();

        logger.trace("Page setup finished.");
    }

    private void createHeader() {
        XWPFHeader header = awardDoc.createHeader(HeaderFooterType.FIRST);
        XWPFParagraph paragraph = awardDoc.createParagraph();
        XWPFRun run = paragraph.createRun();

        paragraph = header.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);

        run.setText("The first page header:");

        // create default page header
        header = awardDoc.createHeader(HeaderFooterType.DEFAULT);

        paragraph = header.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);

        run = paragraph.createRun();
        run.setText("The default page header:");
    }

    private void generateFooter() {
        XWPFParagraph paragraph = awardDoc.createParagraph();
        XWPFFooter footer;
        footer = awardDoc.createFooter(HeaderFooterType.FIRST);
        paragraph = footer.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);

        footer = awardDoc.createFooter(HeaderFooterType.DEFAULT);

        paragraph = footer.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);

//        paragraph.getCTP().addNewR().addNewRPr().addNewSz().setVal(BigInteger.valueOf(18));
//
        XWPFRun run = paragraph.createRun();
        run.getCTR().addNewFldChar().setFldCharType(STFldCharType.BEGIN);
        run = paragraph.createRun();
        run.setFontSize(CN_FONT_SIZE_XIAO_WU);
        run.getCTR().addNewInstrText().setStringValue("PAGE \\* MERGEFORMAT");
        run = paragraph.createRun();
        run.getCTR().addNewFldChar().setFldCharType(STFldCharType.SEPARATE);
        run = paragraph.createRun();
        run.getCTR().addNewRPr().addNewNoProof();
        run = paragraph.createRun();
        run.getCTR().addNewFldChar().setFldCharType(STFldCharType.END);
        
//        CTP ctp = paragraph.getCTP();
//        ctp.addNewFldSimple().setInstr("PAGE \\* MERGEFORMAT");
        
        CTDocument1 document = awardDoc.getDocument();

        CTBody body = document.getBody();

        if (!body.isSetSectPr()) {
            body.addNewSectPr();
        }
        CTSectPr section = body.getSectPr();

        if (!section.isSetPgSz()) {
            section.addNewPgSz();
        }
        CTPageNumber pageNumber = section.getPgNumType();
        if (pageNumber == null) {
            pageNumber = section.addNewPgNumType();
        }
        pageNumber.setStart(PAGE_NUMBER_START);

//        XWPFHeaderFooterPolicy headerFooterPolicy = awardDoc.getHeaderFooterPolicy();
//        if (headerFooterPolicy == null) {
//            headerFooterPolicy = awardDoc.createHeaderFooterPolicy();
//        }
    }

    private void generateFirstPage(ArbitrationApplication aApplication, ArbitrationRoutine routine) {

        XWPFParagraph p1 = awardDoc.createParagraph();
        p1.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun p1r1 = p1.createRun();
        p1r1.setFontFamily(FONT_FAMILY_SONG);
        p1r1.setFontSize(CN_FONT_SIZE_XIAO_YI);
        p1r1.setText("华南国际经济贸易仲裁委员会\n");

        XWPFParagraph p2 = awardDoc.createParagraph();
        p2.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun p2r1 = p2.createRun();
        p2r1.setFontFamily(FONT_FAMILY_SONG);
        p2r1.setFontSize(CN_FONT_SIZE_XIAO_YI);
        p2r1.addBreak();
        p2r1.setText("裁    决    书");
        p2r1.addBreak();
        p2r1.addBreak();

        for (int i = 0; i < routine.getProposerList().size(); ++i) {
            ArbitrationProposer pro = (ArbitrationProposer) routine.getProposerList().get(i);
            addProposerTable(pro, i + 1, routine.getProposerList().size());
            breakLine();
        }

        for (int i = 0; i < routine.getRespondentList().size(); ++i) {
            ArbitrationRespondent res = (ArbitrationRespondent) routine.getRespondentList().get(i);
            addRespondentTable(res, i + 1, routine.getRespondentList().size());
            breakLine();
        }

        XWPFParagraph p5 = awardDoc.createParagraph();
        p5.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun p5r1 = p5.createRun();
        p5r1.setFontFamily(FONT_FAMILY_FANGSONG);
        p5r1.setFontSize(CN_FONT_SIZE_XIAO_ER);
        p5r1.addBreak();
        p5r1.addBreak();
        p5r1.setText("深   圳");

        XWPFParagraph p6 = awardDoc.createParagraph();
        p6.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun p6r1 = p6.createRun();
        p6r1.setFontFamily(FONT_FAMILY_FANGSONG);
        p6r1.setFontSize(CN_FONT_SIZE_XIAO_ER);
        p6r1.addBreak();
//        p6r1.setText(cnDateGenerator());  /// Generate today's date
        p6r1.setText(routine.getAwardDate().trim());
        p6r1.addBreak(BreakType.PAGE);
    }

    private void generateContentPages(ArbitrationRoutine routine,
            ArbitrationApplication aApplication,
            List appEvidenceList,
            String respondContent,
            List resEvidenceList) {
        XWPFParagraph p1 = awardDoc.createParagraph();
        p1.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun p1r1 = p1.createRun();
        p1r1.setFontFamily(FONT_FAMILY_SONG);
        p1r1.setFontSize(CN_FONT_SIZE_XIAO_YI);
        p1r1.setText("裁    决    书\n");

        XWPFParagraph p3 = awardDoc.createParagraph();
        p3.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun p3r1 = p3.createRun();
        p3r1.setFontFamily(FONT_FAMILY_FANGSONG);
        p3r1.setFontSize(CN_FONT_SIZE_SAN);
        p3r1.addBreak();
        p3r1.setText("华南国仲深裁〔XXX〕X号");
        p3r1.addBreak();

        addNormalTextParagraphs(routine.getRoutineText(), 0, 0);

        addSubTitle("一、案    情");
        addTitleTextParagraph("（一）申请人的主张和请求", 0);

        /// 申请人的主张和请求
        /// Dirty implementation. TODO: regex deletion of original numbering with wrong format
        String lines[] = aApplication.getRequest().split("\\r?\\n");
        for (int i = 0; i < lines.length; ++i) {
            if (lines[i].matches("^[0-9].*")) {
                if (lines[i].length() >= 3) {
                    lines[i] = lines[i].substring(2).trim();
                }
            }
        }
        addNumbering(lines, 0, 1);

        /// 申请人诉称
        addNormalTextParagraph("申请人诉称：", 0);
        addNormalTextParagraphs(aApplication.getFactAndReason().trim(), 0, 1);

        /// 申请人提交证据
        if (appEvidenceList != null && !appEvidenceList.isEmpty()) {
            addNormalTextParagraph("申请人为支持仲裁请求提交了以下证据：", 0);
            for (int i = 0; i < appEvidenceList.size(); ++i) {
                List eList = (List) appEvidenceList.get(i);
                if (i == 0) {
                    String eLines[] = (String[]) eList.toArray(new String[eList.size()]);
                    addNumbering(eLines, 0, 1);
                } else {
                    char c = (char) (i + '0');
                    String tmpStr = "补充证据" + numberToCN(c) + "：";
                    addNormalTextParagraph(tmpStr, 0);
                    String eLines[] = (String[]) eList.toArray(new String[eList.size()]);
                    addNumbering(eLines, 0, 1);
                }
            }
        }

        if (respondContent != null && !respondContent.isEmpty()) {
            addTitleTextParagraph("（二）被申请人提出如下答辩意见", 0);
            addNormalTextParagraphs(respondContent, 0, 1);
        }

        if (resEvidenceList != null && !resEvidenceList.isEmpty()) {
            addNormalTextParagraph("被申请人为支持仲裁请求提交了以下证据：", 0);
            for (int i = 0; i < resEvidenceList.size(); ++i) {
                List eList = (List) resEvidenceList.get(i);
                if (i == 0) {
                    String eLines[] = (String[]) eList.toArray(new String[eList.size()]);
                    addNumbering(eLines, 0, 1);
                } else {
                    char c = (char) (i + '0');
                    String tmpStr = "补充证据" + numberToCN(c) + "：";
                    addNormalTextParagraph(tmpStr, 0);
                    String eLines[] = (String[]) eList.toArray(new String[eList.size()]);
                    addNumbering(eLines, 0, 1);
                }
            }
        }

        addSubTitle("二、仲裁庭意见");
        addNormalTextParagraph("就本案争议，仲裁庭意见如下：", 1);
//        addNormalTextParagraph("{意见描述部分}", 0);

        addSubTitle("三、裁    决");
        addNormalTextParagraph("根据上述事实和仲裁庭意见，仲裁庭对本案作出裁决如下：", 0);
//        addNormalTextParagraph("{裁决描述部分}", 2);

        addNormalTextParagraph("本裁决为终局裁决。", 0);
        addNormalTextParagraph("（紧接下一页）", 0);

        breakToNextPage();
    }

    private void generateSignaturePage() {
        addNormalTextParagraph("（此页无正文）", 4);

        addSignatureText("首席仲裁员：");
        addSignatureText("仲  裁  员：");
        addSignatureText("仲  裁  员：");

        addSignatureDate(cnDateGenerator() + "于深圳");
    }

    private void addNumbering(String[] strList, int emptyLineInBetween, int emptyLineAfter) {
        CTAbstractNum cTAbstractNum = CTAbstractNum.Factory.newInstance();
        cTAbstractNum.setAbstractNumId(BigInteger.valueOf(0));

        CTLvl cTLvl = cTAbstractNum.addNewLvl();
        cTLvl.addNewNumFmt().setVal(STNumberFormat.DECIMAL);
        cTLvl.addNewLvlText().setVal("%1.");
        cTLvl.addNewStart().setVal(BigInteger.ONE);

        XWPFAbstractNum abstractNum = new XWPFAbstractNum(cTAbstractNum);

        XWPFNumbering numbering = awardDoc.createNumbering();
        numbering.removeAbstractNum(BigInteger.ZERO);
        BigInteger abstractNumID = numbering.addAbstractNum(abstractNum);
        BigInteger numID = numbering.addNum(abstractNumID);

        /// Reset numbering start from 1.
        XWPFNum num = numbering.getNum(numID);
        CTNumLvl lvlOverride = num.getCTNum().addNewLvlOverride();
        lvlOverride.setIlvl(BigInteger.ZERO);
        CTDecimalNumber number = lvlOverride.addNewStartOverride();
        number.setVal(BigInteger.ONE);

        for (String str : strList) {
//            if (str.matches("^[0-9].*")) {
//                if (str.length() >= 3) {
//                    str = str.substring(2).trim();
//                }
//            }
            str = str.trim();
            str = findAndCorrectMoneyFormats(str);
            XWPFParagraph paragraph = awardDoc.createParagraph();

            CTPPr ppr = paragraph.getCTP().getPPr();
            if (ppr == null) {
                ppr = paragraph.getCTP().addNewPPr();
            }

            CTSpacing spacing = ppr.isSetSpacing() ? ppr.getSpacing() : ppr.addNewSpacing();
            spacing.setBefore(BigInteger.valueOf(0L));
            spacing.setAfter(BigInteger.valueOf(0L));
            spacing.setLineRule(STLineSpacingRule.EXACT);
            spacing.setLine(TEXT_LINE_SPACING);
            paragraph.setFirstLineIndent(CN_FONT_SIZE_SAN * 2 * 20);
            paragraph.setNumID(numID);
            XWPFRun run = paragraph.createRun();
            run.setFontFamily(FONT_FAMILY_FANGSONG);
            run.getCTR().getRPr().getRFonts().setAscii(FONT_FAMILY_TIME_NEW_ROMAN);
            run.getCTR().getRPr().getRFonts().setHAnsi(FONT_FAMILY_TIME_NEW_ROMAN);
            run.getCTR().getRPr().getRFonts().setEastAsia(FONT_FAMILY_FANGSONG);
            run.setFontSize(CN_FONT_SIZE_SAN);
            run.setText(str);
        }

        /// Insert empty lines after all numbering
        for (int i = 0; i < emptyLineAfter; ++i) {
            XWPFParagraph paragraph = awardDoc.createParagraph();
        }
    }

    private void addTextParagraph(String str, int emptyLineAfter, boolean bold) {
        XWPFParagraph paragraph = awardDoc.createParagraph();

        CTPPr ppr = paragraph.getCTP().getPPr();
        if (ppr == null) {
            ppr = paragraph.getCTP().addNewPPr();
        }
        CTSpacing spacing = ppr.isSetSpacing() ? ppr.getSpacing() : ppr.addNewSpacing();
        spacing.setBefore(BigInteger.valueOf(0L));
        spacing.setAfter(BigInteger.valueOf(0L));
        spacing.setLineRule(STLineSpacingRule.EXACT);
        spacing.setLine(TEXT_LINE_SPACING);

        paragraph.setAlignment(ParagraphAlignment.BOTH);
        paragraph.setFirstLineIndent(CN_FONT_SIZE_SAN * 2 * 20);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily(FONT_FAMILY_FANGSONG);
        run.getCTR().getRPr().getRFonts().setAscii(FONT_FAMILY_TIME_NEW_ROMAN);
        run.getCTR().getRPr().getRFonts().setHAnsi(FONT_FAMILY_TIME_NEW_ROMAN);
        run.getCTR().getRPr().getRFonts().setEastAsia(FONT_FAMILY_FANGSONG);
        run.setFontSize(CN_FONT_SIZE_SAN);
        run.setBold(bold);
        run.setText(str);
        for (int i = 0; i < emptyLineAfter; ++i) {
            run.addBreak();
        }
    }

    private void addNormalTextParagraph(String str, int emptyLineAfter) {
        addTextParagraph(findAndCorrectMoneyFormats(str), emptyLineAfter, false);
    }

    private void addNormalTextParagraphs(String str, int emptyLineInBetween, int emptyLineAfter) {
        String lines[] = str.split("\\r?\\n");
        for (int i = 0; i < lines.length; ++i) {
            String line = lines[i].trim();
            if (line != null && !line.isEmpty()) {
                addTextParagraph(findAndCorrectMoneyFormats(line), emptyLineInBetween, false);
            }
        }
        for (int i = 0; i < emptyLineAfter; ++i) {
            addTextParagraph("", emptyLineAfter - 1, false);
        }
    }

    private void addTitleTextParagraph(String str, int emptyLineAfter) {
        addTextParagraph(str, emptyLineAfter, true);
    }

    private void addSubTitle(String str) {
        XWPFParagraph paragraph = awardDoc.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily(FONT_FAMILY_HEITI);
        run.setFontSize(CN_FONT_SIZE_ER);
        run.addBreak();
        run.setText(str);
        run.addBreak();
        run.addBreak();
    }

    private void addSignatureText(String str) {
        XWPFParagraph paragraph = awardDoc.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily(FONT_FAMILY_KAITI);
        run.setFontSize(CN_FONT_SIZE_ER);
        run.setText(str);
        run.addBreak();
        run.addBreak();
    }

    private void addSignatureDate(String str) {
        XWPFParagraph paragraph = awardDoc.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily(FONT_FAMILY_KAITI);
        run.setFontSize(CN_FONT_SIZE_ER);
        run.setText(str);
    }

    private void breakLine() {
        XWPFParagraph paragraph = awardDoc.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setFontFamily(FONT_FAMILY_SONG);
        run.setFontSize(CN_FONT_SIZE_SAN);
        run.addBreak();
    }

    private void breakToNextPage() {
        XWPFParagraph paragraph = awardDoc.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.addBreak(BreakType.PAGE);
    }

    private void setTableBorderToNone(XWPFTable proposerTable) {
        CTTblPr tblpro;
        CTTblBorders borders;
        tblpro = proposerTable.getCTTbl().getTblPr();
        borders = tblpro.addNewTblBorders();
        borders.addNewBottom().setVal(STBorder.NONE);
        borders.addNewLeft().setVal(STBorder.NONE);
        borders.addNewRight().setVal(STBorder.NONE);
        borders.addNewTop().setVal(STBorder.NONE);
        borders.addNewInsideH().setVal(STBorder.NONE);
        borders.addNewInsideV().setVal(STBorder.NONE);
    }

    private void setTableRowContent(XWPFTableRow tableRow, String key, String value) {
        XWPFParagraph paragraph;
        XWPFRun paragraphRun;
        tableRow.getCell(0).removeParagraph(0);
        tableRow.getCell(0).getCTTc().addNewTcPr().addNewTcW().setW(TABLE_KEY_WIDTH);
        paragraph = tableRow.getCell(0).addParagraph();
        paragraph.setAlignment(ParagraphAlignment.DISTRIBUTE);
        paragraphRun = paragraph.createRun();
        paragraphRun.setFontFamily(FONT_FAMILY_FANGSONG);
        paragraphRun.setFontSize(CN_FONT_SIZE_SAN);
        paragraphRun.setText(key);

        tableRow.getCell(1).removeParagraph(0);
        tableRow.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(TABLE_VALUE_WIDTH);
        paragraph = tableRow.getCell(1).addParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        paragraphRun = paragraph.createRun();
        paragraphRun.setFontFamily(FONT_FAMILY_FANGSONG);
        paragraphRun.setFontSize(CN_FONT_SIZE_SAN);
        paragraphRun.setText(value);
    }

    private void addProposerTable(ArbitrationProposer pro, int countPro, int totalCount) {
        XWPFTable proposerTable = awardDoc.createTable(4, 2);
        setTableBorderToNone(proposerTable);
        CTTblLayoutType type = proposerTable.getCTTbl().getTblPr().addNewTblLayout();
        type.setType(STTblLayoutType.FIXED);

        /// Doesn't seem to have any effect
        proposerTable.getCTTbl().addNewTblGrid().addNewGridCol().setW(TABLE_KEY_WIDTH);
        proposerTable.getCTTbl().getTblGrid().addNewGridCol().setW(TABLE_VALUE_WIDTH);

        String proKey = "";
        if (totalCount == 1) {
            proKey = "申  请  人：";
        } else {
            proKey = "第" + numberToCN((char) (countPro + '0')) + "申请人：";
        }
        setTableRowContent(proposerTable.getRow(0), proKey, pro.getProposer());
        setTableRowContent(proposerTable.getRow(1), "地      址：", pro.getAddress());
        setTableRowContent(proposerTable.getRow(2), "法定代表人：", pro.getRepresentative());
        setTableRowContent(proposerTable.getRow(3), "代  理  人：", pro.getAgency());
    }

    private void addRespondentTable(ArbitrationRespondent res, int countRes, int totalCount) {
        XWPFTable respondentTable = awardDoc.createTable(4, 2);
        setTableBorderToNone(respondentTable);
        CTTblLayoutType type = respondentTable.getCTTbl().getTblPr().addNewTblLayout();
        type.setType(STTblLayoutType.FIXED);

        /// Doesn't seem to have any effect
        respondentTable.getCTTbl().addNewTblGrid().addNewGridCol().setW(TABLE_KEY_WIDTH);
        respondentTable.getCTTbl().getTblGrid().addNewGridCol().setW(TABLE_VALUE_WIDTH);

        String resKey = "";
        if (totalCount == 1) {
            resKey = "被申请人：";
        } else {
            resKey = "第" + numberToCN((char) (countRes + '0')) + "被申请人：";
        }
        setTableRowContent(respondentTable.getRow(0), resKey, res.getRespondent());
        setTableRowContent(respondentTable.getRow(1), "地      址：", res.getAddress());
        setTableRowContent(respondentTable.getRow(2), "法定代表人：", res.getRepresentative());
        setTableRowContent(respondentTable.getRow(3), "代  理  人：", res.getAgency());
    }
}
