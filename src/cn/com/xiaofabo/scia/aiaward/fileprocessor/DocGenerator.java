/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.fileprocessor;

import static cn.com.xiaofabo.scia.aiaward.fileprocessor.AwardDocGenerator.logger;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author 陈光曦
 */
public abstract class DocGenerator implements OutputGenerator {

    public static final Logger logger = Logger.getLogger(DocGenerator.class.getName());

    public static final BigInteger PAGE_A4_WIDTH = BigInteger.valueOf(11900L);
    public static final BigInteger PAGE_A4_HEIGHT = BigInteger.valueOf(16840L);

    public static final int CN_FONT_SIZE_XIAO_YI = 24;
    public static final int CN_FONT_SIZE_ER = 22;
    public static final int CN_FONT_SIZE_XIAO_ER = 18;
    public static final int CN_FONT_SIZE_SAN = 16;
    public static final int CN_FONT_SIZE_XIAO_SAN = 15;
    public static final int CN_FONT_SIZE_SI = 14;
    public static final int CN_FONT_SIZE_XIAO_SI = 12;
//    public static final int CN_FONT_SIZE_WU = 10.5;
    public static final int CN_FONT_SIZE_XIAO_WU = 9;

    public static final String FONT_FAMILY_TIME_NEW_ROMAN = "Times New Roman";
    public static final String FONT_FAMILY_SONG = "宋体";
    public static final String FONT_FAMILY_FANGSONG = "仿宋_GB2312";
    public static final String FONT_FAMILY_HEITI = "黑体";
    public static final String FONT_FAMILY_KAITI = "楷体";

    private List<String> errorToUser;
    private List<String> warningToUser;

    public DocGenerator() {
        PropertyConfigurator.configure("log/config.txt");
        errorToUser = new LinkedList<>();
        warningToUser = new LinkedList<>();
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

    public String cnDateGenerator() {
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

    public String numberToCN(char number) {
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

    public String findAndCorrectMoneyFormats(String str) {
        String toReturn = "";
        Pattern pattern = Pattern.compile(
                "(人民币)?[0-9.,，]+(万)?(亿)?元"
                + "|" + "[0-9.,，]+(万)?(亿)?美元"
                + "|" + "[0-9.,，]+(万)?(亿)?美金"
                + "|" + "[0-9.,，]+(万)?(亿)?欧元");
        Matcher matcher = pattern.matcher(str);

        List<AwardDocGenerator.MoneyString> moneyStrList = new LinkedList<>();
        while (matcher.find()) {
            String match = matcher.group();
            int start = matcher.start();
            int end = matcher.end();
            while (match.startsWith("，")) {
                match = match.substring(1);
                ++start;
            }
            moneyStrList.add(new AwardDocGenerator.MoneyString(start, end, match));
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

    public String replaceStr(String baseStr, int startIdx, int endIdx, String str) {
        String firstPart = baseStr.substring(0, startIdx);
        String secondPart = baseStr.substring(endIdx);
        return firstPart + str + secondPart;
    }

    protected class MoneyString {

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

    public String correctNumberFormat(String s) {
        s = removeAllCommas(s);
        int backIdx = s.contains(".") ? s.indexOf(".") - 1 : s.length() - 1;
        backIdx -= 3;
        while (backIdx >= 0) {
            s = replaceStr(s, backIdx + 1, backIdx + 1, ",");
            backIdx -= 3;
        }
        return s;
    }

    public String removeAllCommas(String str) {
        return str.replaceAll("[,，]", "");
    }
}
