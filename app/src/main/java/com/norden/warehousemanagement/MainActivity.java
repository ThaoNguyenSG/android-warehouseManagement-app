package com.norden.warehousemanagement;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends ListActivity {

    private static int ACTIVITY_ITEM_ADD = 1;
    private static int ACTIVITY_ITEM_UPDATE = 2;

    private long idActual;

    private warehouseManagementDataSource bd;
    private adapterWarehouseManagementItems scItems;

    private static String[] from = new String[]{warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ITEMCODE,
            warehouseManagementDataSource.WAREHOUSEMANAGEMENT_DESCRIPTION,
            warehouseManagementDataSource.WAREHOUSEMANAGEMENT_STOCK};
    private static int[] to = new int[]{R.id.tvItemCode, R.id.tvDescription, R.id.tvStock};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Activity filter");

        // Botó d'afegir
        Button btn = (Button) findViewById(R.id.btnAdd);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        bd = new warehouseManagementDataSource(this);
        loadItems();
    }

    private void addItem() {
        // Cridem a l'activity del detall de la tasca enviant com a id -1
        Bundle bundle = new Bundle();
        bundle.putLong("id",-1);

        idActual = -1;

        Intent i = new Intent(this, itemActivity.class );
        i.putExtras(bundle);
        startActivityForResult(i,ACTIVITY_ITEM_ADD);
    }

    private void loadItems() {
        // Demanem totes les tasques
        Cursor cursorTasks = bd.warehouseManagement();

        // Now create a simple cursor adapter and set it to display
        scItems = new adapterWarehouseManagementItems(this, R.layout.warehouse_item, cursorTasks, from, to, 1);
        setListAdapter(scItems);
    }

    private void refreshItems() {
        Cursor cursorTasks = bd.warehouseManagement();

        // Notifiquem al adapter que les dades han canviat i que refresqui
        scItems.changeCursor(cursorTasks);
        scItems.notifyDataSetChanged();
    }

    private void updateItem(long id) {
        // Cridem a l'activity del detall de l'article enviant com a id -1
        Bundle bundle = new Bundle();
        bundle.putLong("id",id);

        idActual = id;

        Intent i = new Intent(this, itemActivity.class );
        i.putExtras(bundle);
        startActivityForResult(i,ACTIVITY_ITEM_UPDATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_ITEM_ADD) {
            if (resultCode == RESULT_OK) {
                // Carreguem tots els articles a lo bestia
                refreshItems();
            }
        }

        if (requestCode == ACTIVITY_ITEM_UPDATE) {
            if (resultCode == RESULT_OK) {
                refreshItems();
            }
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // modifiquem el id
        updateItem(id);
    }

}

class adapterWarehouseManagementItems extends android.widget.SimpleCursorAdapter {

    public adapterWarehouseManagementItems(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = super.getView(position, convertView, parent);

        // Agafem l'objecte de la view que es una LINEA DEL CURSOR
        Cursor linia = (Cursor) getItem(position);

        int stock = linia.getInt(
                linia.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_STOCK)
        );

        // Si el estoc actual es 0 o negatiu la row s'ha de mostrar en color vermell de fons, si té estoc caldrà que aparegui en color blanc de fons.
        if (stock <= 0) {
            view.setBackgroundColor(Color.parseColor("#FF0000"));
        }

        return view;
    }
}
