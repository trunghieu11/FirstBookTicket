package com.eleven.trunghieu11.firstbookticket;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by trunghieu11 on 6/27/2016.
 */
public class CheckTerm extends AsyncTask<String, Void, Boolean> {

    @Override
    public Boolean doInBackground(String... parameters) {
        return check(parameters[0], parameters[1], parameters[2], parameters[3]);
    }

    private Boolean check(String urlString, String startTerm, String term, String endTerm) {
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            boolean meetStart = false;
            while ((line = reader.readLine()) != null) {
                line = line.toLowerCase();

                if (line.contains(startTerm)) {
                    meetStart = true;
                }

                if (line.contains(term) && meetStart) {
                    return true;
                }

                if (line.contains(endTerm)) {
                    meetStart = false;
                }
            }
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}