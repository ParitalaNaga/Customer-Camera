package com.example.cameraapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class mySqliteDbHandler extends SQLiteOpenHelper {
    public mySqliteDbHandler(@Nullable Context context) {
        super(context, "ImageDatabase.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table tableimage(name text, image blob);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("drop table if exists tableimage");
    }

    public boolean insertImage(String username, byte[] img) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name ", "MyImage");
        contentValues.put("image ", img);
        long ins = sqLiteDatabase.insert("tableimage", null, contentValues);
        if (ins == -1) return false;
        else return true;
    }


}
