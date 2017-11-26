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
public class ArbitrationApplication {

    private int id;
    private String title;
    private String gist;
    private String request;
    private String factAndReason;

    public ArbitrationApplication(int id) {
        this.id = id;
    }

    public ArbitrationApplication(int id, String title) {
        this.id = id;
        this.title = title;
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
