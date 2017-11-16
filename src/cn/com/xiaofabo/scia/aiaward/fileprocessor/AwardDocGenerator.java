/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.fileprocessor;

import cn.com.xiaofabo.scia.aiaward.entities.ArbitrationApplication;
import cn.com.xiaofabo.scia.aiaward.entities.Proposer;
import cn.com.xiaofabo.scia.aiaward.entities.Respondent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocDefaults;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;

/**
 *
 * @author 陈光曦
 */
public class AwardDocGenerator implements OutputGenerator {

    public static final Logger logger = Logger.getLogger(AwardDocGenerator.class.getName());
    private final String outAwardDocUrl;
    private final XWPFDocument awardDoc;

    /// Constants
    private static final BigInteger PAGE_A4_WIDTH = BigInteger.valueOf(11900L);
    private static final BigInteger PAGE_A4_HEIGHT = BigInteger.valueOf(16840L);

    private static final BigInteger PAGE_MARGIN_TOP = BigInteger.valueOf(2153L);
    private static final BigInteger PAGE_MARGIN_BOTTOM = BigInteger.valueOf(2493L);
    private static final BigInteger PAGE_MARGIN_LEFT = BigInteger.valueOf(1796L);
    private static final BigInteger PAGE_MARGIN_RIGHT = BigInteger.valueOf(1796L);
    private static final BigInteger PAGE_MARGIN_HEADER = BigInteger.valueOf(850L);
    private static final BigInteger PAGE_MARGIN_FOOTER = BigInteger.valueOf(2153L);

    private static final BigInteger PAGE_NUMBER_START = BigInteger.valueOf(0L);

    private static final BigInteger TEXT_LINE_SPACING = BigInteger.valueOf(500L);

    private static final BigInteger TABLE_KEY_WIDTH = BigInteger.valueOf(960L);

    private static final BigInteger DEFAULT_FONT_SIZE_HALF_16 = BigInteger.valueOf(32L);

    private static final int CN_FONT_SIZE_XIAO_YI = 24;
    private static final int CN_FONT_SIZE_ER = 22;
    private static final int CN_FONT_SIZE_XIAO_ER = 18;
    private static final int CN_FONT_SIZE_SAN = 16;

    private static final int FONT_SIZE_FOOTER = 10;

    private static final String FONT_FAMILY_TIME_NEW_ROMAN = "Times New Roman";
    private static final String FONT_FAMILY_SONG = "宋体";
    private static final String FONT_FAMILY_FANGSONG = "仿宋_GB2312";
    private static final String FONT_FAMILY_HEITI = "黑体";
    private static final String FONT_FAMILY_KAITI = "楷体";

    public AwardDocGenerator(String outAwardDocUrl) {
        PropertyConfigurator.configure("log/config.txt");
        logger.trace("Constructor: start constructing AwardDocGenerator");
        this.outAwardDocUrl = outAwardDocUrl;

        awardDoc = new XWPFDocument();
        logger.trace("Empty award document created");

        logger.trace("Constructor: finish constructing AwardDocGenerator");
    }

    public XWPFDocument generateAwardDoc(ArbitrationApplication aApplication, String routineContent) {
        logger.info("Start generating award to file: " + outAwardDocUrl);

        pageSetup();
        generateFirstPage(aApplication);
        generateContentPages(aApplication, routineContent);
//        generateSignaturePage();
        try {
            FileOutputStream fos = new FileOutputStream(outAwardDocUrl);
            awardDoc.write(fos);
            fos.close();
            logger.debug("SUCCESS: Award document generated.");
        } catch (FileNotFoundException e) {
            logger.fatal("Cannot create output file: " + outAwardDocUrl);
            logger.fatal("Please make sure parent folder exists!");
            logger.fatal("PROGRAM ABORTED!!!");
        } catch (IOException e) {
            logger.fatal("Cannot write to output file: " + outAwardDocUrl);
            logger.fatal("PROGRAM ABORTED!!!");
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

        createFooter();

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

    private void createFooter() {
        XWPFParagraph paragraph = awardDoc.createParagraph();
        XWPFRun run = paragraph.createRun();
        XWPFFooter footer;
        footer = awardDoc.createFooter(HeaderFooterType.FIRST);
        paragraph = footer.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);

        footer = awardDoc.createFooter(HeaderFooterType.DEFAULT);

        paragraph = footer.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);

        run = paragraph.createRun();
        paragraph.getCTP().addNewFldSimple().setInstr("PAGE \\* MERGEFORMAT");

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

        XWPFHeaderFooterPolicy headerFooterPolicy = awardDoc.getHeaderFooterPolicy();
        if (headerFooterPolicy == null) {
            headerFooterPolicy = awardDoc.createHeaderFooterPolicy();
        }
    }

