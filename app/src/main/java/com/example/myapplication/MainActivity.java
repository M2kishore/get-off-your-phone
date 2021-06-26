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
import android.text.Editable;
import android.text.TextWatcher;
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
    // classes are created

    private EditText mEditTextInput;
    private TextView mTextViewCountDown;
    private Button mButtonSet;
    private Button mButtonStartPause;
    private Button mButtonReset;

    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;

    private long mStartTimeInMillis;
    private long mTimeLeftInMillis;
    private long mEndTime;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextInput = findViewById(R.id.edit_text_input);
        mTextViewCountDown = findViewById(R.id.text_view_countdown);

        mButtonSet = findViewById(R.id.button_set);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);

        mButtonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = mEditTextInput.getText().toString();
                if (input.length() == 0) {
                    Toast.makeText(MainActivity.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                long millisInput = Long.parseLong(input) * 60000;
                if (millisInput == 0) {
                    Toast.makeText(MainActivity.this, "Please enter a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }

                setTime(millisInput);
                mEditTextInput.setText("");
            }
        });

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
                stopService(new Intent(MainActivity.this, FloatingWindow.class));
            }

        });


        // If the app is started again while the
        // floating window service is running
        // then the floating window service will stop
        //if (isMyServiceRunning()) {
            // onDestroy() method in FloatingWindow
            // class will be called here
        //}

        // currentDesc String will be empty
        // at first time launch
        // but the text written in floating
        // window will not gone

        // The Main Button that helps to minimize the app
        /*minimizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // First it confirms whether the
                // 'Display over other apps' permission in given
                if (checkOverlayDisplayPermission()) {
                    // FloatingWindow service is started
                    startService(new Intent(MainActivity.this, FloatingWindow.class));
                    // The MainActivity closes here
                    finish();
                } else {
                    // If permission is not given,
                    // it shows the AlertDialog box and
                    // redirects to the Settings
                    requestOverlayDisplayPermission();
                }
            }
        });*/
    }
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        // A loop is needed to get Service information that
        // are currently running in the System.
        // So ActivityManager.RunningServiceInfo is used.
        // It helps to retrieve a
        // particular service information, here its this service.
        // getRunningServices() method returns a list of the
        // services that are currently running
        // and MAX_VALUE is 2147483647. So at most this many services
        // can be returned by this method.
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            // If this service is found as a running,
            // it will return true or else false.
            if (FloatingWindow.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void requestOverlayDisplayPermission() {
        // An AlertDialog is created
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // This dialog can be closed, just by taping
        // anywhere outside the dialog-box
        builder.setCancelable(true);

        // The title of the Dialog-box is set
        builder.setTitle("Screen Overlay Permission Needed");

        // The message of the Dialog-box is set
        builder.setMessage("Enable 'Display over other apps' from System Settings.");

        // The event of the Positive-Button is set
        builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The app will redirect to the 'Display over other apps' in Settings.
                // This is an Implicit Intent. This is needed when any Action is needed
                // to perform, here it is
                // redirecting to an other app(Settings).
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));

                // This method will start the intent. It takes two parameter, one is the Intent and the other is
                // an requestCode Integer. Here it is -1.
                startActivityForResult(intent, RESULT_OK);
            }
        });
        dialog = builder.create();
        // The Dialog will
        // show in the screen
        dialog.show();
    }

    private boolean checkOverlayDisplayPermission() {
        // Android Version is lesser than Marshmallow or
        // the API is lesser than 23
        // doesn't need 'Display over other apps' permission enabling.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            // If 'Display over other apps' is not enabled
            // it will return false or else true
            if (!Settings.canDrawOverlays(this)) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
     private void setTime(long milliseconds) {
         mStartTimeInMillis = milliseconds;
         resetTimer();
         closeKeyboard();
     }

     private void startTimer() {
         mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

         mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
             @Override
             public void onTick(long millisUntilFinished) {
                 mTimeLeftInMillis = millisUntilFinished;
                 updateCountDownText();
             }

             @Override
             public void onFinish() {
                 mTimerRunning = false;
                 if (checkOverlayDisplayPermission()) {
                     // FloatingWindow service is started
                     startService(new Intent(MainActivity.this, FloatingWindow.class));
                     // The MainActivity closes here
                     finish();
                 } else {
                     // If permission is not given,
                     // it shows the AlertDialog box and
                     // redirects to the Settings
                     requestOverlayDisplayPermission();
                 }
                 updateWatchInterface();
             }
         }.start();

         mTimerRunning = true;
         updateWatchInterface();
     }

     private void pauseTimer() {
         mCountDownTimer.cancel();
         mTimerRunning = false;
         updateWatchInterface();
     }

     private void resetTimer() {
         mTimeLeftInMillis = mStartTimeInMillis;
         updateCountDownText();
         updateWatchInterface();
     }

     private void updateCountDownText() {
         int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
         int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
         int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

         String timeLeftFormatted;
         if (hours > 0) {
             timeLeftFormatted = String.format(Locale.getDefault(),
                     "%d:%02d:%02d", hours, minutes, seconds);
         } else {
             timeLeftFormatted = String.format(Locale.getDefault(),
                     "%02d:%02d", minutes, seconds);
         }

         mTextViewCountDown.setText(timeLeftFormatted);
     }

     private void updateWatchInterface() {
         if (mTimerRunning) {
             mEditTextInput.setVisibility(View.INVISIBLE);
             mButtonSet.setVisibility(View.INVISIBLE);
             mButtonReset.setVisibility(View.INVISIBLE);
             mButtonStartPause.setText("Pause");
         } else {
             mEditTextInput.setVisibility(View.VISIBLE);
             mButtonSet.setVisibility(View.VISIBLE);
             mButtonStartPause.setText("Start");

             if (mTimeLeftInMillis < 1000) {
                 mButtonStartPause.setVisibility(View.INVISIBLE);
             } else {
                 mButtonStartPause.setVisibility(View.VISIBLE);
             }

             if (mTimeLeftInMillis < mStartTimeInMillis) {
                 mButtonReset.setVisibility(View.VISIBLE);
             } else {
                 mButtonReset.setVisibility(View.INVISIBLE);
             }
         }
     }

     private void closeKeyboard() {
         View view = this.getCurrentFocus();
         if (view != null) {
             InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
             imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
         }
     }

     @Override
     protected void onStop() {
         super.onStop();

         SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
         SharedPreferences.Editor editor = prefs.edit();

         editor.putLong("startTimeInMillis", mStartTimeInMillis);
         editor.putLong("millisLeft", mTimeLeftInMillis);
         editor.putBoolean("timerRunning", mTimerRunning);
         editor.putLong("endTime", mEndTime);

         editor.apply();

         if (mCountDownTimer != null) {
             mCountDownTimer.cancel();
         }
     }

     @Override
     protected void onStart() {
         super.onStart();

         SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

         mStartTimeInMillis = prefs.getLong("startTimeInMillis", 600000);
         mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis);
         mTimerRunning = prefs.getBoolean("timerRunning", false);

         updateCountDownText();
         updateWatchInterface();

         if (mTimerRunning) {
             mEndTime = prefs.getLong("endTime", 0);
             mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

             if (mTimeLeftInMillis < 0) {
                 mTimeLeftInMillis = 0;
                 mTimerRunning = false;
                 updateCountDownText();
                 updateWatchInterface();
             } else {
                 startTimer();
             }
         }
     }
}