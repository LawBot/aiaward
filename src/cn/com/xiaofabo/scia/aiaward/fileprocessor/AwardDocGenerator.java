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
import java.util.LinkedList;
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

    private static final BigInteger TABLE_KEY_WIDTH = BigInteger.valueOf(3005L);    /// ~4.0cm
    private static final BigInteger TABLE_VALUE_WIDTH = BigInteger.valueOf(5216L);  /// ~10.5cm

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

    /// Generating the cover page
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

        /// Generating arbitral comment
        generateArbitralComment();
        
        generateArbitration();

        breakToNextPage();
    }

    private void generateSignaturePage() {
        addNormalTextParagraph("（此页无正文）", 4);

        addSignatureText("首席仲裁员：");
        addSignatureText("仲  裁  员：");
        addSignatureText("仲  裁  员：");

        addSignatureDate(cnDateGenerator() + "于深圳");
    }
    
    private void generateArbitralComment(){
        String emptyList[] = {"", "", ""};
        addSubTitle("二、仲裁庭意见");
        addNormalTextParagraph("仲裁庭根据申请人的仲裁请求和被申请人的答辩意见，"
                + "结合本案的证据，审查了以下争议事实和法律问题。", 1);
        
        addTitleTextParagraph("（一）关于本案的管辖权 ", 0);
        addNormalTextParagraph("仲裁庭审查了申请人与被申请人与       年   月   日签订的"
                + "《      合同》后，认为上述合同是对双方当事人具有约束力的文件，且上述"
                + "《     合同》的第       条载有本会仲裁解决纠纷的规定，因此仲裁庭认为"
                + "本会对本案享有管辖权。", 1);
        
        addNormalTextParagraph("涉外案件，根据《最高人民法院关于适用<中华人民共和国涉外民事"
                + "法律关系适用法>若干问题的解释（一）》的规定，本案属于涉外案件。根据"
                + "《合同法》第一百二十六条的规定，涉外合同的当事人可以选择处理合同争议"
                + "所适用的法律，但法律另有规定的除外。根据本案申请人和被申请人之间"
                + "     年     月     日签订的《  合同》第   条，本合同适用"
                + "中国人民共和国法律，并将争议提交深圳国际仲裁院仲裁。"
                + "因此，本院对本案享有仲裁管辖权并适用中国法律 。", 1);
        
        addTitleTextParagraph("（二）关于合同的效力", 0);
        addNormalTextParagraph("仲裁庭认为，本案申请人与被申请人于    年    月     日"
                + "签订的《  合同》是双方当事人自愿协商签订的，是双方当事人的真实意思表示，"
                + "不违反中国的法律和行政法规的强制性规定，应属合法有效，"
                + "并对本案双方当事人具有约束力", 1);
        
        addTitleTextParagraph("（三） 关于本案相关事实的认定", 0);
        addNormalTextParagraph("根据申请人提交的证据材料和庭审调查，仲裁庭对有关本案争议的"
                + "相关事实认定如下（仲裁庭根据案情撰写）： ", 0);
        addNumbering(emptyList, 0, 1);
        
        addTitleTextParagraph("（四）关于本案的争议焦点", 0);
        String conflictList[] = {
            "买方是否按买卖合同的约定支付了货款？（合同法第159、160、161条）",
            "卖方是否按买卖合同的约定交付货物？（合同法第138、139、141条）",
            "卖方交付的货物是否符合质量标准？（合同法第153、154、155条）",
            "卖方交付的货物是否符合数量要求？（合同法第158条）",
            "买方是否存在违约责任？",
            "卖方是否存在违约责任？",
            "货物在运送过程中灭失或损坏由谁承担赔偿责任？（合同法第141、144、145条）",
            "由谁负责验货？（合同法第157、158条）",
            "验货费用由谁承担？（合同法第157、158条）",
            "在什么情况下可以解除合同？（合同法第93至97条）",
            "如何确定赔偿金额？（合同法第113条）",
            "受损方是否可以主张间接损失？（合同法第114条）",
            "瑕疵履行如何处理？（合同法第111条）",
            "违约金和定金可以同时适用吗？（合同法第114、115、116条）",
            "约定的违约金低于或高于损失的如何处理？（合同法第114条）",
            "谁来进行减损？（合同法第119条）",
            "未进行减损的后果？（合同法第119条）",
            "谁承担减损的费用？（合同法第119条）",
            "买卖双方均有过错造成违约的如何处理？（合同法第120条）",
            "标的物孳息应该归谁？（合同法第163条）",
            "货物交付的风险何时转移？（合同法第144、145、146条）",
            "第三方过失造成违约如何处理？（合同法第121条）"
        };
        addNumbering(conflictList, 0, 1);
        
        addTitleTextParagraph("（五）被申请人是否存在违约责任（仲裁庭根据案情撰写）", 0);
        addNumbering(emptyList, 0, 1);
        
        addTitleTextParagraph("（六）申请人是否存在过错（仲裁庭根据案情撰写）", 0);
        addNumbering(emptyList, 0, 1);
        
        addTitleTextParagraph("（七）关于申请人提出的请求", 0);
        String requestList[] = {
            "解除合同（合同法第93条至97条）",
            "实际履行 （合同法第8条）",
            "强制履行（合同法第107条、第110条）",
            "违约责任（合同法第7章）", 
            "损失赔偿及范围（合同法第113条）",
            "损害赔偿（合同法第122条）",
            "违约金（合同法第114条）",
            "定金（合同法第115条）",
            "违约金与定金的选择（合同法第116条）",
            "偿还借款（合同法第206条）", 
            "利率及利息支付（合同法第204条、第205条、最新民间借贷司法解释第25条至第32条）",
            "直接损失和间接损失 （合同法第113条）",
            "金钱债务（合同法第109条）",
            "非金钱债务（合同法第110条）",
            "不可抗力（合同法第117条）",
            "律师费、公证费、保全费、检验费、差旅费、证人费及仲裁费（深圳国际中仲裁院《仲裁规则》第62条）"
        };
        addNumbering(requestList, 0, 1);
        
        addTitleTextParagraph("（八）关于被申请人的仲裁反请求（如有反请求）", 0);
        addNumbering(emptyList, 0, 1);
        
        addTitleTextParagraph("（九）被申请人缺席的后果（如果缺席审理）", 0);
        addNormalTextParagraph("被申请人经合法通知无正当理由未到庭，亦未提交任何书面答辩意见"
                + "或证据，视为自行放弃抗辩的权利，应自行承担由此引起的法律后果。", 1);
        
        addTitleTextParagraph("（十）关于公证费、差旅费、检验费、律师费及仲裁费的问题", 0);
        addNormalTextParagraph("申请人请求仲裁庭裁决被申请人赔偿申请人因本案而产生的公证费、"
                + "差旅费、检验费、律师费及仲裁费等合理费用。仲裁庭认为，申请人提交了上述费用"
                + "的付款凭证，以证明其实际支出，该等费用均为申请人为保护其合法权益所产生的"
                + "费用，故仲裁庭予以支持。", 1);
        addNormalTextParagraph("根据《仲裁规则》第六十二条的规定，本案仲裁费、仲裁员实际开支"
                + "费用应由被申请人承担。", 1);
    }
    
    private void generateArbitration(){
        addSubTitle("三、裁    决");
        addNormalTextParagraph("根据上述事实和仲裁庭意见，仲裁庭对本案作出裁决如下：", 0);
        addNormalTextParagraph("（一）被申请人应向申请人支付尚欠借款人民币               元。", 0);
        addNormalTextParagraph("（二）被申请人应向申请人支付利息人民币               元。", 0);
        addNormalTextParagraph("（三）被申请人应向申请人支付律师费人民币               元。", 0);
        addNormalTextParagraph("（四）本案仲裁费人民币              元，由被申请人承担/"
                + "由申请人承担       %，即人民币               元，被申请人承担      %，"
                + "即人民币               元。申请人已足额预缴的人民币              元，"
                + "抵作本案仲裁费不予退回，被申请人应直接向申请人支付人民币               元。", 0);
        addNormalTextParagraph("本案仲裁员实际开支人民币               元，由被申请人"
                + "承担/由申请人承担       %，即人民币               元，被申请人承担"
                + "       %，即人民币               元。申请人预缴的人民币"
                + "               元，与其应承担的仲裁员实际开支相抵后，余款人民币元由"
                + "仲裁院退还给申请人；被申请人预缴的人民币               元，"
                + "与其应承担的仲裁员实际开支相抵后，余款人民币元由仲裁院退还给被申请人。", 0);
        addNormalTextParagraph("（五）驳回申请人的其他仲裁请求。", 0);
        addNormalTextParagraph("（六）驳回被申请人的仲裁反请求/其他仲裁反请求。", 1);
        addNormalTextParagraph("以上确定的各项应付款项/应履行义务，被申请人应在本裁决作出"
                + "之日起（      ）日内支付完毕/履行完毕。", 1);
        addNormalTextParagraph("本裁决为终局裁决，自作出之日起生效。", 0);
        addNormalTextParagraph("（紧接下一页）", 0);
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
        XWPFTable proposerTable = awardDoc.createTable(5, 2);
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
        int rowNumber = 0;
        setTableRowContent(proposerTable.getRow(rowNumber++), proKey, pro.getProposer());
        setTableRowContent(proposerTable.getRow(rowNumber++), "地      址：", pro.getAddress());
        setTableRowContent(proposerTable.getRow(rowNumber++), "统一社会信用代码：", "");
        setTableRowContent(proposerTable.getRow(rowNumber++), "法定代表人：", pro.getRepresentative());
        setTableRowContent(proposerTable.getRow(rowNumber++), "代  理  人：", pro.getAgency());
    }

    private void addRespondentTable(ArbitrationRespondent res, int countRes, int totalCount) {
        XWPFTable respondentTable = awardDoc.createTable(5, 2);
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
        int rowNumber = 0;
        setTableRowContent(respondentTable.getRow(rowNumber++), resKey, res.getRespondent());
        setTableRowContent(respondentTable.getRow(rowNumber++), "地      址：", res.getAddress());
        setTableRowContent(respondentTable.getRow(rowNumber++), "统一社会信用代码：", "");
        setTableRowContent(respondentTable.getRow(rowNumber++), "法定代表人：", res.getRepresentative());
        setTableRowContent(respondentTable.getRow(rowNumber++), "代  理  人：", res.getAgency());
    }
}
