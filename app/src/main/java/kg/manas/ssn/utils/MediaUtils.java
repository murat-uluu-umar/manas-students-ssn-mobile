package kg.manas.ssn.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

import kg.manas.ssn.R;
import kg.manas.ssn.view.ActivityMain;

public class MediaUtils {
    public static byte[] bitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return  byteArray;
    }
    public static Bitmap byteArrayToBitmap(byte[] array){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        return BitmapFactory.decodeByteArray(array, 0, array.length, options);
    }
    public static AlertDialog noNetworkConnectionDialog(Context context){
        return new AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_baseline_wifi_off_24)
                .setTitle("No internet connection")
                .setMessage("Please check internet connection, and retry")
                .setNeutralButton("Close", (dialogInterface, i) -> dialogInterface.dismiss()).create();
    }
    public static AlertDialog logoutDialog(DialogInterface.OnClickListener positive){
        return new AlertDialog.Builder(ActivityMain.Context)
                .setTitle("Logout")
                .setMessage("Do you want to logout?")
                .setPositiveButton("Yes", positive)
                .setNegativeButton("No", (dialogInterface, i) -> {
                    dialogInterface.cancel();
                })
                .create();
    }
    public static AlertDialog errorDialog(Context context, String title,String message){
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_baseline_error_outline_24)
                .setNeutralButton("Close", (dialogInterface, i) -> dialogInterface.dismiss())
                .create();
    }
}
