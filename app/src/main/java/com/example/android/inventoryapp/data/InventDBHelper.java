package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mayank on 05-03-2017.
 */

public class InventDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = InventDBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "invent.db";
    private static final int DATTABASE_VERSION = 1;

    public InventDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATTABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_INVENT_TABLE = "CREATE TABLE " + InventContract.InventEntry.TABLE_NAME + " ("
                + InventContract.InventEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + InventContract.InventEntry.COLUMN_NAME + " TEXT NOT NULL,"
                + InventContract.InventEntry.COLUMN_PRICE + " TEXT,"
                + InventContract.InventEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 2,"
                + InventContract.InventEntry.COLUMN_IMAGE + " BLOB,"
                + InventContract.InventEntry.COLUMN_EMAIL + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_INVENT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + InventContract.InventEntry.TABLE_NAME);
        onCreate(db);
    }
}
