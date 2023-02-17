package com.example.tasks.tasks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataModel {

    public String title;
    public Date date;
    public String description;
    public Integer priority;
    public Boolean notification;
    public Boolean done;

    public DataModel(String title, String description, Date date, Integer priority, Boolean notification, Boolean done) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.priority = priority;
        this.notification = notification;
        this.done = done;
    }

    public DataModel(String[] modelArray) {
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
        Date dt = new Date();
        try {
            dt = simpleDate.parse(modelArray[1]);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.title = modelArray[0];
        this.date = dt;
        this.description = modelArray[2];
        this.priority = Integer.parseInt(modelArray[3]);
        this.notification = Boolean.parseBoolean(modelArray[4]);
        this.done = Boolean.parseBoolean(modelArray[5]);
    }
}