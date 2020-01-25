package com.norden.warehousemanagement;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private static int ACTIVITY_ITEM_ADD = 1;
    private static int ACTIVITY_ITEM_UPDATE = 2;

    private int iStock = 100000000;
    private long idActual;

    private warehouseManagementDataSource bd;
    private adapterWarehouseManagementItems scItems;

    private static String[] from = new String[]{warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ITEMCODE,
            warehouseManagementDataSource.WAREHOUSEMANAGEMENT_DESCRIPTION,
            warehouseManagementDataSource.WAREHOUSEMANAGEMENT_STOCK};
    private static int[] to = new int[]{R.id.tvItemCode2, R.id.tvDescription2, R.id.tvStock2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Magatzem");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        bd = new warehouseManagementDataSource(this);
        loadItems();
    }

    private void addItem() {
        // Cridem a l'activity del detall de l'article enviant com a id -1
        Bundle bundle = new Bundle();
        bundle.putLong("id",-1);

        idActual = -1;

        Intent i = new Intent(this, itemActivity.class );
        i.putExtras(bundle);
        startActivityForResult(i,ACTIVITY_ITEM_ADD);
    }

    private void loadItems() {
        // Crido a la quaery que em retorna tots els articles
        Cursor cursorTasks = bd.warehouseManagement();

        // Now create a simple cursor adapter and set it to display
        scItems = new adapterWarehouseManagementItems(this, R.layout.warehouse_item, cursorTasks, from, to, 1);

        ListView lv = (ListView) findViewById(R.id.listView);

        lv.setAdapter(scItems);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateItem(id);
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter_items_menu, menu);
        return true;
    }

    // Capturar pulsacions en el menú de la barra superior.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.allItems:
                loadItems();
                return true;
            case R.id.stockItems:
                loadStockItems();
                return true;
            case R.id.noStockItems:
                loadNoStockItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadStockItems() {
        // Cridem la query que ens retorna tots els articles amb stock
        Cursor cursorTasks = bd.stockIems();

        // Now create a simple cursor adapter and set it to display
        scItems = new adapterWarehouseManagementItems(this, R.layout.warehouse_item, cursorTasks, from, to, 1);

        ListView lv = (ListView) findViewById(R.id.listView);

        lv.setAdapter(scItems);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateItem(id);
            }
        });
    }

    private void loadNoStockItems() {
        // Cridem a la query que ens retorna els articles que NO tenen stock
        Cursor cursorTasks = bd.noStockIems();

        // Now create a simple cursor adapter and set it to display
        scItems = new adapterWarehouseManagementItems(this, R.layout.warehouse_item, cursorTasks, from, to, 1);

        ListView lv = (ListView) findViewById(R.id.listView);

        lv.setAdapter(scItems);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateItem(id);
            }
        });
    }

    public void deleteItem(final int idItem) {
        // Pedimos confirmación
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("¿Desitja eliminar l'article?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                bd.itemDelete(idItem);

                Intent mIntent = new Intent();
                mIntent.putExtra("id", -1);  // Devolvemos -1 indicant que s'ha eliminat
                setResult(RESULT_OK, mIntent);

                refreshItems();
            }
        });

        builder.setNegativeButton("No", null);

        builder.show();

    }

    public void sendDialogDataToActivity(String stock, String date, boolean notAddingStock, Cursor linia) {
        //Log.e("@HOLAA", "Stock: " + stock + " | Date: " + date + " | Not adding stock? " + notAddingStock);

        int stockUpdate;

        if (!notAddingStock) {
            // AFEGIR STOCK
            bd.movementAdd(
                    linia.getString(linia.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ITEMCODE)),
                    date,
                    Integer.parseInt(stock),
                    "Entrada",
                    linia.getInt(linia.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ID))
            );
            stockUpdate = linia.getInt(linia.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_STOCK)) + Integer.parseInt(stock);
        }
        else {
            // TREURE STOCK
            bd.movementAdd(
                    linia.getString(linia.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ITEMCODE)),
                    date,
                    -Integer.parseInt(stock),
                    "Sortida",
                    linia.getInt(linia.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ID))
            );
            stockUpdate = linia.getInt(linia.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_STOCK)) - Integer.parseInt(stock);
        }

        bd.itemUpdate(
                linia.getInt(linia.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ID)),
                linia.getString(linia.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ITEMCODE)),
                linia.getString(linia.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_DESCRIPTION)),
                linia.getInt(linia.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_PVP)),
                stockUpdate);

        /*Cursor movements = bd.movements();
        movements.moveToFirst();

        Log.e("@MOVEMENTS", DatabaseUtils.dumpCursorToString(movements));*/

        refreshItems();
    }

}

class adapterWarehouseManagementItems extends android.widget.SimpleCursorAdapter {

    public MainActivity mainActivity;

    LayoutInflater mInflater;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    public adapterWarehouseManagementItems(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mainActivity = (MainActivity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final View view = super.getView(position, convertView, parent);

        // Agafem l'objecte de la view que es una LINEA DEL CURSOR
        final Cursor linia = (Cursor) getItem(position);

        int stock = linia.getInt(
                linia.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_STOCK)
        );

        // Si el estoc actual es 0 o negatiu la row s'ha de mostrar en color vermell de fons, si té estoc caldrà que aparegui en color blanc de fons.
        if (stock <= 0) {
            view.setBackgroundColor(Color.parseColor("#e53935"));
        }
        else if (stock > 0) {
            view.setBackgroundColor(0x00000000);
        }

        // Botó d'eliminar un article
        ImageView ivDelete = (ImageView) view.findViewById(R.id.ivDelete);
        ivDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View row = (View) v.getParent();
                // Busco el ListView
                ListView lv = (ListView) row.getParent();
                // Busco quina posicio ocupa la Row dins de la ListView
                int position = lv.getPositionForView(row);

                // Carrego la linia del cursor de la posició.
                Cursor linia = (Cursor) getItem(position);

                mainActivity.deleteItem(linia.getInt(linia.getColumnIndexOrThrow(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ID)));
            }
        });

        // Botó d'afegir stock
        ImageView ivStockAdd = (ImageView) view.findViewById(R.id.ivStockEdit);
        ivStockAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAlertDialogButtonClicked(view, "Modificar stock", linia);
            }
        });

        return view;
    }

    public void showAlertDialogButtonClicked(View view, String title, final Cursor linia) {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle(title);

        Context context = mainActivity.getApplicationContext();
        mInflater = LayoutInflater.from(context);

        // set the custom layout
        final View customLayout = mInflater.inflate(R.layout.dialog_stock, null);
        builder.setView(customLayout);

        final String[] _Date = {""};

        ImageView ivDatePicker = (ImageView) customLayout.findViewById(R.id.ivDatePicker);
        ivDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        mainActivity,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        final TextView tvDatePicker = (TextView) customLayout.findViewById(R.id.tvDatePicker);

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: yyyy/mm/dd: " + year + "/" + month + "/" + day);

                String date = day + "/" + month + "/" + year;
                tvDatePicker.setText(date);
                _Date[0] = date;
            }
        };

        // add a button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean notAddingStock = false;
                Switch switchStock = (Switch) customLayout.findViewById(R.id.switchStock);
                if (switchStock.isChecked()) {
                    notAddingStock = true;
                }

                EditText editText = customLayout.findViewById(R.id.edtNum);
                mainActivity.sendDialogDataToActivity(editText.getText().toString(), _Date[0], notAddingStock, linia);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
