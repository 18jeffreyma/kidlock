package com.intel.location.ma.app.kidlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class BootReciever extends BroadcastReceiver {
    String strLine;
    File tempFile;

    public BootReciever() {
        strLine = "";

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            File cDir = context.getCacheDir();
            tempFile = new File(cDir.getPath(), "KID_LOCK");

            FileReader fReader = new FileReader(tempFile);
            BufferedReader bReader = new BufferedReader(fReader);
            if (bReader.readLine() != null){
                strLine = bReader.readLine();
                //Toast.makeText(context, "strline read", Toast.LENGTH_LONG).show();
                Intent i = new Intent(context,loadingScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        } catch (FileNotFoundException e) {
            //Toast.makeText(context, "File not found", Toast.LENGTH_LONG).show();
            e.printStackTrace();

        } catch (IOException io) {
            //Toast.makeText(context, "IO exception", Toast.LENGTH_LONG).show();
            io.printStackTrace();
        }




    }
}