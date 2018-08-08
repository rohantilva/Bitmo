package com.bitmo.bitmo;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by Mateo on 4/29/18.
 */

public class RequestEntry {
    private String pk;
    private String uname;
    private String msg;
    private String amt;
    private boolean remind;
    private boolean fulfilled;
    private boolean isFromSomeoneElse;
    private String timeStamp;

    public RequestEntry() {}

    public RequestEntry(String pk, String uname, String amt, String msg,
                        boolean remind, boolean fulfilled, boolean isFromSomeoneElse, String timeStamp) {
        this.pk = pk;
        this.uname = uname;
        this.msg = msg;
        this.amt = amt;
        this.remind = remind;
        this.fulfilled = fulfilled;
        this.isFromSomeoneElse = isFromSomeoneElse;
        this.timeStamp = timeStamp;
    }

    public String getPk() {
        return this.pk;
    }

    public String getUname() {
        return this.uname;
    }

    public String getMsg() {
        return this.msg;
    }

    public String getAmt() {
        return this.amt;
    }

    public boolean getRemind() {
        return this.remind;
    }

    public boolean getFulfilled() {
        return this.fulfilled;
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

    public void setUname(String uname) {
        this.uname = uname;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public void setRemind(boolean remind) {
        this.remind = remind;
    }

    public void setFulfilled(boolean fulfilled) {
        this.fulfilled = fulfilled;
    }

    public void setIsFromSomeoneElse(boolean isFromSomeoneElse) {
        this.isFromSomeoneElse = isFromSomeoneElse;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

}
