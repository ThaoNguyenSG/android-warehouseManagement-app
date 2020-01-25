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

    public static final String table_MOVEMENT = "movement";
    public static final String MOVEMENT_ID = "_id";
    public static final String MOVEMENT_ITEMCODE = "itemCode";
    public static final String MOVEMENT_DATE = "date";
    public static final String MOVEMENT_QUANTITY = "quantity";
    public static final String MOVEMENT_TYPE = "type";
    public static final String MOVEMENT_WAREHOUSEMANAGEMENTID = "warehouseManagementId";

    private warehouseManagementHelper dbHelper;
    private SQLiteDatabase dbW, dbR;

    // CONSTRUCTOR
    public warehouseManagementDataSource(Context ctx) {
        // En el constructor directament obro la comunicació amb la base de dades
        dbHelper = new warehouseManagementHelper(ctx);

        // Construeixo dos databases, un per llegir i l'altre per alterar
        open();
    }

    // DESTRUCTOR
    protected void finalize () {
        // Tanquem les databases
        dbW.close();
        dbR.close();
    }

    private void open() {
        dbW = dbHelper.getWritableDatabase();
        dbR = dbHelper.getReadableDatabase();
    }

    /* FUNCIONS QUE RETORNEN CURSORS DE LA BDD */

    // Retorna tots els articles
    public Cursor warehouseManagement() {
        return dbR.query(table_WAREHOUSEMANAGEMENT, new String[]{WAREHOUSEMANAGEMENT_ID,WAREHOUSEMANAGEMENT_ITEMCODE,WAREHOUSEMANAGEMENT_DESCRIPTION,WAREHOUSEMANAGEMENT_PVP,WAREHOUSEMANAGEMENT_STOCK},
                null, null,
                null, null, WAREHOUSEMANAGEMENT_ID);
    }

    // Retorna un cursor només amb el id indicat (retorna un sol article)
    public Cursor item(long id) {
        return dbR.query(table_WAREHOUSEMANAGEMENT, new String[]{WAREHOUSEMANAGEMENT_ID,WAREHOUSEMANAGEMENT_ITEMCODE,WAREHOUSEMANAGEMENT_DESCRIPTION,WAREHOUSEMANAGEMENT_PVP,WAREHOUSEMANAGEMENT_STOCK},
                WAREHOUSEMANAGEMENT_ID+ "=?", new String[]{String.valueOf(id)},
                null, null, null);

    }

    // Retorna un cursor només amb el codi de l'article indicat
    public Cursor itemCode(String itemCode) {
        return dbR.query(table_WAREHOUSEMANAGEMENT, new String[]{WAREHOUSEMANAGEMENT_ID,WAREHOUSEMANAGEMENT_ITEMCODE,WAREHOUSEMANAGEMENT_DESCRIPTION,WAREHOUSEMANAGEMENT_PVP,WAREHOUSEMANAGEMENT_STOCK},
                WAREHOUSEMANAGEMENT_ITEMCODE+ "=?", new String[]{String.valueOf(itemCode)},
                null, null, null);

    }

    // Retorna els articles que SI tenen stock
    public Cursor stockIems() {
        return dbR.query(table_WAREHOUSEMANAGEMENT, new String[]{WAREHOUSEMANAGEMENT_ID,WAREHOUSEMANAGEMENT_ITEMCODE,WAREHOUSEMANAGEMENT_DESCRIPTION,WAREHOUSEMANAGEMENT_PVP,WAREHOUSEMANAGEMENT_STOCK},
                WAREHOUSEMANAGEMENT_STOCK + ">" + 0, null,
                null, null, WAREHOUSEMANAGEMENT_ID);
    }

    // Retorna els articles que NO tenen stock
    public Cursor noStockIems() {
        return dbR.query(table_WAREHOUSEMANAGEMENT, new String[]{WAREHOUSEMANAGEMENT_ID,WAREHOUSEMANAGEMENT_ITEMCODE,WAREHOUSEMANAGEMENT_DESCRIPTION,WAREHOUSEMANAGEMENT_PVP,WAREHOUSEMANAGEMENT_STOCK},
                WAREHOUSEMANAGEMENT_STOCK + "<=" + 0, null,
                null, null, WAREHOUSEMANAGEMENT_ID);
    }

    /* FUNCIONS DE MANIPULACIÓ DE DADES */

    // Creem un nou article i retornem el id creat per si el necessitem
    public long itemAdd(String itemCode, String description, double pvp, int stock) {
        ContentValues values = new ContentValues();
        values.put(WAREHOUSEMANAGEMENT_ITEMCODE, itemCode);
        values.put(WAREHOUSEMANAGEMENT_DESCRIPTION, description);
        values.put(WAREHOUSEMANAGEMENT_PVP, pvp);
        values.put(WAREHOUSEMANAGEMENT_STOCK, stock);

        return dbW.insert(table_WAREHOUSEMANAGEMENT,null,values);
    }

    // Modifiquem els valors de l'article amb clau primària "id"
    public void itemUpdate(long id, String itemCode, String description, double pvp, int stock) {
        ContentValues values = new ContentValues();
        values.put(WAREHOUSEMANAGEMENT_ITEMCODE, itemCode);
        values.put(WAREHOUSEMANAGEMENT_DESCRIPTION, description);
        values.put(WAREHOUSEMANAGEMENT_PVP, pvp);
        values.put(WAREHOUSEMANAGEMENT_STOCK, stock);

        dbW.update(table_WAREHOUSEMANAGEMENT,values, WAREHOUSEMANAGEMENT_ID + " = ?", new String[] { String.valueOf(id) });
    }

    // Eliminem l'item amb clau primària "id"
    public void itemDelete(long id) {
        dbW.delete(table_WAREHOUSEMANAGEMENT,WAREHOUSEMANAGEMENT_ID + " = ?", new String[] { String.valueOf(id) });
    }

    // TAULA MOVEMENT ------------------------------- :D

    public long movementAdd(String itemCode, String date, int quantity, String type, int warehouseManagementId) {
        ContentValues values = new ContentValues();
        values.put(MOVEMENT_ITEMCODE, itemCode);
        values.put(MOVEMENT_DATE, date);
        values.put(MOVEMENT_QUANTITY, quantity);
        values.put(MOVEMENT_TYPE, type);
        values.put(MOVEMENT_WAREHOUSEMANAGEMENTID, warehouseManagementId);

        return dbW.insert(table_MOVEMENT,null,values);
    }

    public Cursor movements() {
        return dbR.query(table_MOVEMENT, new String[]{MOVEMENT_ID, MOVEMENT_ITEMCODE,MOVEMENT_DATE,MOVEMENT_QUANTITY,MOVEMENT_TYPE,MOVEMENT_WAREHOUSEMANAGEMENTID},
                null, null,
                null, null, MOVEMENT_ID);
    }
}
