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
    private static final int DATABASE_VERSION = 1;

    private static final String table_create_current_best_location = "Create table IF NOT EXISTS currentBestLocation(_id integer primary key autoincrement,"
            + "latitude text,"
            + "longitude text,"
            + "status text);";

    private static final String table_create_previous_best_location = "Create table IF NOT EXISTS previousBestLocation(_id integer primary key autoincrement,"
            + "latitude text,"
            + "longitude text,"
            + "status text);";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //Current best location
        db.execSQL(table_create_current_best_location);
        //Previous best location
        db.execSQL(table_create_previous_best_location);
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
}
