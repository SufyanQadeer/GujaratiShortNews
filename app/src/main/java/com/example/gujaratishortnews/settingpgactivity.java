package com.example.gujaratishortnews;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class settingpgactivity extends AppCompatActivity {

    Switch notiswitch,imageswitch;
    Boolean noti=true;
    Boolean image=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingpage);

        notiswitch=(Switch)findViewById(R.id.notiswitch);
        imageswitch=(Switch)findViewById(R.id.imageswitch);


        SharedPreferences pref = getApplicationContext().getSharedPreferences("Mypref",0);
        final SharedPreferences.Editor editor=pref.edit();

        notiswitch.setChecked(pref.getBoolean("notienable",true));
        imageswitch.setChecked(pref.getBoolean("imageenable",true));


        notiswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        editor.putBoolean("notienable",isChecked);
        editor.commit();

        if(isChecked)
        {
            Toast.makeText(settingpgactivity.this, "Notifications turned on", Toast.LENGTH_SHORT).show();

        }else
        {
            Toast.makeText(settingpgactivity.this, "Notifications turned off", Toast.LENGTH_SHORT).show();
        }

            }
        });

     imageswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
             editor.putBoolean("imageenable",isChecked);
             editor.commit();


             if(isChecked)
             {
                 Toast.makeText(settingpgactivity.this, "Enabled Images-App restart required for effect", Toast.LENGTH_SHORT).show();

             }else
             {
                 Toast.makeText(settingpgactivity.this, "Disabled Images-App restart required for effect", Toast.LENGTH_SHORT).show();
             }
         }


     });





    }


     @Override
   public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void backbuttonclicked(View view) {
        NavUtils.navigateUpFromSameTask(this);
    }

    public void feedbackclicked(View view) {

        final View feedbackdialog = View.inflate(this,R.layout.feedbacklayout,null);

        final Dialog dialog1 = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(feedbackdialog);

        final Button b = (Button)dialog1.getWindow().findViewById(R.id.feedbacksubmit);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String to = "nikunjbhimani99@gmail.com";
                String sub = "Feedback For Instant App..";
                String mess = ((EditText)dialog1.findViewById(R.id.feedbackedit)).getText().toString();
                Intent mail = new Intent(Intent.ACTION_SENDTO);
                mail.setData(Uri.parse("mailto:"));
                mail.putExtra(Intent.EXTRA_EMAIL,new String[]{to});
                mail.putExtra(Intent.EXTRA_SUBJECT, sub);
                mail.putExtra(Intent.EXTRA_TEXT, mess);
               // mail.setType("message/rfc822");
                startActivity(Intent.createChooser(mail, "Send email via:"));
                dialog1.dismiss();
            }
        });

        dialog1.setCancelable(true);
        dialog1.show();

    }


    public void rateusclicked(View view) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + this.getPackageName())));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    public void shareappclicked(View view) {
        try {
            Intent shareappIntent = new Intent(Intent.ACTION_SEND);
            shareappIntent.setType("text/plain");
            shareappIntent.putExtra(Intent.EXTRA_SUBJECT, "Instant-Gujarati Short News");
            String shareMessage= "\nDownload Instant for getting Gujarati News instantly..\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareappIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareappIntent, "choose one"));
        }
        catch (Exception e)
        {
        }
    }



}
