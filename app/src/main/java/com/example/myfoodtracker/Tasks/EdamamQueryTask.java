package com.example.myfoodtracker.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.myfoodtracker.BuildConfig;

import java.io.IOException;
import java.io.OutputStreamWriter;

import okhttp3.OkHttpClient;

public class EdamamQueryTask extends AsyncTask
{
    public queryResponse delegate = null;
    private String query;
    private Context context;
    public static final String TAG = "EdamamQueryTask";

    public EdamamQueryTask(String query, queryResponse response, Context context) {
        this.query = query;
        this.delegate = response;
        this.context = context;
    }

    public interface queryResponse
    {
        void processFinish(String s);
    }

    @Override
    protected Object doInBackground(Object[] objects)
    {
        OkHttpClient client = new OkHttpClient();
        okhttp3.Response response = null;
        String strResponse = "";
        try {
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url("https://edamam-food-and-grocery-database.p.rapidapi.com/parser?ingr=" + query)
                    .get()
                    .addHeader("X-RapidAPI-Key", BuildConfig.EDAMAM_FG_API_KEY)
                    .addHeader("X-RapidAPI-Host", "edamam-food-and-grocery-database.p.rapidapi.com")
                    .build();
            response = client.newCall(request).execute();
            strResponse = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error requesting from URL");
        }

        return strResponse;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        delegate.processFinish(o.toString());
    }
}

