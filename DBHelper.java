package com.example.logintime;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "Database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table LoginLogout(id INTEGER PRIMARY KEY AUTOINCREMENT, namauser TEXT, waktulogin TEXT, waktulogout TEXT)");
        DB.execSQL("create Table Location(id INTEGER PRIMARY KEY AUTOINCREMENT, namauser TEXT, tanggal TEXT, latitude TEXT, longitude TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int ii) {
        DB.execSQL("drop Table if exists LoginLogout");
        DB.execSQL("drop Table if exists Location");
        onCreate(DB);
    }

    public Boolean insertloginlogoutdata(String waktulogin, String waktulogout)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("namauser", "admin");
        contentValues.put("waktulogin", waktulogin);
        contentValues.put("waktulogout", waktulogout);
        long result=DB.insert("LoginLogout", null, contentValues);
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }

    public Boolean insertlocationdata(String tanggal, String latitude, String longitude)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("namauser", "admin");
        contentValues.put("tanggal", tanggal);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        long result=DB.insert("Location", null, contentValues);
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }
}
