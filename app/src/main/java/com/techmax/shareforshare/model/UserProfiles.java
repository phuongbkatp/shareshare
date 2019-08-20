package com.techmax.shareforshare.model;

/**
 * Created by ABC on 9/2/2017.
 */

public class UserProfiles {

    private String udn;
    private boolean uds;
    private String utok;
    private String udi;
    private long udl;
    private String udc;
    private int av;

    public UserProfiles() {
    }

    public UserProfiles(String udn, boolean uds) {
        this.udn = udn;
        this.uds = uds;
    }

    public String getUdn() {
        return udn;
    }

    public void setUdn(String udn) {
        this.udn = udn;
    }

    public boolean isUds() {
        return uds;
    }

    public void setUds(boolean uds) {
        this.uds = uds;
    }

    public String getUtok() {
        return utok;
    }

    public void setUtok(String utok) {
        this.utok = utok;
    }

    public String getUdi() {
        return udi;
    }

    public void setUdi(String udi) {
        this.udi = udi;
    }

    public long getUdl() {
        return udl;
    }

    public void setUdl(long udl) {
        this.udl = udl;
    }

    public String getUdc() {
        return udc;
    }

    public void setUdc(String udc) {
        this.udc = udc;
    }

    public int getAv() {
        return av;
    }

    public void setAv(int av) {
        this.av = av;
    }
}
