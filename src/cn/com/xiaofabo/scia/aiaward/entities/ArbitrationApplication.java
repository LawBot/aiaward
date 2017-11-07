/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.entities;

import java.util.List;

/**
 *
 * @author gchen
 */
public class ArbitrationApplication {

    private int id;
    private String title;
    private List proposerList;
    private List respondentList;

    public ArbitrationApplication(int id) {
        this.id = id;
    }

    public ArbitrationApplication(int id, String title, List proposerList, List respondentList) {
        this.id = id;
        this.title = title;
        this.proposerList = proposerList;
        this.respondentList = respondentList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List getProposerList() {
        return proposerList;
    }

    public void setProposerList(List proposerList) {
        this.proposerList = proposerList;
    }

    public List getRespondentList() {
        return respondentList;
    }

    public void setRespondentList(List respondentList) {
        this.respondentList = respondentList;
    }

}
