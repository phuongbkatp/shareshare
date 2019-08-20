package com.techmax.shareforshare.model;

/**
 * Created by ABC on 9/12/2017.
 */

public class Payout {

    private String pw;
    private String key="";
    private double pa;
    private double pc;
    private double pr;
    private int pclk;
    private long pt;
    private int ps;
    private String psm;

    public Payout() {
    }

    public Payout(String pw, double pa, double pc, double pr, int pclk, long pt, int ps, String psm) {
        this.pw = pw;
        this.pa = pa;
        this.pc = pc;
        this.pr = pr;
        this.pclk = pclk;
        this.pt = pt;
        this.ps = ps;
        this.psm = psm;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public double getPa() {
        return pa;
    }

    public void setPa(double pa) {
        this.pa = pa;
    }

    public double getPc() {
        return pc;
    }

    public void setPc(double pc) {
        this.pc = pc;
    }

    public double getPr() {
        return pr;
    }

    public void setPr(double pr) {
        this.pr = pr;
    }

    public int getPclk() {
        return pclk;
    }

    public void setPclk(int pclk) {
        this.pclk = pclk;
    }

    public long getPt() {
        return pt;
    }

    public void setPt(long pt) {
        this.pt = pt;
    }

    public int getPs() {
        return ps;
    }

    public void setPs(int ps) {
        this.ps = ps;
    }

    public String getPsm() {
        return psm;
    }

    public void setPsm(String psm) {
        this.psm = psm;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
