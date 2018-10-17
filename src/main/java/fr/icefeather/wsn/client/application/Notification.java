package fr.icefeather.wsn.client.application;

import com.sun.net.httpserver.Headers;

import java.text.SimpleDateFormat;
import java.util.*;

public class Notification {
    public Date date;
    public String message;
    public Headers headers;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public Notification(){
        this.date = new Date();
    }

    public Notification(String message, Headers headers){
        this.date = new Date();
        this.message = message;
        this.headers = headers;
    }

    public String getDate(){
        return dateFormat.format(date);
    }

    public Headers getHeaders() {
        return headers;
    }

    public List<Map.Entry> getHeadersList() {
        List<Map.Entry> headersList = new ArrayList<>();
        Iterator headersIter = headers.entrySet().iterator();
        while (headersIter.hasNext()) {
            Map.Entry entry = (Map.Entry)headersIter.next();
            headersList.add(entry);
        }
        return headersList;
    }
}
