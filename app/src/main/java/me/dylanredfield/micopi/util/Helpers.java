package me.dylanredfield.micopi.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

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

    public static boolean isOnline(Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public static ProgressDialog showProgressDialog(String message, Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        return dialog;
    }

    public static void hideProgressDialog(ProgressDialog dialog) {
        dialog.dismiss();
    }

    public static ArrayList<String> getStringArrayFromPoint(List<ParseObject> list) {
        ArrayList<String> newList = new ArrayList<>();

        for (ParseObject po : list) {
            newList.add(po.getString(Keys.USERNAME_STR));
        }
        return newList;
    }
}
