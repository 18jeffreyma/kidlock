package com.intel.location.ma.app.kidlock.old;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.intel.location.indoor.app.kidlock.R;
import com.intel.location.ma.app.kidlock.FamilyInfo;
import com.intel.location.ma.app.kidlock.KidLock;
import com.intel.location.ma.app.kidlock.a2;

import java.io.File;
import java.io.IOException;

public class checkPassword extends AppCompatActivity {

    File tempFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkpass);
        setupUI(findViewById(R.id.checkpass));
        Button submitPass = (Button) findViewById(R.id.submitCheck);
        submitPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView checkPin = (TextView) findViewById(R.id.checkPin);
                if (checkPin.getText().toString() == null || checkPin.getText().toString().isEmpty()) {
                    android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(checkPassword.this);
                    alert.setTitle("PIN is not valid!");
                    alert.setMessage("Please Enter A Valid PIN Number");
                    alert.setPositiveButton("OK", null);
                    alert.show();

                } else if (checkPin.getText().toString().equals(FamilyInfo.getInstance().getString("pin",""))) {
                    Intent i = new Intent(checkPassword.this, a2.class);
                    FamilyInfo.getInstance().removeLocationData();

                    Intent i2 = new Intent(checkPassword.this, KidLock.class);
                    stopService(i2);

                    tempFile = new File(getBaseContext().getCacheDir(),"KID_LOCK");




                    /** Saving the contents to the file*/
                    try {
                        tempFile.getCanonicalFile().delete();

                        //Toast.makeText(checkPassword.this, "childLocked deleted", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    /** Closing the writer object */



                    startActivity(i);
                    finish();
                } else {
                    android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(checkPassword.this);
                    alert.setTitle("PIN is not correct!");
                    alert.setMessage("Please try again!");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                }


            }
        });
        Button goBack = (Button) findViewById(R.id.retur);
        goBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(checkPassword.this, a2.class);
                i.putExtra("pin",getIntent().getStringExtra("pin"));
                startActivity(i);
                finish();
            }
        });

    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(checkPassword.this);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

}
