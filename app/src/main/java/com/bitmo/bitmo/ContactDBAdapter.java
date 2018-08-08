package com.bitmo.bitmo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by joanne. Thanks joanne!
 * Edited by Mateo Paredes
 */
public class ContactDBAdapter {

    private SQLiteDatabase db;
    private static ContactDBAdapter dbInstance = null;
    private MyDBhelper dbHelper;
    private final Context context;

    private static final String DB_NAME = "item.db";
    private static int dbVersion = 1;

    private static final String CONTACT_ID = "contact_id";
    private static final String CONTACTS_TABLE = "contacts";
    public static final String CONTACT_FIRST_NAME = "contact_first_name";   // column 0
    public static final String CONTACT_LAST_NAME = "contact_last_name";
    public static final String CONTACT_UNAME = "contact_unm";
    public static final String CONTACT_PK = "contact_pubkey";


    public static final String[] CONTACT_COLS = {CONTACT_ID, CONTACT_FIRST_NAME, CONTACT_LAST_NAME, CONTACT_UNAME, CONTACT_PK};

    public static synchronized ContactDBAdapter getInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new ContactDBAdapter(context.getApplicationContext());
        }
        return dbInstance;
    }

    private ContactDBAdapter(Context ctx) {
        context = ctx;
        dbHelper = new MyDBhelper(context, DB_NAME, null, dbVersion);
    }

    public void open() throws SQLiteException {
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = dbHelper.getReadableDatabase();
        }
    }


    // database update methods

    public long insertItem(ContactEntry it) {
        // Still need to figure out what I'm going to do logically to handle incomplete profiles




        ContentValues cvalues = new ContentValues();
        // assign values for each col
        cvalues.put(CONTACT_FIRST_NAME, it.getFName());
        cvalues.put(CONTACT_LAST_NAME, it.getLName());
        cvalues.put(CONTACT_UNAME, it.getUname());
        cvalues.put(CONTACT_PK, it.getPK());

        // add to course table in database
        long ret = db.insert(CONTACTS_TABLE, null, cvalues);
        return ret;
    }

    public boolean removeItem(long id) {
        return db.delete(CONTACTS_TABLE, "CONTACT_ID="+id, null) > 0;
    }

    public boolean updateField(long id, int field, String wh) {
        ContentValues cvalue = new ContentValues();
        cvalue.put(CONTACT_COLS[field], wh);
        return db.update(CONTACTS_TABLE, cvalue, CONTACT_ID +"="+id, null) > 0;
    }

    // database query methods
    public Cursor getAllItems() {
        Cursor cursor = db.query(CONTACTS_TABLE, CONTACT_COLS, null, null, null, null, null);
        //this.updateField(cursor.getPosition(), 2, "");
        return db.query(CONTACTS_TABLE, CONTACT_COLS, null, null, null, null, null);
    }

    /*public Cursor getAllRequests() {
        Cursor cursor = db.query(CONTACTS_TABLE, CONTACT_COLS, null, null, null, null, null);
        //this.updateField(cursor.getPosition(), 2, "");
        return db.query(ITEMS_TABLE, ITEM_COLS, CONTACT_TYPE + "='request_from' OR " + ITEM_TYPE + "='request_to'", null, null, null, null);
    }*/

    public ContactEntry getItem(long id) throws SQLException {
        Cursor cursor = db.query(true, CONTACTS_TABLE, CONTACT_COLS, CONTACT_ID +"="+id, null, null, null, null, null);
        if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
            System.out.println(cursor.getCount());
            throw new SQLException("No contacts found for row: " + id);
        }
        // must use column indices to get column values
        int index_first_name = cursor.getColumnIndex(CONTACT_FIRST_NAME);
        int index_last_name = cursor.getColumnIndex(CONTACT_LAST_NAME);
        int index_uname = cursor.getColumnIndex(CONTACT_UNAME);
        int index_pk = cursor.getColumnIndex(CONTACT_PK);


        return new ContactEntry(cursor.getString(index_first_name), cursor.getString(index_last_name), cursor.getString(index_uname),
                cursor.getString(index_pk));
    }

    public int getLatestId() {
        Cursor cursor = db.query(CONTACTS_TABLE, CONTACT_COLS, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isLast()) {
                cursor.moveToNext();
            }
            return cursor.getInt(0);
        } else {
            return -1;
        }
    }


    private static class MyDBhelper extends SQLiteOpenHelper {

        // SQL statement to create a new database
        private static final String DB_CREATE = "CREATE TABLE " + CONTACTS_TABLE
                + " (" + CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CONTACT_FIRST_NAME + " TEXT, " + CONTACT_LAST_NAME + " TEXT, " + CONTACT_UNAME + " TEXT, "
                + CONTACT_PK + " TEXT);";

        public MyDBhelper(Context context, String name, SQLiteDatabase.CursorFactory fct, int version) {
            super(context, name, fct, version);
        }

        @Override
        public void onCreate(SQLiteDatabase adb) {
            // TODO Auto-generated method stub
            adb.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase adb, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            Log.w("ItemDB", "upgrading from version " + oldVersion + " to "
                    + newVersion + ", destroying old data");
            // drop old table if it exists, create new one
            // better to migrate existing data into new table
            adb.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE);
            onCreate(adb);
        }
    } // DBhelper class

} // ProfileDBadapter class
