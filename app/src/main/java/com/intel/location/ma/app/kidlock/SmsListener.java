package com.intel.location.ma.app.kidlock;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsListener extends BroadcastReceiver {



    static final public String SMS_RESULT = "com.intel.location.indoor.app.indoorlocationsimple.SMS_PROCESSED";

    static final public String SMS_MESSAGE = "com.intel.location.indoor.app.indoorlocationsimple.SMS_MSG";

    // Get the object of SmsManager
    final SmsManager smsListen = SmsManager.getDefault();
    LocalBroadcastManager smsListenManager;

    String message;

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        smsListenManager = LocalBroadcastManager.getInstance(context);
        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    message = currentMessage.getDisplayMessageBody();

                } // end for loop
                Intent i2 = new Intent(SMS_RESULT);
                i2.putExtra("confirmation", message);

                smsListenManager.sendBroadcast(i2);
                //Toast.makeText(context, "SMS Brodcasted with message: " + message, Toast.LENGTH_LONG).show();
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }
}
