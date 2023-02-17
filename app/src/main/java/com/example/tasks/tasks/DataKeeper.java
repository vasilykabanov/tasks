package com.example.tasks.tasks;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DataKeeper {

    public static final int ST_OPEN = 0;
    public static final int ST_DONE = 1;

    public static Date selectedDate;
    private ArrayList<DataModel> data;
    private ArrayList<DataModel> actualData;
    private Context context;
    public static int state = ST_OPEN;
    public static int editId = -1;

    public DataKeeper(Context context) {
        this.context = context;

        data = new ArrayList<DataModel>();
        String content = "";
        try {
            content = readFromFile();
        } catch (FileNotFoundException e) {
            /* Do Nothing */
        }

        String[] objects = content.split("\\$");
        for (String objstr : objects) {
            String[] model = objstr.split("\\|");
            if (model.length == 6) {
                DataModel dm = new DataModel(model);
                data.add(dm);
            } else {
                Log.e("TASKS", "Bad data in datafile: " + model.toString());
            }
        }

        if (data.size() == 0) {
            addCalendar();
        }
        updateActualWithState();
    }

    private void updateActualWithState() {
        actualData = new ArrayList<DataModel>();

        for (DataModel obj : data) {
            if (obj.title.equals("Calendar Place")) {
                actualData.add(obj);
                continue;
            }

            if (state == ST_OPEN) {
                if (!obj.done) {
                    actualData.add(obj);
                }
            }
            if (state == ST_DONE) {
                if (obj.done) {
                    actualData.add(obj);
                }
            }
        }
    }

    private void addCalendar() {
        data.add(new DataModel(
                "Calendar Place",
                "Doesn't display",
                new Date(),
                1,
                false,
                false
        ));
    }

    public ArrayList<DataModel> getData() {
        return actualData;
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("data.dat", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() throws FileNotFoundException {

        String ret = "";
        try {
            InputStream inputStream = context.openFileInput("data.dat");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (IOException e) {
            Log.d("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public void add(DataModel dataModel) {
        data.add(dataModel);
        updateActualWithState();
        saveAllInFile();
    }

    private void saveAllInFile() {
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
        String saveData = "";
        for (DataModel elem : data) {
            saveData += elem.title + "|";
            saveData += simpleDate.format(elem.date) + "|";
            saveData += elem.description + "|";
            saveData += elem.priority + "|";
            saveData += elem.notification + "|";
            saveData += elem.done;
            saveData += "$\n";
        }
        writeToFile(saveData);
    }

    public DataModel get(int listPosition) {
        return actualData.get(listPosition);
    }

    public void remove(int listPosition) {
        DataModel toDelItem = actualData.get(listPosition);
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) == toDelItem) {
                data.remove(i);
            }
        }
        updateActualWithState();
        saveAllInFile();
    }

    public int size() {
        return actualData.size();
    }

    public void clearAll() {
        data = new ArrayList<DataModel>();
        actualData = new ArrayList<DataModel>();
        addCalendar();
        updateActualWithState();
        saveAllInFile();
    }

    public void setDone(int index) {
        actualData.get(index).done = true;
        updateActualWithState();
        saveAllInFile();
    }

    public void update() {
        updateActualWithState();
    }

    public void toggleDone(int index) {
        actualData.get(index).done = !actualData.get(index).done;
        updateActualWithState();
        saveAllInFile();
    }

    public void editItem(int index, String title, String date, String desc, Integer priority, boolean notify, boolean b) {
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
        Date dt = new Date();
        try {
            dt = simpleDate.parse(date);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DataModel item = actualData.get(index);
        item.title = title;
        item.date = dt;//date;
        item.description = desc;
        item.priority = priority;
        item.notification = notify;
        updateActualWithState();
        saveAllInFile();
    }
}
