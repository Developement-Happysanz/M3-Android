package com.happysanztech.mmm.bean.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Admin on 22-01-2018.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;

    public static final String TAG = "SQLiteHelper.java";

    private static final String DATABASE_NAME = "MMM.db";
    private static final int DATABASE_VERSION = 9;

    private static final String table_create_current_best_location = "Create table IF NOT EXISTS currentBestLocation(_id integer primary key autoincrement,"
            + "latitude text,"
            + "longitude text,"
            + "status text);";

    private static final String table_store_trades = "Create table IF NOT EXISTS storeTradeData(_id integer primary key autoincrement,"
            + "trade_id text,"
            + "trade_name text);";

    private static final String table_create_previous_best_location = "Create table IF NOT EXISTS previousBestLocation(_id integer primary key autoincrement,"
            + "latitude text,"
            + "longitude text,"
            + "status text);";

    private static final String table_create_store_location_data = "Create table IF NOT EXISTS storeLocationData(_id integer primary key autoincrement,"
            + "user_id text,"
            + "lat text,"
            + "lon text,"
            + "location text,"
            + "dateandtime text,"
            + "distance text,"
            + "pia_id text,"
            + "gps_status text,"
            + "server_id text,"
            + "sync_status text);";

    private static final String table_create_prospect_data = "Create table IF NOT EXISTS storeProspectData(_id integer primary key autoincrement,"
            + "aadhar_status text,"
            + "aadhar_number text,"
            + "prospect_name text,"
            + "prospect_gender text,"
            + "prospect_dob text,"
            + "prospect_age text,"
            + "prospect_nationality text,"
            + "prospect_religion text,"
            + "prospect_community_class text,"
            + "prospect_community text,"
            + "prospect_father_name text,"
            + "prospect_mother_name text,"
            + "prospect_mobile text,"
            + "prospect_sec_mobile text,"
            + "prospect_email text,"
            + "prospect_state text,"
            + "prospect_city text,"
            + "prospect_address text,"
            + "prospect_mother_tongue text,"
            + "prospect_disability text,"
            + "prospect_blood_group text,"
            + "prospect_admission_date text,"
            + "prospect_admission_location text,"
            + "prospect_admission_lat text,"
            + "prospect_admission_lon text,"
            + "prefered_trade text,"
            + "prospect_institute text,"
            + "prospect_qualification text,"
            + "prospect_qualified_promotion text,"
            + "created_by text,"
            + "created_at text,"
            + "pia_id text,"
            + "prospect_image text,"
            + "server_id text,"
            + "sync_status text);";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //Current best location
        db.execSQL(table_create_current_best_location);
        //Previous best location
        db.execSQL(table_create_previous_best_location);
        //Store location details
        db.execSQL(table_create_store_location_data);
        //Store prospect details
        db.execSQL(table_create_prospect_data);
        //Store trade details
        db.execSQL(table_store_trades);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        //Current best location
        db.execSQL("DROP TABLE IF EXISTS currentBestLocation");
        //Previous best location
        db.execSQL("DROP TABLE IF EXISTS previousBestLocation");
        //Store location data
        db.execSQL("DROP TABLE IF EXISTS storeLocationData");
        //Store prospect data
        db.execSQL("DROP TABLE IF EXISTS storeProspectData");
        //Store trade data
        db.execSQL("DROP TABLE IF EXISTS storeTradeData");
    }

    public void open() throws SQLException {
        db = this.getWritableDatabase();
    }

    /*
     *   Current location Info Data Store and Retrieve Functionality
     */
    public long current_best_location_insert(String val1, String val2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put("latitude", val1);
        initialValues.put("longitude", val2);
        initialValues.put("status", "y");
        long l = db.insert("currentBestLocation", null, initialValues);
        db.close();
        return l;
    }

    public Cursor getCurrentBestLocationTopValue() throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        String fetch = "SELECT * FROM currentBestLocation LIMIT 1;";
        Cursor c = db.rawQuery(fetch, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public void deleteAllCurrentBestLocation() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("currentBestLocation", null, null);
    }
    /*
     *   End
     */

    /*
     *   Previous location Info Data Store and Retrieve Functionality
     */
    public long previous_best_location_insert(String val1, String val2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put("latitude", val1);
        initialValues.put("longitude", val2);
        initialValues.put("status", "y");
        long l = db.insert("previousBestLocation", null, initialValues);
        db.close();
        return l;
    }

    public Cursor getPreviousBestLocationTopValue() throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        String fetch = "SELECT * FROM previousBestLocation LIMIT 1;";
        Cursor c = db.rawQuery(fetch, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public void deleteAllPreviousBestLocation() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("previousBestLocation", null, null);
    }
    /*
     *   End
     */

    /*
     *   Store location data functionality
     */
    public long store_location_data_insert(String val1, String val2, String val3, String val4, String val5, String val6, String val7, String val8) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put("user_id", val1);
        initialValues.put("lat", val2);
        initialValues.put("lon", val3);
        initialValues.put("location", val4);
        initialValues.put("dateandtime", val5);
        initialValues.put("distance", val6);
        initialValues.put("pia_id", val7);
        initialValues.put("gps_status", val8);
        initialValues.put("server_id", "");
        initialValues.put("sync_status", "N");
        long l = db.insert("storeLocationData", null, initialValues);
        db.close();
        return l;
    }

    public String isRecordSynced() {
        String checkFlag = "0";
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "Select count(*) from storeLocationData where sync_status = 'N'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                checkFlag = cursor.getString(0);
            } while (cursor.moveToNext());
        }
