package com.bitmo.bitmo;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Mateo on 4/29/18.
 */

public class FirebaseManager {
    private static FirebaseManager myfb;
    private SharedPreferences appData;
    private SharedPreferences.Editor appEditor;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String myPk;
    private String myUname;


    private FirebaseManager(Context context) {
        appData = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        appEditor = appData.edit();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        myPk = appData.getString("pk", "0");
        myUname = appData.getString("uname", "@Frank");
    }

    public static FirebaseManager setupInstance(Context context){
        if (myfb == null){ //if there is no instance available... create new one
            myfb = new FirebaseManager(context);
        }
        return myfb;
    }

    public static FirebaseManager getInstance() {
        if (myfb == null){ //if there is no instance available... create new one
            return null;
        }
        return myfb;
    }

    public DatabaseReference getRef(String ref) {
        return database.getReference(ref);
    }

    public DatabaseReference getRef() {
        return myRef;
    }

    public void addUser(UserEntry u) {
        myRef.child(u.getUname()).setValue(u);
        myPk = u.getPK();
        myUname = u.getUname();
    }

    public void addContact(String pk, String uname) {
        myRef.child(this.myPk).child("contacts").child(pk).setValue(uname);
    }

    public void addRequest(String to, String amtString, String msg) {
        // TODO Set otherUname to other user's username
        String otherUname = to;
        this.myUname = appData.getString("uname", "@Frank");
        String timeStamp = Long.toString(System.currentTimeMillis());
        RequestEntry putInDest = new RequestEntry(this.myPk, this.myUname, amtString, msg,
                false, false, true, timeStamp);
        RequestEntry putInMine = new RequestEntry(to, otherUname, amtString, msg,
                false, false, false, timeStamp);

        /*myRef.child(to).child("requests").child(this.myPk).setValue(putInDest);
        myRef.child(this.myPk).child("requests").child(to).setValue(putInMine);*/
        myRef.child(to).child("requests").child(putInDest.getTimeStamp()).setValue(putInDest);
        myRef.child(this.myPk).child("requests").child(putInMine.getTimeStamp()).setValue(putInMine);
    }

    public void updateUserField(String field, String newVal) {
        //if (field.equals("uname")) {
        //    this.myUname = newVal;
        //}
        myRef.child(this.myPk).child(field).setValue(newVal);
    }

    public void fulfillRequest(String from, String timeStamp) {
        /*myRef.child(from).child("requests").child(this.myPk).removeValue();
        myRef.child(this.myPk).child("requests").child(from).removeValue();*/
        myRef.child(from).child("requests").child(timeStamp).removeValue();
        myRef.child(this.myPk).child("requests").child(timeStamp).removeValue();
    }

    public void cancelRequest(String otherPk, String timeStamp) {
        /*myRef.child(otherPk).child("requests").child(this.myPk).removeValue();
        myRef.child(this.myPk).child("requests").child(otherPk).removeValue();*/
        myRef.child(otherPk).child("requests").child(timeStamp).removeValue();
        myRef.child(this.myPk).child("requests").child(timeStamp).removeValue();
    }

    public void remind(RequestEntry req) {
        //myRef.child(req.getPk()).child("requests").child(this.myPk).child("remind").setValue(true);
        myRef.child(req.getPk()).child("requests").child(req.getTimeStamp()).child("remind").setValue(true);

    }

    public void payUser(String pk, String amt, String msg) {
        String timeStamp = Long.toString(System.currentTimeMillis());
        PayEntry p = new PayEntry(this.myPk, this.myUname, amt, msg, true, timeStamp);
        myRef.child(pk).child("pay_queue").child(timeStamp).setValue(p);
        myRef.child(myPk).child("history").child(timeStamp).setValue(new PayEntry(pk, pk, amt, msg, false, timeStamp));
        float totalBTC = appData.getFloat("Total_BTC", 0.0f);
        totalBTC -= Float.parseFloat(amt);
        appEditor.putFloat("Total_BTC", totalBTC);
        appEditor.commit();
        myfb.setBTC(totalBTC);
    }

    public String getMyUname() {
        this.myUname = appData.getString("uname", "@Frank");
        return this.myUname;
    }

    public void changeUser(String pk, String uname) {
        this.myPk = pk;
        this.myUname = uname;
    }

    public void setBTC(float amt) {
        myRef.child(myPk).child("totalBTC").setValue(Float.toString(amt));
    }

    public void pkToUser(String pk) {

    }

    public void addHistory(PayEntry p) {
        myRef.child(myPk).child("pay_queue").child(p.getTimeStamp()).removeValue();
        myRef.child(myPk).child("history").child(p.getTimeStamp()).setValue(p);
    }

}