    private void generateFirstPage(ArbitrationApplication aApplication) {
        /// TODO: only 1 proposer and 1 respondent considered
        Proposer pro = (Proposer) aApplication.getProposerList().get(0);
        Respondent res = (Respondent) aApplication.getRespondentList().get(0);

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

        XWPFTable proposerTable = awardDoc.createTable(4, 2);
        setTableBorderToNone(proposerTable);

        setTableRowContent(proposerTable.getRow(0), "申  请  人：", pro.getProposer(), true);
        setTableRowContent(proposerTable.getRow(1), "地      址：", pro.getAddress(), false);
        setTableRowContent(proposerTable.getRow(2), "法定代表人：", pro.getRepresentative(), false);
        setTableRowContent(proposerTable.getRow(3), "代  理  人：", pro.getAgency(), false);

        XWPFParagraph p3 = awardDoc.createParagraph();
        p3.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun p3r1 = p3.createRun();
        p3r1.setFontFamily(FONT_FAMILY_SONG);
        p3r1.setFontSize(CN_FONT_SIZE_SAN);
        p3r1.addBreak();

        XWPFTable respondentTable = awardDoc.createTable(4, 2);
        setTableBorderToNone(respondentTable);

        setTableRowContent(respondentTable.getRow(0), "被申请人：", res.getRespondent(), true);
        setTableRowContent(respondentTable.getRow(1), "地      址：", res.getAddress(), false);
        setTableRowContent(respondentTable.getRow(2), "法定代表人：", res.getRepresentative(), false);
        setTableRowContent(respondentTable.getRow(3), "代  理  人：", res.getAgency(), false);

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
        p6r1.setText(cnDateGenerator());
        p6r1.addBreak(BreakType.PAGE);

    }

