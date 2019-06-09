package com.example.gujaratishortnews;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IFirestoreLoadDone
{

    private VerticalViewPager viewPager;
    private ViewPagerAdapter viewAdapter;
    IFirestoreLoadDone iFirestoreLoadDone;
    CollectionReference news;
    SwipeRefreshLayout SR;
    View topview;
    int id,ci;
    boolean isup;
    List<News> _news = new ArrayList<>();
   loadingdialog loadingdialog;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {


     loadingdialog =new loadingdialog(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

   showloadingdialog();



    FirebaseMessaging.getInstance().subscribeToTopic("General")
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        String msg = "Sucessfull";
                        if (!task.isSuccessful()) {
                            msg = "Failed";
                        }

                    }
                });


   // MyAsyncTask myAsyncTask = new MyAsyncTask(MainActivity.this);
 //   myAsyncTask.execute();



        SR=(SwipeRefreshLayout)findViewById(R.id.swiperefreshlayout);
        topview=(LinearLayout)findViewById(R.id.topbar);
        topview.setVisibility(View.INVISIBLE);
        isup=false;

        iFirestoreLoadDone= this;
          news = FirebaseFirestore.getInstance().collection("News");

        viewPager=(VerticalViewPager)findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(10);



    getData();

        SR.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
//               update();
//
//            SR.setRefreshing(false);

              new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                       update();

                        SR.setRefreshing(false);
                    }
                },1800);

             //   Toast.makeText(MainActivity.this,String.format("ID: %d,CI: %d",id,ci),Toast.LENGTH_SHORT).show();


            }

        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

                if(isup)
                {
                    slideup(topview);
                    isup=!isup;
                }
            }

            @Override
            public void onPageSelected(int i)
            {
                if(i != 0)
                {
                    SR.setEnabled(false);
                }
                else
                {
                    SR.setEnabled(true);
                }

                // Toast.makeText(MainActivity.this, i, Toast.LENGTH_SHORT).show();
            }



            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

   private void showloadingdialog()
    {

        loadingdialog.showdialog();

      /*  final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                loadingdialog.hidedialog();
            }
        }, 5000);*/
    };

   private void hideladingdialog()
    {
        loadingdialog.hidedialog();
    }


    private  void update()
            {

                News n1 = _news.get(0);

                ci = n1.getId();


        Query query = news.orderBy("id", Query.Direction.DESCENDING).limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for (QueryDocumentSnapshot idsnapshot:task.getResult())
                    {
                         //id = (int) idsnapshot.get("id");
                        id = Integer.parseInt(idsnapshot.getId());

                          if(id ==ci)
                             {
                                Toast.makeText(MainActivity.this,"UptoDate!",Toast.LENGTH_SHORT).show();
                             }
                          else
                            {
                                _news.clear();
                              //  Toast.makeText(MainActivity.this,"Need to Update!"+id,Toast.LENGTH_SHORT).show();
                                getData();
                            }
                   }
                }
            }
        });
    }



    private void getData()
    {
        news.orderBy("id", Query.Direction.DESCENDING).limit(80).
                get()
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        iFirestoreLoadDone.onFireStoreLoadFailed(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if(task.isSuccessful())
                        {

                            for (QueryDocumentSnapshot newsSnapshot:task.getResult())
                            {
                                News news =newsSnapshot.toObject(News.class);
                                _news.add(news);

                            }
                        iFirestoreLoadDone.onFireStoreLoadSucess(_news);

                         }
                    }
                });

    }
    @Override
    public void onFireStoreLoadSucess(List<News> news)
    {
        viewAdapter = new ViewPagerAdapter(this,news);
         viewPager.setAdapter(viewAdapter);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                hideladingdialog();
            }
        }, 1000);



         if(getIntent().getExtras()!=null)
         {
             Bundle b = getIntent().getExtras();
             int notinewsid = b.getInt("newsid");
             News n2 = _news.get(0);

            int c2 = n2.getId();

             viewPager.setCurrentItem(c2-notinewsid);
         }
    }

    @Override
    public void onFireStoreLoadFailed(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public void screenTapped(View view)
    {
       if(isup)
       {
           slideup(topview);
       }
       else
       {
           slidedown(topview);
       }
       isup=!isup;

    }

    private void slidedown(View topview)
    {
        topview.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,0,-topview.getHeight(),0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        topview.startAnimation(animate);
    }

    private void slideup(View topview)
    {
        TranslateAnimation animate = new TranslateAnimation(
                0,0,0,-topview.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        topview.startAnimation(animate);

    }

    public void topbuttonclicked(View view)
    {
    viewPager.setCurrentItem(0,true);
    }

    public void settingbuttonclicked(View view)
    {
        startActivity(new Intent(this,settingpgactivity.class));
    }

   /* public void showsplash()
    {
        final View dialogview = View.inflate(this,R.layout.activity_splash_screen,null);
        final Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogview);

         ImageView imageView = (ImageView)dialog.findViewById(R.id.splashgif);

             Glide.with(this).load(R.drawable.splashscreengif2).into(imageView);
              dialog.setCancelable(true);
              dialog.show();

       final Handler handler  = new Handler();
       final Runnable runnable = new Runnable() {
           @Override
           public void run() {
                {
                    dialog.dismiss();
               }
         }
        };
       handler.postDelayed(runnable, 5000);
    }
*/




    public void shareclicked(View view)
    {
        View view2 =(View)viewPager.findViewWithTag("myview"+viewPager.getCurrentItem());
        Bitmap screenshot = ViewShot(view2);
         shareImage(screenshot);
    }



    public Bitmap ViewShot(View v)
    {
        int height = v.getHeight();
        int width = v.getWidth();
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas (b);
        v.layout(0, 0 , v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }


    private void shareImage(Bitmap bitmap){
        // save bitmap to cache directory
        try {
            File cachePath = new File(this.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        File imagePath = new File(this.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Sample Text while Saving.....");
            shareIntent.setType("image/png");
            startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
     viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
        viewPager.setCurrentItem(viewPager.getCurrentItem()-1);

    }





 class MyAsyncTask extends AsyncTask<Void,Void,Void>
    {
        private final AppCompatActivity activity;
        private Context context;
       // Dialog dialog;
       // View dialogview;
       loadingdialog loadingdialog;


        public MyAsyncTask(AppCompatActivity activity) {
           this.activity=activity;
            loadingdialog =new loadingdialog(activity);

           /* dialogview = View.inflate(activity,R.layout.activity_splash_screen,null);

            dialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(dialogview);

            ImageView imageView = (ImageView)dialog.findViewById(R.id.splashgif);

            Glide.with(activity).load(R.drawable.splashscreengif2).into(imageView);
            dialog.setCancelable(true);
            Log.v("async const","Inconstruction");*/
        }


        @Override
        protected void onPreExecute()
        {
             // Toast.makeText(context,"Preexecute",Toast.LENGTH_LONG);
             //  dialog.show();
            loadingdialog.showdialog();
            super.onPreExecute();


        }


        @Override
        protected void onPostExecute(Void aVoid)
        {
         loadingdialog.hidedialog();
           super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            news = FirebaseFirestore.getInstance().collection("News");

            news.orderBy("id", Query.Direction.DESCENDING).limit(80).
                    get()
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            iFirestoreLoadDone.onFireStoreLoadFailed(e.getMessage());
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task)
                        {
                            if(task.isSuccessful())
                            {



                                for (QueryDocumentSnapshot newsSnapshot:task.getResult())
                                {
                                    News news =newsSnapshot.toObject(News.class);
                                    _news.add(news);



                                }

                                iFirestoreLoadDone.onFireStoreLoadSucess(_news);


                            }
                        }
                    });
            return null;
        }
    }

}

