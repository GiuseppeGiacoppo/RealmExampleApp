package me.giacoppo.realmexampleapp.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.giacoppo.realmexampleapp.sqlite.tables.AccountTable;


public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Sqlite.db";

    public static final String SQL_CREATE_ACCOUNT_TABLE =
            "CREATE TABLE " + AccountTable.TABLE_NAME + " (" +
                    AccountTable._ID + " TEXT PRIMARY KEY," +
                    AccountTable.NAME + " TEXT," +
                    AccountTable.TOTAL + " DOUBLE" + " )";

    public static final String SQL_DELETE_ACCOUNT_TABLE =
            "DROP TABLE IF EXISTS " + AccountTable.TABLE_NAME;

    public MyDatabaseHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ACCOUNT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //sqLiteDatabase.execSQL(SQL_DELETE_DATABASE);
        //onCreate(sqLiteDatabase);
    }
}