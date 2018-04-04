package com.pyrooka.logunlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.Calendar;

public class UnlockBroadcastReceiver extends BroadcastReceiver {

    // Have  to store the instance, to make a callback.
    private MainActivity main;

    public UnlockBroadcastReceiver(MainActivity main) {
        this.main = main;
    }

    @Override
    public void onReceive(Context c, Intent i) {
        // We only interested in device unlock events.
        if (i.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            // Try to write the date to a file on the internal storage.
            try {
                String content = Calendar.getInstance().getTime().toString() + '\n';

                FileOutputStream fos = c.openFileOutput("unlocklog.txt", Context.MODE_APPEND);
                fos.write(content.getBytes());
                fos.close();

            } catch (Exception e) {
                Toast.makeText(c, "Error in broadcast. " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            // Refresh the log list.
            main.displayLogs();
        }
    }
}
