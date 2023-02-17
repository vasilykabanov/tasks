package com.example.tasks.tasks;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PinActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences("TaskPrefs", Activity.MODE_PRIVATE);
        Boolean hasPin = settings.getBoolean("hasPin", false);
        if (hasPin) {
            setContentView(R.layout.activity_pin);
            Button clickButton = (Button) findViewById(R.id.buttonPin);
            clickButton.setOnClickListener(this);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonPin:
                SharedPreferences settings = getSharedPreferences("TaskPrefs", Activity.MODE_PRIVATE);
                String pin = settings.getString("pin", "123456789");
                EditText pinEdit = (EditText) findViewById(R.id.editText);
                String enteredPin = pinEdit.getText().toString();
                if (pin.equals(enteredPin)) {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }
}
