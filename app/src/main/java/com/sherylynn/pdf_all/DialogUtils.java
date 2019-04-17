package com.sherylynn.pdf_all;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;

import com.blankj.utilcode.util.LogUtils;

public class DialogUtils {
    public static void create_test_dialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.dialog_test_message)
                .setTitle(R.string.dialog_test_title);
        AlertDialog dialog=builder.create();
        dialog.show();
    }
    public static void reopen(final Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_reopen,null))
                .setPositiveButton(R.string.open, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //reopen pdf
                        LogUtils.v("click reopen");
                        LogUtils.v("reopen UriString:"+SPUtils.get(activity,"LastPDFUriString","self"));
                        SPUtils.putSync(activity,"reopenClick",true);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SPUtils.putSync(activity,"reopenClick",false);
                        // cancel
                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();
    }
    public static void signin(final Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_signin,null))
                .setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //sign in the user
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // cancel
                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();
    }
}
