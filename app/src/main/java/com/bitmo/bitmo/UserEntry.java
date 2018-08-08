package com.bitmo.bitmo;

/**
 * Holds data for one item
 */
public class UserEntry {
    private String fname;
    private String lname;
    private String username;
    private String publicKey;
    private String secretKey;
    private String totalBTC;


    UserEntry() {
        this.username = "";
        this.fname = "";
        this.lname = "";
        this.totalBTC = "";
        this.publicKey = "";
        this.secretKey = "";
    }

    UserEntry(String unm, String fnm, String lnm, String btc, String pk, String sk) {
        this.username = unm;
        this.fname = fnm;
        this.lname = lnm;
        this.totalBTC = btc;
        this.publicKey = pk;
        this.secretKey = sk;
    }

    public String getPK() { return this.publicKey; }

    public String getSK() { return this.secretKey; }

    public String getUname() { return this.username; }

    public String getFName() { return this.fname; }

    public String getLName() { return this.lname; }

    public String getTotalBTC() {
        return this.totalBTC;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setUname(String uname) {
        this.username = uname;
    }

    public void setTotalBTC(String btc) {
        this.totalBTC = btc;
    }

    public void setPk(String pk) {
        this.publicKey = pk;
    }

    public void setSk(String sk) {
        this.secretKey = sk;
    }
}
