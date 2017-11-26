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
public class ArbitrationProposer {

    private String proposer;
    private String agency;
    private String representative;
    private String address;

    public ArbitrationProposer() {
    }

    public ArbitrationProposer(String proposer) {
        this.proposer = proposer;
    }

    public ArbitrationProposer(String proposer, String agency, String representative, String address) {
        this.proposer = proposer;
        this.agency = agency;
        this.representative = representative;
        this.address = address;
    }

    public String getProposer() {
        return proposer;
    }

    public void setProposer(String proposer) {
        this.proposer = proposer;
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
}
