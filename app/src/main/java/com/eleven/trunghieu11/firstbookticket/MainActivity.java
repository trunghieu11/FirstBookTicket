package com.eleven.trunghieu11.firstbookticket;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText urlEditText;
    private EditText termEditText;
    private EditText startTermEditText;
    private EditText endTermEditText;

    private Button startButton;
    private Button stopButton;
    private Button clearButton;

    private boolean enableStatus = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlEditText = (EditText) findViewById(R.id.url_text);
        termEditText = (EditText) findViewById(R.id.term_text);
        startTermEditText = (EditText) findViewById(R.id.start_term_text);
        endTermEditText = (EditText) findViewById(R.id.end_term_text);

        startButton = (Button) findViewById(R.id.start_button);
        stopButton = (Button) findViewById(R.id.stop_button);
        clearButton = (Button) findViewById(R.id.clear_button);

        initial();
    }

    private void initial() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String url = sharedPref.getString(ShareClass.URL_KEY, "");
        String term = sharedPref.getString(ShareClass.TERM_KEY, "");
        String startTerm = sharedPref.getString(ShareClass.START_TERM_KEY, "");
        String endTerm = sharedPref.getString(ShareClass.END_TERM_KEY, "");
        enableStatus = sharedPref.getBoolean(ShareClass.ENABLE_KEY, true);

        enableAll(enableStatus);

        urlEditText.setText(url);
        termEditText.setText(term);
        startTermEditText.setText(startTerm);
        endTermEditText.setText(endTerm);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void startChecking(View view) {
        String url = urlEditText.getText().toString().trim();
        String term = termEditText.getText().toString().trim();
        String startTerm = startTermEditText.getText().toString().trim();
        String endTerm = endTermEditText.getText().toString().trim();

        if (!validInput(url)) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            return;
        }

        enableAll(ShareClass.DISABLE);
        enableStatus = ShareClass.DISABLE;

        Intent notificationIntent = new Intent(this, NotificationService.class);
        notificationIntent.putExtra(ShareClass.URL_KEY, url);
        notificationIntent.putExtra(ShareClass.TERM_KEY, term);
        notificationIntent.putExtra(ShareClass.START_TERM_KEY, startTerm);
        notificationIntent.putExtra(ShareClass.END_TERM_KEY, endTerm);

        startService(notificationIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putString(ShareClass.URL_KEY, urlEditText.getText().toString());
        editor.putString(ShareClass.TERM_KEY, termEditText.getText().toString());
        editor.putString(ShareClass.START_TERM_KEY, startTermEditText.getText().toString());
        editor.putString(ShareClass.END_TERM_KEY, endTermEditText.getText().toString());
        editor.putBoolean(ShareClass.ENABLE_KEY, enableStatus);

        editor.commit();
    }

    private boolean validInput(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    private void enableAll(boolean enable) {
        urlEditText.setEnabled(enable);
        termEditText.setEnabled(enable);
        startTermEditText.setEnabled(enable);
        endTermEditText.setEnabled(enable);

        startButton.setEnabled(enable);
        stopButton.setEnabled(!enable);
        clearButton.setEnabled(enable);
    }

    public void stopChecking(View view) {
        enableAll(ShareClass.ENABLE);
        enableStatus = ShareClass.ENABLE;

        Intent notificationIntent = new Intent(this, NotificationService.class);
        stopService(notificationIntent);
    }

    public void clearAllField(View view) {
        urlEditText.setText(ShareClass.EMPTY_STRING);
        termEditText.setText(ShareClass.EMPTY_STRING);
        startTermEditText.setText(ShareClass.EMPTY_STRING);
        endTermEditText.setText(ShareClass.EMPTY_STRING);
    }
}
