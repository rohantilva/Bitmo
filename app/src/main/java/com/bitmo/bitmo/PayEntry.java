package com.bitmo.bitmo;

/**
 * Created by Mateo on 4/29/18.
 */

public class PayEntry {
    private String pk;
    private String amt;
    private String msg;
    private boolean isFromSomeoneElse;
    private String timeStamp;
    private String uname;

    public PayEntry() {}

    public PayEntry(String pk, String uname, String amt, String msg, boolean isFromSomeoneElse, String timeStamp) {
        this.pk = pk;
        this.uname = uname;
        this.amt = amt;
        this.msg = msg;
        this.isFromSomeoneElse = isFromSomeoneElse;
        this.timeStamp = timeStamp;
    }

    public String getPk() {
        return this.pk;
    }

    public String getUname() {
        return this.uname;
    }

    public String getAmt() {
        return this.amt;
    }

    public String getMsg() {
        return this.msg;
    }

    public boolean getIsFromSomeoneElse() {
        return this.isFromSomeoneElse;
    }

    public String getTimeStamp() {
        return this.timeStamp;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setIsFromSomeoneElse(boolean isFromSomeoneElse) {
        this.isFromSomeoneElse = isFromSomeoneElse;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }
}
