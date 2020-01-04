package com.norden.warehousemanagement;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class warehouseManagementHelper extends SQLiteOpenHelper {

    // database version
    private static final int database_VERSION = 1;

    // database name
    private static final String database_NAME = "warehouseManagementDataBase";

    public warehouseManagementHelper(Context context) {
        super(context, database_NAME, null, database_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WAREHOUSEMANAGEMENT =
                "CREATE TABLE warehouseManagement ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "itemCode TEXT," +
                        "description TEXT," +
                        "pvp INTEGER," +
                        "stock INTEGER)";

        db.execSQL(CREATE_WAREHOUSEMANAGEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // De moment no fem res

    }

}
