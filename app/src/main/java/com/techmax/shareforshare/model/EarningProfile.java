package com.techmax.shareforshare.model;

/**
 * Created by ABC on 8/22/2017.
 */

public class EarningProfile {

    private String key;
    private String eun;
    private String eui;
    private String ew;
    private double et;
    private double etr;
    private double ec;
    private double er;
    private double ecr;
    private long elt;
    private int eclk;
    private int eic;
    private int eeb;
    public int erc;


    public EarningProfile() {
    }

    public EarningProfile(String eun, String eui, double et,double etr, double ec, double er, double ecr,long elt,int eclk,int erc, int eic,int eeb, String ew) {
        this.eun = eun;
        this.eui = eui;
        this.et = et;
        this.etr=etr;
        this.ec = ec;
        this.er = er;
        this.ecr = ecr;
        this.eclk=eclk;
        this.erc=erc;
        this.eic=eic;
        this.eeb=eeb;
        this.ew=ew;
        this.elt=elt;
    }



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEun() {
        return eun;
    }

    public void setEun(String eun) {
        this.eun = eun;
    }

    public String getEui() {
        return eui;
    }

    public void setEui(String eui) {
        this.eui = eui;
    }

    public double getEt() {
        return et;
    }

    public void setEt(double et) {
        this.et = et;
    }

    public double getEc() {
        return ec;
    }

    public void setEc(double ec) {
        this.ec = ec;
    }

    public double getEr() {
        return er;
    }

    public void setEr(double er) {
        this.er = er;
    }

    public double getEcr() {
        return ecr;
    }

    public void setEcr(double ecr) {
        this.ecr = ecr;
    }

    public int getEclk() {
        return eclk;
    }

    public void setEclk(int eclk) {
        this.eclk = eclk;
    }

    public String getEw() {
        return ew;
    }

    public void setEw(String ew) {
        this.ew = ew;
    }

    public double getEtr() {
        return etr;
    }

    public void setEtr(double etr) {
        this.etr = etr;
    }

    public int getErc() {
        return erc;
    }

    public void setErc(int erc) {
        this.erc = erc;
    }

    public long getElt() {
        return elt;
    }

    public void setElt(long elt) {
        this.elt = elt;
    }

    public int getEic() {
        return eic;
    }

    public void setEic(int eic) {
        this.eic = eic;
    }

    public int getEeb() {
        return eeb;
    }

    public void setEeb(int eeb) {
        this.eeb = eeb;
    }
}
