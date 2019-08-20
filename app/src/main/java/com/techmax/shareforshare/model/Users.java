package com.techmax.shareforshare.model;

/**
 * Created by ABC on 8/3/2017.
 */

public class Users {
    private String un;
    private String ue;
    private String ui;
    private long uc;
    private long ul;
    private int us;
    private String ur;

    public Users() {
    }

    public Users(String un, String ue, String ui, long uc, long ul, int us, String ur) {
        this.un = un;
        this.ue = ue;
        this.ui = ui;
        this.uc = uc;
        this.ul = ul;
        this.us = us;
        this.ur=ur;
    }



    public String getUn() {
        return un;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public String getUe() {
        return ue;
    }

    public void setUe(String ue) {
        this.ue = ue;
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String ui) {
        this.ui = ui;
    }

    public long getUc() {
        return uc;
    }

    public void setUc(long uc) {
        this.uc = uc;
    }

    public long getUl() {
        return ul;
    }

    public void setUl(long ul) {
        this.ul = ul;
    }

    public int getUs() {
        return us;
    }

    public void setUs(int us) {
        this.us = us;
    }

    public String getUr() {
        return ur;
    }

    public void setUr(String ur) {
        this.ur = ur;
    }
}
