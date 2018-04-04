package com.pyrooka.logunlock;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private ListView logList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayLogs();
            }
        });

        logList = (ListView) findViewById(R.id.loglist);

        // Create an intent filter. Only looking for the ACTION_USER_PRESENT intents.
        IntentFilter unlockFilter = new IntentFilter(Intent.ACTION_USER_PRESENT);

        // Register our broadcast receiver.
        registerReceiver(new UnlockBroadcastReceiver(this), unlockFilter);

        // Refresh the log list.
        displayLogs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_clear_logs) {
            // Clear the logs.
            clearLogs();
            // Refresh the log list.
            displayLogs();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh the log list.
        displayLogs();
    }

    public void displayLogs() {
        if (logList == null) {
            logList = (ListView) findViewById(R.id.loglist);
        }

        ArrayList<String> logs = new ArrayList<String>();

        // Try to read the logs from the file.
        try {
            FileInputStream fis = openFileInput("unlocklog.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = br.readLine()) != null) {
                logs.add(line);
            }

            br.close();
            fis.close();
        } catch (Exception e) {
            Toast.makeText(this, "Error! " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Reverse the logs. Last one will be the first.
        Collections.reverse(logs);

        // Set the list to the listview through an array adapter.
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, logs);
        logList.setAdapter(adapter);
    }

    private void clearLogs() {
        // Try to empty the log file.
        try {
            FileOutputStream fos = this.openFileOutput("unlocklog.txt", Context.MODE_PRIVATE);
            fos.write("".getBytes());
            fos.close();
        } catch (Exception e) {
            Toast.makeText(this, "Error while clearing logs. " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
