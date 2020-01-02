package com.norden.warehousemanagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class itemActivity extends Activity {

    private long idTask;
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
                aceptarCambios();
            }
        });

        // Boton eliminar
        Button  btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteTask();
            }
        });

        // Boton cancelar
        Button  btnCancel = (Button) findViewById(R.id.btnCancelar);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancelarCambios();
            }
        });

        // Busquem el id que estem modificant
        // si el el id es -1 vol dir que s'està creant
        idTask = this.getIntent().getExtras().getLong("id");

        if (idTask != -1) {
            // Si estem modificant carreguem les dades en pantalla
            cargarDatos();
        }
    }

    private void cargarDatos() {
        // Demanem un cursor que retorna un sol registre amb les dades de la tasca
        // Això es podria fer amb un classe pero...
        Cursor datos = bd.item(idTask);
        datos.moveToFirst();

        // Carreguem les dades en la interfície
        TextView tv;

        tv = (TextView) findViewById(R.id.edtItemCode);
        tv.setText(datos.getString(datos.getColumnIndex(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_ITEMCODE)));

        tv = (TextView) findViewById(R.id.edtDescription);
        tv.setText(datos.getString(datos.getColumnIndex(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_DESCRIPTION)));

        tv = (TextView) findViewById(R.id.edtPvp);
        tv.setText(datos.getString(datos.getColumnIndex(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_PVP)));

        tv = (TextView) findViewById(R.id.edtStock);
        tv.setText(datos.getString(datos.getColumnIndex(warehouseManagementDataSource.WAREHOUSEMANAGEMENT_STOCK)));
    }

    private void aceptarCambios() {
        // Validem les dades
        TextView tv;

        // El codi de l'article ha d'estar informat
        tv = (TextView) findViewById(R.id.edtItemCode);
        String itemCode = tv.getText().toString();
        if (itemCode.trim().equals("")) {
            myDialogs.showToast(this,"El codi de l'article ha d'estar informat");
            return;
        }

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

        // El estoc ha de ser minim 0
        tv = (TextView) findViewById(R.id.edtStock);
        int iStock;
        try {
            iStock = Integer.valueOf(tv.getText().toString());
        }
        catch (Exception e) {
            myDialogs.showToast(this,"El PVP ha de ser un numero enter");
            return;
        }

        if ((iStock < 0)) {
            myDialogs.showToast(this,"El PVP ha de ser mínim 0");
            return;
        }

        // Mirem si estem creant o estem guardant
        if (idTask == -1) {
            idTask = bd.itemAdd(itemCode, description, iPvp, iStock);
        }
        else {
            bd.itemUpdate(idTask,itemCode, description, iPvp, iStock);
        }

        Intent mIntent = new Intent();
        mIntent.putExtra("id", idTask);
        setResult(RESULT_OK, mIntent);

        finish();
    }

    private void cancelarCambios() {
        Intent mIntent = new Intent();
        mIntent.putExtra("id", idTask);
        setResult(RESULT_CANCELED, mIntent);

        finish();
    }

    private void deleteTask() {
        // Pedimos confirmación
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("¿Desitja eliminar l'article?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                bd.itemDelete(idTask);

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
