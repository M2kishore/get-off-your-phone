 package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

 public class MainActivity extends AppCompatActivity {
    // The reference variables for the
    // Button, AlertDialog, EditText
    // classes are create

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        final Button breakTime = findViewById(R.id.break_time_button);
        final Button timeOfTheDay = findViewById(R.id.time_of_the_day_button);
        final Button appSpecific = findViewById(R.id.app_specific_button);
        breakTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MainActivity","coming here");
                Intent intent = new Intent(MainActivity.this, BreakTime.class);
                startActivity(intent);
            }
        });
        timeOfTheDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TimeOfTheDay.class);
                startActivity(intent);
            }
        });
        appSpecific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BreakTime.class);
                startActivity(intent);
            }
        });

    }
}