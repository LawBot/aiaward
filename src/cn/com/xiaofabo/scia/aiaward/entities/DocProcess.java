/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.entities;

import java.util.List;

/**
 *
 * @author 陈光曦
 */
public class DocProcess {

    private final String inRoutineDocUrl;
    private final String inApplicationDocUrl;
    private final List inEvidenceDocUrlList;
    private final String inRespondDocUrl;
    private final String outAwardDocUrl;

    public DocProcess(String inRoutineDocUrl,
            String inApplicationDocUrl,
            List inEvidenceDocUrlList,
            String inRespondDocUrl,
            String outAwardDocUrl) {
        this.inRoutineDocUrl = inRoutineDocUrl;
        this.inApplicationDocUrl = inApplicationDocUrl;
        this.inEvidenceDocUrlList = inEvidenceDocUrlList;
        this.inRespondDocUrl = inRespondDocUrl;
        this.outAwardDocUrl = outAwardDocUrl;
    }

    public String getInApplicationDocUrl() {
        return inApplicationDocUrl;
    }

    public String getInRoutineDocUrl() {
        return inRoutineDocUrl;
    }

    public String getInRespondDocUrl() {
        return inRespondDocUrl;
    }

    public String getOutAwardDocUrl() {
        return outAwardDocUrl;
    }

    public List getInEvidenceDocUrlList() {
        return inEvidenceDocUrlList;
    }

}
