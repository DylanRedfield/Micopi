package me.dylanredfield.micopi;

import android.content.Context;
import android.support.v7.app.AlertDialog;

public class Helpers {
    public static String getHtmlString(String input, String hexColor) {
        return "<font color = '" + hexColor + "'>" + input + "</font>";
    }

    public static void showDialog(String title, String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", null)
                .show();
    }
}
