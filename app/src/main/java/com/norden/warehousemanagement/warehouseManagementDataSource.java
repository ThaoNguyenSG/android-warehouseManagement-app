package com.norden.warehousemanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class warehouseManagementDataSource {

    public static final String table_WAREHOUSEMANAGEMENT = "warehouseManagement";
    public static final String WAREHOUSEMANAGEMENT_ID = "_id";
    public static final String WAREHOUSEMANAGEMENT_ITEMCODE = "itemCode";
    public static final String WAREHOUSEMANAGEMENT_DESCRIPTION = "description";
    public static final String WAREHOUSEMANAGEMENT_PVP = "pvp";
    public static final String WAREHOUSEMANAGEMENT_STOCK = "stock";

    private warehouseManagementHelper dbHelper;
    private SQLiteDatabase dbW, dbR;

    // CONSTRUCTOR
    public warehouseManagementDataSource(Context ctx) {
        // En el constructor directament obro la comunicació amb la base de dades
        dbHelper = new warehouseManagementHelper(ctx);

        // A més també construeixo dos databases un per llegir i l'altre per alterar
        open();
    }

    // DESTRUCTOR
    protected void finalize () {
        // Cerramos los databases
        dbW.close();
        dbR.close();
    }

    private void open() {
        dbW = dbHelper.getWritableDatabase();
        dbR = dbHelper.getReadableDatabase();
    }

    // ******************
    // Funcions que retornen cursors de la bdd
    // ******************

    public Cursor warehouseManagement() {
        // Retorem totes les tasques
        return dbR.query(table_WAREHOUSEMANAGEMENT, new String[]{WAREHOUSEMANAGEMENT_ID,WAREHOUSEMANAGEMENT_ITEMCODE,WAREHOUSEMANAGEMENT_DESCRIPTION,WAREHOUSEMANAGEMENT_PVP,WAREHOUSEMANAGEMENT_STOCK},
                null, null,
                null, null, WAREHOUSEMANAGEMENT_ID);
    }

    public Cursor item(long id) {
        // Retorna un cursor només amb el id indicat
        return dbR.query(table_WAREHOUSEMANAGEMENT, new String[]{WAREHOUSEMANAGEMENT_ID,WAREHOUSEMANAGEMENT_ITEMCODE,WAREHOUSEMANAGEMENT_DESCRIPTION,WAREHOUSEMANAGEMENT_PVP,WAREHOUSEMANAGEMENT_STOCK},
                WAREHOUSEMANAGEMENT_ID+ "=?", new String[]{String.valueOf(id)},
                null, null, null);

    }

    // ******************
    // Funcions de manipulació de dades
    // ******************

    public long itemAdd(String itemCode, String description, int pvp, int stock) {
        // Creem un nou article i retornem el id crear per si el necessiten
        ContentValues values = new ContentValues();
        values.put(WAREHOUSEMANAGEMENT_ITEMCODE, itemCode);
        values.put(WAREHOUSEMANAGEMENT_DESCRIPTION, description);
        values.put(WAREHOUSEMANAGEMENT_PVP, pvp);
        values.put(WAREHOUSEMANAGEMENT_STOCK, stock);

        return dbW.insert(table_WAREHOUSEMANAGEMENT,null,values);
    }

    public void itemUpdate(long id, String itemCode, String description, int pvp, int stock) {
        // Modifiquem els valors de l'item amb clau primària "id"
        ContentValues values = new ContentValues();
        values.put(WAREHOUSEMANAGEMENT_ITEMCODE, itemCode);
        values.put(WAREHOUSEMANAGEMENT_DESCRIPTION, description);
        values.put(WAREHOUSEMANAGEMENT_PVP, pvp);
        values.put(WAREHOUSEMANAGEMENT_STOCK, stock);

        dbW.update(table_WAREHOUSEMANAGEMENT,values, WAREHOUSEMANAGEMENT_ID + " = ?", new String[] { String.valueOf(id) });
    }

    public void itemDelete(long id) {
        // Eliminem l'item amb clau primària "id"
        dbW.delete(table_WAREHOUSEMANAGEMENT,WAREHOUSEMANAGEMENT_ID + " = ?", new String[] { String.valueOf(id) });
    }

}
