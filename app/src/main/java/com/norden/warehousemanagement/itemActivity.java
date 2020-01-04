package com.norden.warehousemanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class itemActivity extends Activity {

    private long idItem;
    private warehouseManagementDataSource bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        bd = new warehouseManagementDataSource(this);

        // Botones de aceptar y cancelar
        // Boton ok
        Button btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                acceptChanges();
            }
        });

        // Boton eliminar
        Button  btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteItem();
            }
        });

        // Boton cancelar
        Button  btnCancel = (Button) findViewById(R.id.btnCancelar);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancelChanges();
            }
        });

        // Busquem el id que estem modificant
        // si el el id es -1 vol dir que s'està creant
        idItem = this.getIntent().getExtras().getLong("id");

        if (idItem != -1) {
            // Si estem modificant carreguem les dades en pantalla
            loadData();
        }
    }

    private void loadData() {
        // Demanem un cursor que retorna un sol registre amb les dades de l'article
        // Això es podria fer amb un classe pero...
        Cursor datos = bd.item(idItem);
        datos.moveToFirst();

        // Carreguem les dades en la interfície
        TextView tv;

        tv = (TextView) findViewById(R.id.edtItemCode);
        tv.setEnabled(false);
        tv.setText(datos.getString(datos.getColumnIndex(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ITEMCODE)));

        tv = (TextView) findViewById(R.id.edtDescription);
        tv.setText(datos.getString(datos.getColumnIndex(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_DESCRIPTION)));

        tv = (TextView) findViewById(R.id.edtPvp);
        tv.setText(datos.getString(datos.getColumnIndex(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_PVP)));

        tv = (TextView) findViewById(R.id.edtStock);
        tv.setText(datos.getString(datos.getColumnIndex(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_STOCK)));
    }

    private void acceptChanges() {
        // Validem les dades
        TextView tv;

        // El codi de l'article ha d'estar informat
        tv = (TextView) findViewById(R.id.edtItemCode);
        String itemCode = tv.getText().toString();
        if (itemCode.trim().equals("")) {
            myDialogs.showToast(this,"El codi de l'article ha d'estar informat");
            return;
        }

        /*Cursor c = bd.item(Long.parseLong(itemCode));
        if (c != null) {
            myDialogs.showToast(this,"Ja existeix un article amb aquest codi");
            return;
        }*/

        // La descripció ha d'estar informada
        tv = (TextView) findViewById(R.id.edtDescription);
        String description = tv.getText().toString();
        if (description.trim().equals("")) {
            myDialogs.showToast(this,"La descripció ha d'estar informada");
            return;
        }

        // El PVP ha de ser minim 0
        tv = (TextView) findViewById(R.id.edtPvp);
        int iPvp;
        try {
            iPvp = Integer.valueOf(tv.getText().toString());
        }
        catch (Exception e) {
            myDialogs.showToast(this,"El PVP ha de ser un numero enter");
            return;
        }

        if ((iPvp < 0)) {
            myDialogs.showToast(this,"El PVP ha de ser mínim 0");
            return;
        }

        // El estoc ha de ser un enter
        tv = (TextView) findViewById(R.id.edtStock);
        int iStock;
        try {
            iStock = Integer.valueOf(tv.getText().toString());
        }
        catch (Exception e) {
            myDialogs.showToast(this,"El estoc ha de ser un numero enter");
            return;
        }

        // Mirem si estem creant o estem guardant
        if (idItem == -1) {
            // El estoc ha de ser mínim 0 si estem creant
            if ((iStock < 0)) {
                myDialogs.showToast(this,"El estoc ha de ser mínim 0");
                return;
            }

            idItem = bd.itemAdd(itemCode, description, iPvp, iStock);
        }
        else {
            bd.itemUpdate(idItem,itemCode, description, iPvp, iStock);
        }

        Intent mIntent = new Intent();
        mIntent.putExtra("id", idItem);
        setResult(RESULT_OK, mIntent);

        finish();
    }

    private void cancelChanges() {
        Intent mIntent = new Intent();
        mIntent.putExtra("id", idItem);
        setResult(RESULT_CANCELED, mIntent);

        finish();
    }

    private void deleteItem() {
        // Pedimos confirmación
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("¿Desitja eliminar l'article?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                bd.itemDelete(idItem);

                Intent mIntent = new Intent();
                mIntent.putExtra("id", -1);  // Devolvemos -1 indicant que s'ha eliminat
                setResult(RESULT_OK, mIntent);

                finish();
            }
        });

        builder.setNegativeButton("No", null);

        builder.show();

    }

}