//        if(cursor != null)
        cursor.close();
        return checkFlag;
    }

    public Cursor getStoredLocationData() throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        String fetch = "SELECT * FROM storeLocationData WHERE sync_status = 'N' ORDER BY _id LIMIT 1;";
        Cursor c = db.rawQuery(fetch, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public void updateLocationSyncStatus(String val1) {
        SQLiteDatabase sqdb = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sync_status", "S");
        System.out.print(val1);
        sqdb.update("storeLocationData", values, "_id=" + val1, null);
    }

    public void deleteAllStoredLocationData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("storeLocationData", null, null);
    }
    /*
     *   End
     */

    /*
     *   Store prospect data functionality
     */
    public long store_prospect_data_insert(String val1, String val2, String val3, String val4, String val5, String val6, String val7, String val8, String val9,
                                           String val10, String val11, String val12, String val13, String val14, String val15, String val16, String val17, String val18, String val19,
                                           String val20, String val21, String val22, String val23, String val24, String val25, String val26, String val27, String val28, String val29,
                                           String val30, String val31, String val32, String val33, String val34, String val35, String val36, String val37, String val38, String val39,
                                           String val40, String val41, String val42, String val43, String val44, String val45, String val46) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put("aadhar_status", val1);
        initialValues.put("aadhar_number", val2);
        initialValues.put("prospect_name", val3);
        initialValues.put("prospect_gender", val4);
        initialValues.put("prospect_dob", val5);
        initialValues.put("prospect_age", val6);
        initialValues.put("prospect_nationality", val7);
        initialValues.put("prospect_religion", val8);
        initialValues.put("prospect_community_class", val9);
        initialValues.put("prospect_community", val10);
        initialValues.put("prospect_father_name", val11);
        initialValues.put("prospect_mother_name", val12);
        initialValues.put("prospect_mobile", val13);
        initialValues.put("prospect_sec_mobile", val14);
        initialValues.put("prospect_email", val15);
        initialValues.put("prospect_state", val16);
        initialValues.put("prospect_city", val17);
        initialValues.put("prospect_address", val18);
        initialValues.put("prospect_mother_tongue", val19);
        initialValues.put("prospect_disability", val20);
        initialValues.put("prospect_blood_group", val21);
        initialValues.put("prospect_admission_date", val22);
        initialValues.put("prospect_admission_location", val23);
        initialValues.put("prospect_admission_lat", val24);
        initialValues.put("prospect_admission_lon", val25);
        initialValues.put("prefered_trade", val26);
        initialValues.put("prospect_institute", val27);
        initialValues.put("prospect_qualification", val28);
        initialValues.put("prospect_qualified_promotion", val29);
        initialValues.put("created_by", val30);
        initialValues.put("created_at", val31);
        initialValues.put("pia_id", val32);
        initialValues.put("prospect_image", val33);
        initialValues.put("qualification_details", val34);
        initialValues.put("year_of_edu", val35);
        initialValues.put("year_of_pass", val36);
        initialValues.put("identification_mark_one", val37);
        initialValues.put("identification_mark_two", val38);
        initialValues.put("languages_know", val39);
        initialValues.put("mother_mobile", val40);
        initialValues.put("father_mobile", val41);
        initialValues.put("head_of_family", val42);
        initialValues.put("head_of_family_edu", val43);
        initialValues.put("family_members", val44);
        initialValues.put("yearly_income", val45);
        initialValues.put("job_card", val46);
        initialValues.put("server_id", "");
        initialValues.put("sync_status", "N");


        String CandidatesJobCard = "";
        long l = db.insert("storeProspectData", null, initialValues);
        db.close();
        return l;
    }

    public String isProspectRecordSynced() {
        String checkFlag = "0";
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "Select count(*) from storeProspectData where sync_status = 'N'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                checkFlag = cursor.getString(0);
            } while (cursor.moveToNext());
        }
//        if(cursor != null)
        cursor.close();
        return checkFlag;
    }

    public Cursor getStoredProspectData() throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        String fetch = "SELECT * FROM storeProspectData WHERE sync_status = 'N' ORDER BY _id LIMIT 1;";
        Cursor c = db.rawQuery(fetch, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public void updateProspectSyncStatus(String val1) {
        SQLiteDatabase sqdb = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sync_status", "S");
        System.out.print(val1);
        sqdb.update("storeProspectData", values, "_id=" + val1, null);
    }

    public void deleteAllStoredProspectData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("storeProspectData", null, null);
    }
    /*
     *   End
     */


    /*
     *   Store trade data functionality
     */
    public long store_trade_data_insert(String val1, String val2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put("trade_id", val1);
        initialValues.put("trade_name", val2);
        long l = db.insert("storeTradeData", null, initialValues);
        db.close();
        return l;
    }

    public Cursor getStoredTradeData() throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        String fetch = "SELECT * FROM storeTradeData ;";
        Cursor c = db.rawQuery(fetch, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public void deleteAllStoredTradeData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("storeTradeData", null, null);
    }
    /*
     *   End
     */
}
