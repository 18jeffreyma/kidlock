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
import java.io.FileWriter;
import java.io.IOException;

public class setPassword extends AppCompatActivity {

    File tempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setpass);
        setupUI(findViewById(R.id.setpass));
        Button submitPass = (Button) findViewById(R.id.submitPass);
        submitPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView pinNum = (TextView) findViewById(R.id.pinNum);
                if (pinNum.getText().toString() == null || pinNum.getText().toString().isEmpty()) {
                    android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(setPassword.this);
                    alert.setTitle("PIN is not valid!");
                    alert.setMessage("Please Enter A Valid PIN Number");
                    alert.setPositiveButton("OK", null);
                    alert.show();

                } else {
                    String pinNumber = pinNum.getText().toString();
                    Intent i = new Intent(setPassword.this, a2.class);
                    FamilyInfo.getInstance().setString("pin",pinNumber);

                    Intent i2 = new Intent(setPassword.this, KidLock.class);
                    startService(i2);


                    tempFile = new File(setPassword.this.getCacheDir(),"KID_LOCK");

                    FileWriter writer=null;


                    /** Saving the contents to the file*/
                    try {
                        writer = new FileWriter(tempFile);
                        writer.write("childLocked");
                        writer.close();

                        //Toast.makeText(setPassword.this, "childLocked written", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    /** Closing the writer object */



                    startActivity(i);
                    finish();
                }

            }
        });
        Button goBack = (Button) findViewById(R.id.retur);
        goBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(setPassword.this, a2.class);
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
                    hideSoftKeyboard(setPassword.this);
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
