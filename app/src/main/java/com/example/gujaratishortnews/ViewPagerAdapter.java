package com.example.gujaratishortnews;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
//import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.List;

import static android.support.v4.view.ViewPager.OnPageChangeListener.*;

public class ViewPagerAdapter extends PagerAdapter {

    Context context;
    List<News> newsList;
    LayoutInflater inflater;

    TextView heading,detail,date;
    ImageView image;
boolean showimage;
    SharedPreferences pref;


    public ViewPagerAdapter(Context context, List<News> newsList) {
        this.context = context;
        this.newsList = newsList;
        inflater=LayoutInflater.from(context);

        pref=context.getSharedPreferences("Mypref",0);
        showimage=pref.getBoolean("imageenable",true);


    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view==o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager)container).removeView((View)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.fragment_child,container,false);
        image=(ImageView)view.findViewById(R.id.image1) ;
        heading=(TextView)view.findViewById(R.id.heading);
        detail=(TextView)view.findViewById(R.id.detail);
        date=(TextView)view.findViewById(R.id.date);

        String url = newsList.get(position).getImageurl();

        view.setTag("myview"+position);

      // Picasso.get().load(url).into(image);

       if(showimage)
       {
           Glide.with(context).load(url).placeholder(R.drawable.placeholderimage).into(image);
       }else
       {
           Glide.with(context).load(R.drawable.placeholderimage).placeholder(R.drawable.placeholderimage).into(image);
       }

       // Glide.with(context).load(url).placeholder(R.drawable.placeholderimage).into(image);


        heading.setText(newsList.get(position).getHeading());
        detail.setText(newsList.get(position).getDetail());
        date.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(newsList.get(position).getDate()));

        container.addView(view);
        return view;

    }

}
