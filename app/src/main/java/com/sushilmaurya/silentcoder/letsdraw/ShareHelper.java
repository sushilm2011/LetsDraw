package com.sushilmaurya.silentcoder.letsdraw;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by silentcoder on 18/1/18.
 */

public class ShareHelper {
    public int shareOnWhatsapp(AppCompatActivity appCompatActivity, Uri uri){
        if (uri != null) {
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
            whatsappIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            whatsappIntent.setType("image/*");
            try {
                appCompatActivity.startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                ex.printStackTrace();
                //showWarningDialog(appCompatActivity, appCompatActivity.getString(R.string.error_activity_not_found));
            }
        }
        return 0;
    }
}
