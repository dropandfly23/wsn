package com.sncf.marlier.wsn.client.application;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Notification {
    public Date date;
    public String message;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public Notification(){
        this.date = new Date();
    }

    public Notification(String message){
        this.date = new Date();
        this.message = message;
    }

    public String getDate(){
        return dateFormat.format(date);
    }
}
