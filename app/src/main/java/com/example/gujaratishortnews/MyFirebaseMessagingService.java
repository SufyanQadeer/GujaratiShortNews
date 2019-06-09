package com.example.gujaratishortnews;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.gujaratishortnews.Config.Config;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

SharedPreferences prefnoti;
Boolean shownoti;


    Target targe = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            showNotification(bitmap);
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        prefnoti = getSharedPreferences("Mypref",0);
        shownoti=prefnoti.getBoolean("notienable",true);

        if(shownoti)
        {
            if(remoteMessage.getData()!=null)
            {
                getImage(remoteMessage);

            }
        }






    }

    private void getImage(final RemoteMessage remoteMessage) {
       // Config.message=remoteMessage.getNotification().getBody();
        Config.title=remoteMessage.getData().get("title");
        Config.image=remoteMessage.getData().get("image");
        Config.newsid=Integer.parseInt(remoteMessage.getData().get("newsid"));

        if(remoteMessage.getData()!=null)
        {
            Handler uihandler = new Handler(Looper.getMainLooper());
            uihandler.post(new Runnable() {
                @Override
                public void run() {

                    if(Config.image!=null)
                    {

                        Picasso.get().load(Config.image).into(targe);



                       }
                }

            });
        }




    }

    public void showNotification(Bitmap bitmap) {

        RemoteViews contentviewsmall = new RemoteViews(getPackageName(),R.layout.notificationlayout);
        RemoteViews contentviewbig = new RemoteViews(getPackageName(),R.layout.notificationlayoutbig);

        Bitmap bitmapsmall = BitmapFactory.decodeResource(getResources(),R.mipmap.instanticon);
        Bitmap bitmapbigicon = BitmapFactory.decodeResource(getResources(),R.drawable.notiicongeneral);

        contentviewsmall.setImageViewBitmap(R.id.imagesmallnoti, bitmapsmall);
        contentviewsmall.setTextViewText(R.id.textsmallnoti,Config.title);
         contentviewbig.setTextViewText(R.id.textbignoti,Config.title);
          contentviewbig.setImageViewBitmap(R.id.imagebignoti,bitmap);
          contentviewbig.setImageViewBitmap(R.id.bignotiicon,bitmapbigicon);

        int notificationId=0;
        Uri path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("newsid", Config.newsid);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,0);



        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"General")
               // .setContentTitle(Config.title)
                .setSmallIcon(R.drawable.noticon)
            //    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
              .setCustomContentView(contentviewsmall)
               .setCustomBigContentView(contentviewbig)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
             //   .setContentText(Config.message)
                .setSound(path);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)

        {
            String chanelID = "General";
            NotificationChannel channel = new NotificationChannel(chanelID,"Generalid",NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(chanelID);
        }
        Notification notification = builder.build();
        notificationManager.notify(notificationId,notification);
    }


}
