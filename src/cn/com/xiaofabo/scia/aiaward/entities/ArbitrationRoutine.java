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
public class ArbitrationRoutine {

    private final String awardDate;
    private final String routineText;

    private final List proposerList;
    private final List respondentList;

    public ArbitrationRoutine(List proposerList, List respondentList, String awardDate, String routineText) {
        this.proposerList = proposerList;
        this.respondentList = respondentList;
        this.awardDate = awardDate;
        this.routineText = routineText;
    }

    public String getAwardDate() {
        return awardDate;
    }

    public String getRoutineText() {
        return routineText;
    }

    public List getProposerList() {
        return proposerList;
    }

    public List getRespondentList() {
        return respondentList;
    }
}
