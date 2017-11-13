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
public class ArbitrationApplication {

    private int id;
    private String title;
    private List proposerList;
    private List respondentList;
    private String gist;
    private String request;
    private String factAndReason;

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

    public String getGist() {
        return gist;
    }

    public void setGist(String gist) {
        this.gist = gist;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getFactAndReason() {
        return factAndReason;
    }

    public void setFactAndReason(String factAndReason) {
        this.factAndReason = factAndReason;
    }

}
