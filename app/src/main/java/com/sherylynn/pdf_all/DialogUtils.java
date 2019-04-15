package com.sherylynn.pdf_all;

import android.app.AlertDialog;
import android.content.Context;

public class DialogUtils {
    public static void create_test_dialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.dialog_test_message)
                .setTitle(R.string.dialog_test_title);
        AlertDialog dialog=builder.create();
        dialog.show();
    }
}
