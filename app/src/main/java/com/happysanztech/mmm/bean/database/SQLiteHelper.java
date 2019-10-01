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
    private static final int DATABASE_VERSION = 2;

    private static final String table_create_current_best_location = "Create table IF NOT EXISTS currentBestLocation(_id integer primary key autoincrement,"
            + "latitude text,"
            + "longitude text,"
            + "status text);";

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
}
