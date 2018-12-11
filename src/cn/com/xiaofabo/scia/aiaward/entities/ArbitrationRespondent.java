/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.xiaofabo.scia.aiaward.entities;

/**
 *
 * @author gchen
 */
public class ArbitrationRespondent {

    private String respondent;
    private String id;
    private String agency;
    private String representative;
    private String address;

    /// Either COM (company) or IND (individual)
    /// this is determined by the id type
    private String type;

    public ArbitrationRespondent() {
    }

    public ArbitrationRespondent(String respondent, String id, String agency, String representative, String address) {
        this.respondent = respondent;
        this.id = id;
        this.agency = agency;
        this.representative = representative;
        this.address = address;
    }

    public String getRespondent() {
        return respondent;
    }

    public void setRespondent(String respondent) {
        this.respondent = respondent;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getRepresentative() {
        return representative;
    }

    public void setRepresentative(String representative) {
        this.representative = representative;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
