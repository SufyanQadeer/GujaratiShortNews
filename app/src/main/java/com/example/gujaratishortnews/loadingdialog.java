package com.example.gujaratishortnews;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class loadingdialog {

    Activity activity;
    Dialog dialog;

    public loadingdialog(Activity activity) {
        this.activity = activity;
    }


    public void showdialog(){


        final View dialogview = View.inflate(activity,R.layout.activity_splash_screen,null);
        dialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogview);
        dialog.setCancelable(true);

     //   ImageView imageView = (ImageView)dialog.findViewById(R.id.splashgif);

//        Glide.with(activity).load(R.drawable.loadingbackgroundgif).into(imageView);

        dialog.show();

    }

    public void hidedialog(){
        dialog.dismiss();
    }
}



