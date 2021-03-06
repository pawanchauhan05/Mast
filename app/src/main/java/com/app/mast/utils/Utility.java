package com.app.mast.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mast.R;

/**
 * Created by pawansingh on 19/05/18.
 */

public class Utility {
    private static final Utility ourInstance = new Utility();

    public static Utility getInstance() {
        return ourInstance;
    }

    private Utility() {
    }

    public void showGreenToast(String textToShow, Context ctx) {
        if (ctx == null || textToShow == null)
            return;
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast_short, null);
        TextView text = (TextView) layout.findViewById(R.id.textToShow);
        text.setText(textToShow);
        text.setBackground(ContextCompat.getDrawable(ctx, R.drawable.green_capsule_shape));
        Toast toast = new Toast(ctx);
        toast.setGravity(Gravity.TOP, 0, Constants.TOAST_HEIGHT_FROM_TOP);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void showRedToast(String textToShow, Context ctx) {
        if (ctx == null || textToShow == null)
            return;
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast_short, null);
        TextView text = (TextView) layout.findViewById(R.id.textToShow);
        text.setText(textToShow);

        text.setBackground(ContextCompat.getDrawable(ctx, R.drawable.red_capsule_shape));

        Toast toast = new Toast(ctx);
        toast.setGravity(Gravity.TOP, 0, Constants.TOAST_HEIGHT_FROM_TOP);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    /**
     * this function is used to show default progress dialog.
     *
     * @param title          - progress dialog title
     * @param message        - progress dialog message
     * @param progressDialog - progress dialog object (not be null)
     */
    public void showProgressBar(String title, String message, ProgressDialog progressDialog) {
        if (progressDialog != null) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
            progressDialog.show();
            progressDialog.setCancelable(true);
        }
    }

    /**
     * this function is used to hide progress dialog.
     *
     * @param progressDialog - progress dialog object (not be null)
     */
    public void hideProgressBar(ProgressDialog progressDialog) {
        try {
            if (progressDialog != null)
                progressDialog.cancel();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
