package com.norden.warehousemanagement;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

public class myDialogs {
    public static void showShortToast(Context cts, String text) {
        Toast.makeText(cts, text, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context cts, String text) {
        Toast.makeText(cts, text, Toast.LENGTH_LONG).show();
    }
}
