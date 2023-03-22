package com.example.myfoodtracker.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.recyclerview.widget.SortedList;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.myfoodtracker.BuildConfig;
import com.example.myfoodtracker.EdamamSearchSuggestion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;

// EDAMAM AUTO COMPLETE TASK
public class EdamamACQTask extends AsyncTask
{
    public queryResponse delegate = null;
    private String query;
    private Context context;
    public static final String TAG = "EdamamACQTask";

    public EdamamACQTask(String query, queryResponse response, Context context) {
        this.query = query;
        this.delegate = response;
        this.context = context;
    }

    public interface queryResponse
    {
        void setSuggestions(String query, List<SearchSuggestion> suggestions);
    }

    @Override
    protected Object doInBackground(Object[] objects)
    {
        OkHttpClient client = new OkHttpClient();
        okhttp3.Response response = null;
        String strResponse = "";
        HashMap<String,List<SearchSuggestion>> results = new HashMap<String,List<SearchSuggestion>>();
        List<SearchSuggestion> suggestionList = new ArrayList<SearchSuggestion>();

        // CHANGE HARDCODED ID AND KEY
        try {
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url("https://api.edamam.com/auto-complete?app_id=" + BuildConfig.EDAMAM_AC_APP_ID + "&app_key=" + BuildConfig.EDAMAM_AC_API_KEY +"=" + query)
                    .get()
                    .build();
            response = client.newCall(request).execute();
            strResponse = response.body().string();
            strResponse = strResponse.substring(1,strResponse.length()-1);

            String suggestions[] = strResponse.split(",");

            for (int i=0; i<suggestions.length; i++)
            {
                suggestionList.add(new EdamamSearchSuggestion(suggestions[i].replaceAll("\"","")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error requesting from URL");
        }

        return suggestionList;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        delegate.setSuggestions(query,(List<SearchSuggestion>)o);
    }
}

