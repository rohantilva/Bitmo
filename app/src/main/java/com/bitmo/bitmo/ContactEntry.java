package com.bitmo.bitmo;

import android.support.annotation.NonNull;

/**
 * Created by RTilva on 5/1/18.
 */

public class ContactEntry implements Comparable<ContactEntry> {
    private String fname;
    private String lname;
    private String username;
    private String publicKey;


    ContactEntry() {
        this.username = "";
        this.fname = "";
        this.lname = "";
        this.publicKey = "";
    }

    ContactEntry(String fnm, String lnm, String unm, String pk) {
        this.username = unm;
        this.fname = fnm;
        this.lname = lnm;
        this.publicKey = pk;
    }

    public String getPK() { return this.publicKey; }

    public String getUname() { return this.username; }

    public String getFName() { return this.fname; }

    public String getLName() { return this.lname; }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setUname(String uname) {
        this.username = uname;
    }

    public void setPk(String pk) {
        this.publicKey = pk;
    }

    @Override
    public int compareTo(@NonNull ContactEntry contactEntry) {
        //need to figure out how to just compare only the public keys

        int lnameCompare = this.lname.compareTo(contactEntry.getLName());
        if (lnameCompare == 0) {
            return this.fname.compareTo(contactEntry.getFName());
        }
        else return lnameCompare;
    }
}
