package com.Whowant.Tokki.Utils;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class DialogMenu {

    public DialogMenu() {
    }

    public void showMenu(Activity activity, String title, int strArray, ItemClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);

        builder.setItems(activity.getResources().getStringArray(strArray), (dialog, pos) -> {
            if (listener != null) {
                listener.onClick(dialog, pos);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showMenu(Activity activity, String title, String[] strArray, ItemClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);

        builder.setItems(strArray, (dialog, pos) -> {
            if (listener != null) {
                listener.onClick(dialog, pos);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public interface ItemClickListener {
        void onClick(DialogInterface dialog, int pos);
    }
}
