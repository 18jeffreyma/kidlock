package com.intel.location.ma.app.kidlock;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import android.widget.CompoundButton;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.view.View;

import com.intel.location.indoor.app.kidlock.R;
import com.intel.location.ma.app.kidlock.old.checkPassword;
import com.intel.location.ma.app.kidlock.old.setPassword;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class a2 extends AppCompatActivity {
    private static final String TAG = a2.class.getSimpleName();
    File tempFile;

    String pin;
    BroadcastReceiver receiver;

    Button goBack;

    TextView latBox;
    TextView lonBox;

    ToggleButton toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p2);

        goBack = (Button) findViewById(R.id.goBack);
        goBack.setVisibility(View.VISIBLE);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(a2.this,AutocompleteMain.class);
                startActivity(i2);
                finish();
            }
        });

        if(FamilyInfo.getInstance().contains("pin")) {
            goBack.setVisibility(View.GONE);
            goBack.setOnClickListener(null);
        }




        addToggleButton();
        /* String childBox = "Child Name:<br> <b>" + FamilyInfo.getInstance().getString("child","") + "</b>";
        ((TextView)findViewById(R.id.childName)).setText(Html.fromHtml(childBox));
        String p = FamilyInfo.getInstance().getString("phone","");
        String formatedNum = "+" +p.substring(0,p.length()-10)+" (" +
                p.substring(p.length()-10,p.length()-7) + ") " + p.substring(p.length()-7,p.length()-4) + "-"
                + p.substring(p.length()-4);
        String formatedHtml = "Phone Number:<br><b>" +formatedNum + "</b>";
        ((TextView)findViewById(R.id.parentNum)).setText(Html.fromHtml(formatedHtml));
        */

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();

        setContentView(R.layout.p2);

        goBack = (Button) findViewById(R.id.goBack);
        goBack.setVisibility(View.VISIBLE);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(a2.this,AutocompleteMain.class);
                startActivity(i2);
                finish();
            }
        });

        if(FamilyInfo.getInstance().contains("pin")) {
            goBack.setVisibility(View.GONE);
            goBack.setOnClickListener(null);
        }




        addToggleButton();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private CompoundButton.OnCheckedChangeListener DialogAction() {
        return new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    setPassword();
                } else {

                    checkPassword();
                }
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener ActivityAction() {
        return new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(a2.this,setPassword.class);

                    startActivity(intent);
                    finish();


                } else {
                    Intent intent = new Intent(a2.this,checkPassword.class);

                    startActivity(intent);
                    finish();


                }
            }
        };
    }


    public void addToggleButton() {
        final Context context = this;
        toggle = (ToggleButton) findViewById(R.id.toggleButton);
        if (!FamilyInfo.getInstance().contains("pin")) {
            toggle.setOnClickListener(null);
            toggle.setChecked(false);

        } else {
            toggle.setOnClickListener(null);
            toggle.setChecked(true);

        }

        toggle.setOnCheckedChangeListener(DialogAction());
    }

    public void setPassword() {
        final AlertDialog.Builder pinAuth = new AlertDialog.Builder(this);
        pinAuth.setTitle("PIN required");
        pinAuth.setMessage("Please enter your pin to lock child.");

        final EditText pinNum = new EditText(this);
        pinNum.setHint("PIN");
        pinNum.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        pinNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        pinNum.setGravity(Gravity.CENTER);


        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(300, 0, 300, 0);
        layout.addView(pinNum, params);
        pinAuth.setView(layout);
        pinAuth.setCancelable(false);

        // These checks seem to work ok.
        pinAuth.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pinNum.getWindowToken(), 0);

                if (pinNum.getText().toString().isEmpty()) {
                    makeToast("Sorry, no pin was entered.", Toast.LENGTH_SHORT);
                    toggle.setOnCheckedChangeListener(null);
                    toggle.setChecked(false);
                    toggle.setOnCheckedChangeListener(DialogAction());



                    goBack.setVisibility(View.VISIBLE);
                    goBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i2 = new Intent(a2.this,AutocompleteMain.class);
                            startActivity(i2);
                            finish();
                        }
                    });
                } else {
                    pin = pinNum.getText().toString();
                    confirmPassword();
                }
            }
        });
        pinAuth.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                toggle.setOnCheckedChangeListener(null);
                toggle.setChecked(false);
                toggle.setOnCheckedChangeListener(DialogAction());

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pinNum.getWindowToken(), 0);

                goBack.setVisibility(View.VISIBLE);
                goBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i2 = new Intent(a2.this,AutocompleteMain.class);
                        startActivity(i2);
                        finish();
                    }
                });
            }
        });

        (new Handler()).postDelayed(new Runnable() {

            public void run() {
//              ((EditText) findViewById(R.id.et_find)).requestFocus();
//
//              EditText yourEditText= (EditText) findViewById(R.id.et_find);
//              InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//              imm.showSoftInput(yourEditText, InputMethodManager.SHOW_IMPLICIT);

                pinNum.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
                pinNum.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));

            }
        }, 50);

        // Show the AlertDialog.
        pinAuth.show();


    }

    public void confirmPassword(){
        final AlertDialog.Builder pinAuth = new AlertDialog.Builder(this);
        pinAuth.setTitle("Confirm PIN");
        pinAuth.setMessage("Please enter your pin again to confirm.");
        pinAuth.setCancelable(false);
        final EditText pinNum = new EditText(this);
        pinNum.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        pinNum.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});
        pinNum.setHint("PIN");

        pinNum.setGravity(Gravity.CENTER);



        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(300, 0, 300, 0);
        layout.addView(pinNum, params);
        pinAuth.setView(layout);
        pinAuth.setCancelable(false);


        // These checks seem to work ok.
        pinAuth.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pinNum.getWindowToken(), 0);

                if(pinNum.getText().toString().equals(pin))
                {
                    toggle.setOnCheckedChangeListener(null);
                    toggle.setChecked(true);
                    toggle.setOnCheckedChangeListener(DialogAction());
                    FamilyInfo.getInstance().setString("pin", pin);

                    Intent i2 = new Intent(a2.this, KidLock.class);

                    i2.putExtra("child",FamilyInfo.getInstance().getString("child",""));
                    i2.putExtra("phone",FamilyInfo.getInstance().getString("phone",""));

                    if(FamilyInfo.getInstance().contains("lockedLocation")){
                        i2.putExtra("lockedLocation",FamilyInfo.getInstance().getLocation("lockedLocation",null));
                    }

                    Intent startForegroundIntent = new Intent(KidLock.STARTFOREGROUND_ACTION);
                    startForegroundIntent.setClass(a2.this,KidLock.class);
                    startService(startForegroundIntent);

                    startService(i2);

                    goBack.setVisibility(View.GONE);


                    tempFile = new File(a2.this.getCacheDir(), "KID_LOCK");

                    FileWriter writer = null;


                    //Saving the contents to the file
                    try {
                        writer = new FileWriter(tempFile);
                        writer.write("childLocked");
                        writer.close();

                        //Toast.makeText(setPassword.this, "childLocked written", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                else {
                    makeToast("Sorry, pin is incorrect.", Toast.LENGTH_SHORT);
                    toggle.setOnCheckedChangeListener(null);
                    toggle.setChecked(false);
                    toggle.setOnCheckedChangeListener(DialogAction());

                    goBack.setVisibility(View.VISIBLE);
                    goBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i2 = new Intent(a2.this,AutocompleteMain.class);
                            startActivity(i2);
                            finish();
                        }
                    });
                }
            }
        });

        pinAuth.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                toggle.setOnCheckedChangeListener(null);
                toggle.setChecked(false);
                toggle.setOnCheckedChangeListener(DialogAction());

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pinNum.getWindowToken(), 0);

                goBack.setVisibility(View.VISIBLE);
                goBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i2 = new Intent(a2.this,AutocompleteMain.class);
                        startActivity(i2);
                        finish();
                    }
                });
            }
        });

        (new Handler()).postDelayed(new Runnable() {

            public void run() {
//              ((EditText) findViewById(R.id.et_find)).requestFocus();
//
//              EditText yourEditText= (EditText) findViewById(R.id.et_find);
//              InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//              imm.showSoftInput(yourEditText, InputMethodManager.SHOW_IMPLICIT);

                pinNum.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
                pinNum.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));

            }
        }, 50);

        // Show the AlertDialog.
        pinAuth.show();

    }


    public void checkPassword(){
        final AlertDialog.Builder pinAuth = new AlertDialog.Builder(this);
        pinAuth.setTitle("Enter PIN");
        pinAuth.setMessage("Please enter your pin to unlock child.");
        pinAuth.setCancelable(false);
        final EditText pinNum = new EditText(this);
        pinNum.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        pinNum.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});
        pinNum.setHint("PIN");

        pinNum.setGravity(Gravity.CENTER);



        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(300, 0, 300, 0);
        layout.addView(pinNum, params);
        pinAuth.setView(layout);
        pinAuth.setCancelable(false);


        // These checks seem to work ok.
        pinAuth.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pinNum.getWindowToken(), 0);

                if(pinNum.getText().toString().equals(FamilyInfo.getInstance().getString("pin","")))
                {
                    toggle.setOnCheckedChangeListener(null);
                    toggle.setChecked(false);
                    toggle.setOnCheckedChangeListener(DialogAction());


                    FamilyInfo.getInstance().removeLocationData();

                    Intent i2 = new Intent(a2.this, KidLock.class);
                    stopService(i2);

                    goBack.setVisibility(View.VISIBLE);
                    goBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i2 = new Intent(a2.this,AutocompleteMain.class);
                            startActivity(i2);
                            finish();
                        }
                    });


                    tempFile = new File(getBaseContext().getCacheDir(),"KID_LOCK");

                    // Saving the contents to the file
                    try {
                        tempFile.getCanonicalFile().delete();

                        //Toast.makeText(checkPassword.this, "childLocked deleted", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                else {
                    makeToast("Sorry, pin is incorrect.", Toast.LENGTH_SHORT);
                    toggle.setOnCheckedChangeListener(null);
                    toggle.setChecked(true);
                    toggle.setOnCheckedChangeListener(DialogAction());

                    goBack.setVisibility(View.GONE);

                }
            }
        });

        pinAuth.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                toggle.setOnCheckedChangeListener(null);
                toggle.setChecked(true);
                toggle.setOnCheckedChangeListener(DialogAction());

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pinNum.getWindowToken(), 0);

            }
        });

        (new Handler()).postDelayed(new Runnable() {

            public void run() {
//              ((EditText) findViewById(R.id.et_find)).requestFocus();
//
//              EditText yourEditText= (EditText) findViewById(R.id.et_find);
//              InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//              imm.showSoftInput(yourEditText, InputMethodManager.SHOW_IMPLICIT);

                pinNum.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN , 0, 0, 0));
                pinNum.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP , 0, 0, 0));

            }
        }, 50);

        // Show the AlertDialog.
        pinAuth.show();

    }


    public void makeToast(String s, int length){
        Toast.makeText(getApplicationContext(), s, length).show();
    }

}