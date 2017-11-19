/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.entities;

/**
 *
 * @author 陈光曦
 */
public class Routine {

    private String awardDate;
    private String routineText;

    public Routine(String awardDate, String routineText) {
        this.awardDate = awardDate;
        this.routineText = routineText;
    }

    public String getAwardDate() {
        return awardDate;
    }

    public String getRoutineText() {
        return routineText;
    }

}