    private void generateContentPages(ArbitrationApplication aApplication, String routineContent) {
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

        addNormalTextParagraphs(routineContent, 0, 0);

        addSubTitle("一、案    情");
        addTitleTextParagraph("（一）申请人的主张和请求", 0);
//        addNormalTextParagraph("{申请人的主张和请求}", 0);
        String lines[] = aApplication.getRequest().split("\\r?\\n");
        addNumbering(lines);
        addNormalTextParagraph("申请人诉称：", 0);
        addNormalTextParagraphs(aApplication.getFactAndReason().trim(), 0, 1);

        addTitleTextParagraph("（二）被申请人提出如下答辩意见", 0);
        addNormalTextParagraph("{被申请人提出答辩意见}", 0);

        addSubTitle("二、仲裁庭意见");
        addNormalTextParagraph("就本案争议，仲裁庭意见如下：", 1);
        addNormalTextParagraph("{意见描述部分}", 0);

        addSubTitle("三、裁    决");
        addNormalTextParagraph("根据上述事实和仲裁庭意见，仲裁庭对本案作出裁决如下：", 0);
        addNormalTextParagraph("{裁决描述部分}", 2);

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

    private String cnDateGenerator() {
        String toReturn = "";
        Calendar cal = Calendar.getInstance();

        int year = cal.get(Calendar.YEAR);
        String yearStr = year + "";
        String yearStrCN = numberToCN(yearStr.charAt(0))
                + numberToCN(yearStr.charAt(1))
                + numberToCN(yearStr.charAt(2))
                + numberToCN(yearStr.charAt(3));

        int month = cal.get(Calendar.MONTH) + 1;
        String monthStr = month + "";
        String monthStrCN = "";
        switch (month) {
            case 10:
                monthStrCN = "十";
                break;
            case 11:
                monthStrCN = "十一";
                break;
            case 12:
                monthStrCN = "十二";
                break;
            default:
                monthStrCN = numberToCN(monthStr.charAt(0));
                break;
        }

        int date = cal.get(Calendar.DATE);
        String dateStr = date + "";
        String dateStrCN = "";
        if (date == 10) {
            dateStrCN = "十";
        } else if (date < 10) {
            dateStrCN = numberToCN(dateStr.charAt(0));
        } else if (date > 10 && date < 20) {
            dateStrCN = "十" + numberToCN(dateStr.charAt(1));
        } else if (date == 20) {
            dateStrCN = "二十";
        } else if (date > 20 && date < 30) {
            dateStrCN = "二十" + numberToCN(dateStr.charAt(1));
        } else if (date == 30) {
            dateStrCN = "三十";
        } else if (date == 31) {
            dateStrCN = "三十一";
        } else {
            dateStrCN = "X";
        }

        toReturn = yearStrCN + "年" + monthStrCN + "月" + dateStrCN + "日";
        return toReturn;
    }

    private String numberToCN(char number) {
        if (number > '9' || number < '0') {
            return null;
        }
        String toReturn;
        switch (number) {
            case '0':
                toReturn = "○";
                break;
            case '1':
                toReturn = "一";
                break;
            case '2':
                toReturn = "二";
                break;
            case '3':
                toReturn = "三";
                break;
            case '4':
                toReturn = "四";
                break;
            case '5':
                toReturn = "五";
                break;
            case '6':
                toReturn = "六";
                break;
            case '7':
                toReturn = "七";
                break;
            case '8':
                toReturn = "八";
                break;
            case '9':
                toReturn = "九";
                break;
            default:
                toReturn = "X";
                break;
        }
        return toReturn;
    }

    private void addNumbering(String[] strList) {
        CTAbstractNum cTAbstractNum = CTAbstractNum.Factory.newInstance();
        cTAbstractNum.setAbstractNumId(BigInteger.valueOf(0));

        CTLvl cTLvl = cTAbstractNum.addNewLvl();
        cTLvl.addNewNumFmt().setVal(STNumberFormat.DECIMAL);
        cTLvl.addNewLvlText().setVal("%1.");
        cTLvl.addNewStart().setVal(BigInteger.valueOf(1));

        XWPFAbstractNum abstractNum = new XWPFAbstractNum(cTAbstractNum);
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

        XWPFNumbering numbering = awardDoc.createNumbering();
        BigInteger abstractNumID = numbering.addAbstractNum(abstractNum);
        BigInteger numID = numbering.addNum(abstractNumID);

        for (String str : strList) {
            if (str.matches("^[0-9].*")) {
                str = str.substring(2).trim();
            }
            str = str.trim();
            str = findAndCorrectMoneyFormats(str);
            paragraph = awardDoc.createParagraph();
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

        paragraph.setAlignment(ParagraphAlignment.LEFT);
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

    private void setTableRowContent(XWPFTableRow tableRow, String key, String value, boolean firstLine) {
        XWPFParagraph paragraph;
        XWPFRun paragraphRun;
        tableRow.getCell(0).removeParagraph(0);
        if (firstLine) {
            tableRow.getCell(0).getCTTc().addNewTcPr().addNewTcW().setW(TABLE_KEY_WIDTH);
        }
        paragraph = tableRow.getCell(0).addParagraph();
        paragraph.setAlignment(ParagraphAlignment.DISTRIBUTE);
        paragraphRun = paragraph.createRun();
        paragraphRun.setFontFamily(FONT_FAMILY_FANGSONG);
        paragraphRun.setFontSize(CN_FONT_SIZE_SAN);
        paragraphRun.setText(key);

        tableRow.getCell(1).removeParagraph(0);
        paragraph = tableRow.getCell(1).addParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        paragraphRun = paragraph.createRun();
        paragraphRun.setFontFamily(FONT_FAMILY_FANGSONG);
        paragraphRun.setFontSize(CN_FONT_SIZE_SAN);
        paragraphRun.setText(value);
    }

    private String findAndCorrectMoneyFormats(String str) {
        String toReturn = "";
        Pattern pattern = Pattern.compile(
                "(人民币)?[0-9.,，]+(万)?(亿)?元"
                + "|" + "[0-9.,，]+(万)?(亿)?美元"
                + "|" + "[0-9.,，]+(万)?(亿)?美金"
                + "|" + "[0-9.,，]+(万)?(亿)?欧元");
        Matcher matcher = pattern.matcher(str);

        List<MoneyString> moneyStrList = new LinkedList<>();
        while (matcher.find()) {
            String match = matcher.group();
            int start = matcher.start();
            int end = matcher.end();
            while (match.startsWith("，")) {
                match = match.substring(1);
                ++start;
            }
            moneyStrList.add(new MoneyString(start, end, match));
//            System.out.println(str.substring(start, end));
        }

        for (int i = moneyStrList.size() - 1; i >= 0; --i) {
            int start = moneyStrList.get(i).getStart();
            int end = moneyStrList.get(i).getEnd();
            String moneyStr = moneyStrList.get(i).getMoneyString();
            StringBuilder tmpStrBuilder = new StringBuilder();
            tmpStrBuilder.append(moneyStr);
            tmpStrBuilder.append("->");
            /// Extract only number part and format it
            Pattern p = Pattern.compile("[0-9.,，]+");
            Matcher m = p.matcher(moneyStr);
            /// Should only be one and only one match!
            if (m.find()) {
                String num = m.group();
                int s = m.start();
                int e = m.end();
                moneyStr = replaceStr(moneyStr, s, e, correctNumberFormat(num));
            }
            if (moneyStr.matches("[0-9.,，]+(万)?元")) {
                moneyStr = "人民币" + moneyStr;
            }
            str = replaceStr(str, start, end, moneyStr);
            tmpStrBuilder.append(moneyStr);
            logger.debug(tmpStrBuilder.toString());
        }

        return str;
    }

    private String replaceStr(String baseStr, int startIdx, int endIdx, String str) {
        String firstPart = baseStr.substring(0, startIdx);
        String secondPart = baseStr.substring(endIdx);
        return firstPart + str + secondPart;
    }

    private String correctNumberFormat(String s) {
        s = removeAllCommas(s);
        int backIdx = s.contains(".") ? s.indexOf(".") - 1 : s.length() - 1;
        backIdx -= 3;
        while (backIdx >= 0) {
            s = replaceStr(s, backIdx + 1, backIdx + 1, ",");
            backIdx -= 3;
        }
        return s;
    }

    private String removeAllCommas(String str) {
        return str.replaceAll("[,，]", "");
    }

    private class MoneyString {

        private final int start;
        private final int end;
        private final String moneyString;

        public MoneyString(int start, int end, String moneyString) {
            this.start = start;
            this.end = end;
            this.moneyString = moneyString;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public String getMoneyString() {
            return moneyString;
        }

    }
}
