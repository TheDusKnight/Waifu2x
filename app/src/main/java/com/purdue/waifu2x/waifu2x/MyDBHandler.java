package com.purdue.waifu2x.waifu2x;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

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
                COLUMN_ID + " INTEGER," + COLUMN_IMAGEPATHNAME + " TEXT PRIMARY KEY)";
        db.execSQL(CREATE_WAIFU_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_waifu);
        onCreate(db);
    }

    public void addImage(waifuImage wi, int id) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, wi.get_id());
        values.put(COLUMN_IMAGEPATHNAME, wi.get_imagePath());

        SQLiteDatabase db = this.getWritableDatabase();
        //Making the newest entry the first row
        if (wi.get_id() == 1) {
            //Checking for and deleting the soon-to-be excess row and cache file
            String query2 = "Select * From " + TABLE_waifu + " Where " + COLUMN_ID + " = 16";
            Cursor c2 = db.rawQuery(query2, null);
            if (c2.getCount() > 0) {
                File file = new File(findImage(16).get_imagePath());
                boolean deleted = file.delete();
                deleteImage(16);
            }
            //Updating the ID of the other rows
            for (int i = 15; i > 0; i--) {
                ContentValues newValues = new ContentValues();
                newValues.put(COLUMN_ID, i + 1);

                String query = "Select * From " + TABLE_waifu + " Where " + COLUMN_ID + " = " + i;
                Cursor c = db.rawQuery(query, null);
                waifuImage oldwi = new waifuImage();

                if (c.moveToFirst()) {
                    oldwi.set_id(Integer.parseInt(c.getString(0)));
                    db.update(TABLE_waifu, newValues, COLUMN_ID + " = ?",
                            new String[] { String.valueOf(oldwi.get_id())} );
                    c.close();
                }
            }

        } //end if

        db.insert(TABLE_waifu, null, values);
        db.close();
    }

    public waifuImage findImage(int id) {
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
            wi.set_imagePath(null);
        }
        db.close();
        return wi;
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

    public void fillMissingImage(int i) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, i - 1);

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * From " + TABLE_waifu + " Where " + COLUMN_ID + " = " + i;
        Cursor c = db.rawQuery(query, null);
        waifuImage wi = new waifuImage();

        if (c.moveToFirst()) {
            wi.set_id(Integer.parseInt(c.getString(0)));
            db.update(TABLE_waifu, values, COLUMN_ID + " = ?",
                    new String[] { String.valueOf(wi.get_id())} );
            c.close();
        }
    }
}
