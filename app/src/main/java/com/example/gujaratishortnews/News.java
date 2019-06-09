package com.example.gujaratishortnews;

import java.util.Date;

public class News {

    private String heading,detail,imageurl;
    private Date date;
    private int id;


    public News() {
    }

    public News(String heading, String detail, String imageurl, Date date) {
        this.heading = heading;
        this.detail = detail;
        this.imageurl = imageurl;
        this.date = date;


    }

    public String getHeading() {
        return heading;
    }

    public String getDetail() {
        return detail;
    }

    public String getImageurl() {
        return imageurl;
    }

    public Date getDate() {
        return date;
    }

    public int getId() {
        return id;
    }
}

