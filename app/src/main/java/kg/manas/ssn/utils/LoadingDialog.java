package kg.manas.ssn.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import java.util.Objects;

import kg.manas.ssn.R;

public class LoadingDialog {
    public static AlertDialog dialog;

    public LoadingDialog(Activity activity) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            View view = activity.getLayoutInflater().inflate(R.layout.fragment_loading_dialog, null);
            builder.setView(view);
            builder.setCancelable(false);
            dialog = builder.create();
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(){
        try {
            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void dismiss(){
        try {
            dialog.dismiss();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
