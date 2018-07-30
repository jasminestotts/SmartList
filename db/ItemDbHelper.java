package com.gmail.jasminestotts.smartlist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemDbHelper extends SQLiteOpenHelper {

    public ItemDbHelper(Context context) {
        super(context, ItemContract.DB_NAME, null, ItemContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + ItemContract.ItemEntry.TABLE + " ( " +
                ItemContract.ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ItemContract.ItemEntry.COL_ITEM_TITLE + " TEXT NOT NULL);";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ItemContract.ItemEntry.TABLE);
        onCreate(db);
    }
}
