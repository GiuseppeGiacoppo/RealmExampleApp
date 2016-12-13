package me.giacoppo.realmexampleapp.sqlite.tables;


import android.provider.BaseColumns;

public class AccountTable implements BaseColumns {
    public static final String TABLE_NAME = "accounts";

    public static final String NAME = "name";
    public static final String TOTAL = "total";

    public static final String[] FIELDS = {_ID,NAME,TOTAL};
}