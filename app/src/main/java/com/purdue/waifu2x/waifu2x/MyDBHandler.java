package com.purdue.waifu2x.waifu2x;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHandler extends SQLiteOpenHelper {
    //The methods used to access the SQLite Database

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "waifuDB.db";
    public static final String TABLE_waifu = "waifuImage";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_IMAGEPATHNAME = "imagepath";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory,
                       int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WAIFU_TABLE = "CREATE TABLE " + TABLE_waifu + "(" +
                COLUMN_ID + "INTEGER PRIMARY KEY," + COLUMN_IMAGEPATHNAME + " TEXT)";
        db.execSQL(CREATE_WAIFU_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_waifu);
        onCreate(db);
    }

    public void addImage(waifuImage wi) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, wi.get_id());
        values.put(COLUMN_IMAGEPATHNAME, wi.get_imagePath());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_waifu, null, values);
        db.close();
    }

    public String findImage(int id) {
        String query = "Select * From " + TABLE_waifu + " Where " + COLUMN_ID + " = " + id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        waifuImage wi = new waifuImage();

        if (c.moveToFirst()) {
            c.moveToFirst();
            wi.set_id(Integer.parseInt(c.getString(0)));
            wi.set_imagePath(c.getString(1));
            c.close();
        } else {
            wi = null;
        }
        db.close();
        return wi.get_imagePath();
    }

    public boolean deleteImage(int id) {
        boolean result = false;

        String query = "Select * From " + TABLE_waifu + " Where " + COLUMN_ID + " = " + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        waifuImage wi = new waifuImage();
        if (c.moveToFirst()) {
            wi.set_id(Integer.parseInt(c.getString(0)));
            db.delete(TABLE_waifu, COLUMN_ID + " = ?",
                    new String[] { String.valueOf(wi.get_id())} );
            c.close();
            result = true;
        }
        db.close();
        return result;
    }
}
