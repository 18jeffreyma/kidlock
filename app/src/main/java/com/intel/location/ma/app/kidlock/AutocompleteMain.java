package com.intel.location.ma.app.kidlock;

/**
 * Created by majeffrx on 6/23/2016.
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.intel.location.indoor.app.kidlock.R;


public class AutocompleteMain extends AppCompatActivity implements  OnItemClickListener, OnItemSelectedListener  {

    // Initialize variables

    AutoCompleteTextView textView=null;
    private ArrayAdapter<String> adapter;


    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public static final String TAG = "com.intel.kidlock";

    SharedPreferences pref;

    // Store contacts values in these arraylist
    public static ArrayList<String> phoneValueArr = new ArrayList<String>();
    public static ArrayList<String> nameValueArr = new ArrayList<String>();

    EditText toNumber=null;
    String toNumberValue="";
    TextView nameBox;

    BroadcastReceiver receiver;
    Intent intent;

    String kidName;
    String phoneString;

    File tempFile;

    public static final String BROADCAST_ACTION = "com.intel.location.indoor.app.indoorlocationsimple.confirmparent";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.main);

        setupUI(findViewById(R.id.main  ));

        kidName = "";
        phoneString ="";
        final Button Send = (Button) findViewById(R.id.Send);

        // Initialize AutoCompleteTextView values
        ((EditText)findViewById(R.id.toNumber)).getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        textView = (AutoCompleteTextView) findViewById(R.id.toNumber);

        //Create adapter
        adapter = new ArrayAdapter<String>
                (this, R.layout.simple_dropdown_item_2line, new ArrayList<String>());
        textView.setThreshold(1);

        //Set adapter to AutoCompleteTextView
        textView.setAdapter(adapter);
        textView.setOnItemSelectedListener(this);
        textView.setOnItemClickListener(this);
        textView.setText(FamilyInfo.getInstance().getString("phone",""));
        // Read contact data and add data to ArrayAdapter
        // ArrayAdapter used by AutoCompleteTextView

        readContactData();
        ((EditText)findViewById(R.id.childName)).getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        nameBox = (TextView) findViewById(R.id.childName);
        nameBox.setText(FamilyInfo.getInstance().getString("child",""));




        intent = new Intent(BROADCAST_ACTION);
        /********** Button Click pass textView object ***********/
        Send.setOnClickListener(BtnAction(textView));

        addResetButtonListener();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getStringExtra("confirmation").toUpperCase().replaceAll("\\s+$", "").equals("Y") && !phoneString.isEmpty()) {
                    sendConfirmationDone(phoneString,kidName);
                    FamilyInfo.getInstance().setString("phone", phoneString);
                    FamilyInfo.getInstance().setString("child", kidName.replaceAll("\\s+$", ""));
                    FamilyInfo.getInstance().setBoolean("confirmed",true);

                    showConfirmationDoneDialog(kidName);



                }
            }
        };

        checkAndRequestPermissions();



    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(SmsListener.SMS_RESULT)
        );
    }

    private OnClickListener BtnAction(final AutoCompleteTextView toNumber) {
        return new OnClickListener() {

            public void onClick(View v) {

                String NameSel = "";
                NameSel = toNumber.getText().toString();


                final String ToNumber = toNumberValue;
                String regexStr = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$";

                if (ToNumber.length() == 0 ) {
                    TextView phoneBox = (TextView) findViewById(R.id.toNumber);

                    toNumberValue = phoneBox.getText().toString().replaceAll("[^\\p{L}\\p{Nd}]+", "");


                    String name = nameBox.getText().toString();


                    if(toNumberValue.matches(regexStr)) {

                        phoneString = toNumberValue;
                        kidName = name.replaceAll("\\s+$", "");

                        if (phoneString.equals(FamilyInfo.getInstance().getString("phone","")) &&
                                kidName.equals(FamilyInfo.getInstance().getString("child",""))) {
                            Intent startNext = new Intent(AutocompleteMain.this,a2.class);
                            startActivity(startNext);
                            finish();
                        } else {
                            showRequestDialog();
                            sendRequestConfirm(phoneString,kidName);

                            //Add more stuff
                        }


                    }
                    else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(AutocompleteMain.this);
                        alert.setTitle("Not a valid phone number or contact.");
                        alert.setMessage("Please enter valid information.");
                        alert.setPositiveButton("OK",null);
                        alert.show();
                    }

                }
                else if (nameValueArr.indexOf(toNumber.getText().toString())!= -1) {
                    toNumberValue = phoneValueArr.get(nameValueArr.indexOf(toNumber.getText().toString()))
                            .replaceAll("[^\\p{L}\\p{Nd}]+", "");
                    phoneString = toNumberValue;
                    String name = nameBox.getText().toString();

                    kidName = name.replaceAll("\\s+$", "");

                    if (phoneString.equals(FamilyInfo.getInstance().getString("phone","")) &&
                            kidName.equals(FamilyInfo.getInstance().getString("child",""))) {
                        Intent startNext = new Intent(AutocompleteMain.this,a2.class);
                        startActivity(startNext);
                        finish();
                    } else {
                        showRequestDialog();
                        sendRequestConfirm(phoneString,kidName);
                        //Add more stuff
                    }

                }


                else
                {
                    TextView phoneBox = (TextView) findViewById(R.id.toNumber);
                    toNumberValue = phoneBox.getText().toString().replaceAll("[^\\p{L}\\p{Nd}]+", "");

                    String name = nameBox.getText().toString();

                    if(toNumberValue.matches(regexStr)) {
                        phoneString = toNumberValue.replaceAll("[^\\p{L}\\p{Nd}]+", "");;
                        kidName = name.replaceAll("\\s+$", "");

                        if (phoneString.equals(FamilyInfo.getInstance().getString("phone","")) &&
                                kidName.equals(FamilyInfo.getInstance().getString("child",""))) {
                            Intent startNext = new Intent(AutocompleteMain.this,a2.class);
                            startActivity(startNext);
                            finish();
                        } else {

                            showRequestDialog();
                            sendRequestConfirm(phoneString, kidName);
                            //Add more stuff
                        }
                    }

                    else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(AutocompleteMain.this);
                        alert.setTitle("Not a valid phone number or contact.");
                        alert.setMessage("Please enter valid information.");
                        alert.setPositiveButton("OK",null);
                        alert.show();;
                    }

                }

            }
        };
    }


    public void addResetButtonListener(){
        Button reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.support.v7.app.AlertDialog.Builder resetDialog = new android.support.v7.app.AlertDialog.Builder(AutocompleteMain.this);
                resetDialog.setTitle("Confirmation");
                resetDialog.setMessage("Press OK to confirm you would like to clear the app information.");
                resetDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FamilyInfo.getInstance().clear();

                        tempFile = new File(getBaseContext().getCacheDir(),"KID_LOCK");

                        try {
                            tempFile.getCanonicalFile().delete();

                            //Toast.makeText(checkPassword.this, "childLocked deleted", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        nameBox.setText("");
                        TextView phoneBox = (TextView) findViewById(R.id.toNumber);
                        phoneBox.setText("");

                    }
                });
                resetDialog.setNegativeButton("Cancel",null);
                resetDialog.show();

            }
        });
    }

    public void showRequestDialog() {
        final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(AutocompleteMain.this);
        confirmDialog.setTitle("Confirm Your Phone Number");
        confirmDialog.setMessage("Instructions have been texted to the parent phone number.");
        confirmDialog.setNegativeButton("Close",null);
        confirmDialog.show();
    }

    public void sendRequestConfirm(String number, String kidName ) {
        String message = "Your phone has been entered as the emergency contact for " +
                kidName.replaceAll("\\s+$", "").toUpperCase() + ". Reply 'Y' to confirm.";
        SmsManager sm = SmsManager.getDefault();
        sm.sendTextMessage(number, null, message, null, null);
    }

    public void sendConfirmationDone(String number, String kidName) {

        String message = "You are confirmed as the emergency contact for " + kidName.replaceAll("\\s+$", "").toUpperCase() +".";
        SmsManager sm = SmsManager.getDefault();
        sm.sendTextMessage(number, null, message, null, null);
    }

    public void showConfirmationDoneDialog(String kidName) {
        final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(AutocompleteMain.this);
        FamilyInfo.getInstance().setBoolean("confirmed",true);
        confirmDialog.setTitle("Emergency Contact Confirmed");
        confirmDialog.setMessage("You have confirmed as the emergency contact of " + kidName.replaceAll("\\s+$", "").toUpperCase() );
        confirmDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {
                Intent i2 = new Intent(AutocompleteMain.this, a2.class);
                startActivity(i2);
                finish();
            }
        });
        confirmDialog.show();
    }

    // Read phone contact name and phone numbers

    private void readContactData() {

        try {

            /*********** Reading Contacts Name And Number **********/

            String phoneNumber = "";
            ContentResolver cr = getBaseContext()
                    .getContentResolver();

            //Query to get contact name

            Cursor cur = cr
                    .query(ContactsContract.Contacts.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

            // If data data found in contacts
            if (cur.getCount() > 0) {

                Log.i("AutocompleteContacts", "Reading   contacts........");

                int k=0;
                String name = "";

                while (cur.moveToNext())
                {

                    String id = cur
                            .getString(cur
                                    .getColumnIndex(ContactsContract.Contacts._ID));
                    name = cur
                            .getString(cur
                                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    //Check contact have phone number
                    if (Integer
                            .parseInt(cur
                                    .getString(cur
                                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                    {

                        //Create query to get phone number by contact id
                        Cursor pCur = cr
                                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                + " = ?",
                                        new String[] { id },
                                        null);
                        int j=0;

                        while (pCur
                                .moveToNext())
                        {
                            // Sometimes get multiple data
                            if(j==0)
                            {
                                // Get Phone number
                                phoneNumber =""+pCur.getString(pCur
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                // Add contacts names to adapter
                                adapter.add(name);

                                // Add ArrayList names to adapter
                                phoneValueArr.add(phoneNumber.toString());
                                nameValueArr.add(name.toString());

                                j++;
                                k++;
                            }
                        }  // End while loop
                        pCur.close();
                    } // End if

                }  // End while loop

            } // End Cursor value check
            cur.close();


        } catch (Exception e) {
            Log.i("AutocompleteContacts","Exception : "+ e);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
        // TODO Auto-generated method stub
        //Log.d("AutocompleteContacts", "onItemSelected() position " + position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

        InputMethodManager imm = (InputMethodManager) getSystemService(
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

        // Get Array index value for selected name
        int i = nameValueArr.indexOf(""+arg0.getItemAtPosition(arg2));

        // If name exist in name ArrayList
        if (i >= 0) {

            // Get Phone Number
            toNumberValue = phoneValueArr.get(i);
            String phoneString = toNumberValue.toString();

            InputMethodManager imm = (InputMethodManager) getSystemService(
                    INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);


        }

    }

    protected void onResume() {

        super.onResume();
        setContentView(R.layout.main);

        setupUI(findViewById(R.id.main  ));

        kidName = "";
        phoneString ="";
        final Button Send = (Button) findViewById(R.id.Send);

        // Initialize AutoCompleteTextView values

        textView = (AutoCompleteTextView) findViewById(R.id.toNumber);

        //Create adapter
        adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        textView.setThreshold(1);

        //Set adapter to AutoCompleteTextView
        textView.setAdapter(adapter);
        textView.setOnItemSelectedListener(this);
        textView.setOnItemClickListener(this);
        textView.setText(FamilyInfo.getInstance().getString("phone",""));
        // Read contact data and add data to ArrayAdapter
        // ArrayAdapter used by AutoCompleteTextView

        readContactData();

        nameBox = (EditText) findViewById(R.id.childName);
        nameBox.setText(FamilyInfo.getInstance().getString("child",""));

        intent = new Intent(BROADCAST_ACTION);
        /********** Button Click pass textView object ***********/
        Send.setOnClickListener(BtnAction(textView));

        addResetButtonListener();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getStringExtra("confirmation").toUpperCase().replaceAll("\\s+$", "").equals("Y") && !phoneString.isEmpty()) {
                    sendConfirmationDone(phoneString,kidName);
                    FamilyInfo.getInstance().setString("phone", phoneString);
                    FamilyInfo.getInstance().setString("child", kidName.replaceAll("\\s+$", ""));
                    FamilyInfo.getInstance().setBoolean("confirmed",true);

                    showConfirmationDoneDialog(kidName);



                }
            }
        };

    }

    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        finish();
    }

    protected void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void showSoftKeyboard(View view){
        if(view.requestFocus()){
            InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(AutocompleteMain.this);
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

    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int contactPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int phonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);

        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }

        if (contactPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }

        if (phonePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;

    }




}
