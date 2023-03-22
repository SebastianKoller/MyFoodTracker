package com.example.myfoodtracker.UI;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

//import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.myfoodtracker.Tasks.EdamamACQTask;
import com.example.myfoodtracker.EdamamFood;
import com.example.myfoodtracker.Tasks.EdamamQueryTask;
import com.example.myfoodtracker.EdamamRVAdapter;
import com.example.myfoodtracker.R;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SearchFragment extends Fragment
        implements EdamamQueryTask.queryResponse, EdamamACQTask.queryResponse
{

    public SearchFragment thisFrag = this;
    public static final String TAG = "SearchFragment";
    public static final String DB_CHILD = "Foods";

    int searchItemLayout;

    HashMap<String,List<SearchSuggestion>> previousLists;
    FloatingSearchView floatingSearchView;

    View thisView;
    ViewGroup viewGroup;
    RecyclerView foodRecyclerView;

    RecyclerView.LayoutManager layoutManager;
    DividerItemDecoration dividerItemDecoration;

    DatabaseReference dbRef;
    String foodListString;
//    ArrayList<SearchResultFood> foodList;
    ArrayList<EdamamFood> foodList;
    //FoodRecyclerViewAdapter foodsAdapter;
    EdamamRVAdapter foodsAdapter;

    EdamamACQTask edamamACQTask;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.i("TAG","Instance " + this.toString() + " created");
        viewGroup = container;
        thisView = inflater.inflate(R.layout.fragment_search, container, false);
        setFrontEnd();
        setBackEnd();
        setListeners();
        return thisView;
    }

    private void setFrontEnd()
    {
        floatingSearchView = thisView.findViewById(R.id.floatingSearchView);
        //floatingSearchView.bringToFront();
//        searchView = thisView.findViewById(R.id.searchView1);
//        autoCompleteTextView = thisView.findViewById(R.id.autoCompleteTextView);
//        autoCompleteTextView.setDropDownAnchor(anchorView.getId());

        foodRecyclerView = thisView.findViewById(R.id.searchFoodRecyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        foodRecyclerView.setLayoutManager(layoutManager);
        dividerItemDecoration = new DividerItemDecoration(foodRecyclerView.getContext(), LinearLayout.VERTICAL);
        foodRecyclerView.addItemDecoration(dividerItemDecoration);

        searchItemLayout = R.layout.food_item_search;
        floatingSearchView.setSuggestionsAnimDuration(0);

    }

    private void setBackEnd()
    {
        foodList = new ArrayList<>();
        dbRef = FirebaseDatabase.getInstance(MainActivity.FIREBASE_URL).getReference().child(DB_CHILD);
//        foodsAdapter = new FoodRecyclerViewAdapter(getActivity(),foodList,searchItemLayout);
        foodsAdapter = new EdamamRVAdapter(getActivity(),foodList,searchItemLayout);
        foodRecyclerView.setAdapter(foodsAdapter);

        previousLists = new HashMap<String,List<SearchSuggestion>>();

    }

    private void setListeners()
    {

        floatingSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                foodRecyclerView.setClickable(false);
                floatingSearchView.bringToFront();
            }

            @Override
            public void onFocusCleared() {
                foodRecyclerView.setClickable(true);
                foodRecyclerView.bringToFront();
            }
        });

        floatingSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"clicked floatingSearchView");
            }
        });

        floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {

                if (newQuery.length() > 2)
                {

                    if (previousLists.containsKey(newQuery))
                    {
                        List<SearchSuggestion> prevSug = previousLists.get(newQuery);

                        floatingSearchView.clearSuggestions();
                        floatingSearchView.swapSuggestions(previousLists.get(newQuery));

//                        if (prevSug.toArray().length == 1 && newQuery.contentEquals(prevSug.get(0).getBody()))
//                        {
//                            floatingSearchView.clearSuggestions();
//                        }

                        Log.i(TAG,"Replaced with old "+newQuery+" suggestions");
                        floatingSearchView.hideProgress();
                    }
                    else
                    {
                        Log.i(TAG,"Requesting new " + newQuery + " suggestions");
                        edamamACQTask = new EdamamACQTask(newQuery,thisFrag,getActivity());
                        edamamACQTask.execute();
                        floatingSearchView.showProgress();
                    }
                }
                else
                {
                    floatingSearchView.clearSuggestions();
                }
            }
        });

        floatingSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                floatingSearchView.setSearchText(searchSuggestion.getBody());
                sendQuery(searchSuggestion.getBody());
            }

            @Override
            public void onSearchAction(String currentQuery) {
                sendQuery(currentQuery);
            }

            private void sendQuery(String currentQuery) {

                cancelACTask();
                new EdamamQueryTask(currentQuery,thisFrag,getActivity()).execute();
            }
        });
    }

    @Override
    public void processFinish(String s) {
        foodListString = s;
        //parseFoodQuery(s);
        parseEdamamFoodQuery(s);
    }

    private void parseFoodQuery(String json){
        ObjectMapper mapper = new ObjectMapper();
        foodList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray gotFoods = jsonObject.getJSONArray("foods");

            for (int i = 0; i < gotFoods.length(); i++){
                //SearchResultFood tempFood = mapper.readValue(gotFoods.getJSONObject(i).toString(),SearchResultFood.class);
                EdamamFood tempFood = mapper.readValue(gotFoods.getJSONObject(i).toString(),EdamamFood.class);
                foodList.add(tempFood);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //foodsAdapter = new FoodRecyclerViewAdapter(getActivity(),foodList,searchItemLayout);
        foodsAdapter = new EdamamRVAdapter(getActivity(),foodList,searchItemLayout);
        foodRecyclerView.setAdapter(foodsAdapter);
        Log.i(TAG,"Refreshing foodList");
    }

    private void parseEdamamFoodQuery(String json){
        ObjectMapper mapper = new ObjectMapper();
        foodList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);

            JSONArray foodsJson = jsonObject.getJSONArray("hints");

            //Log.i(TAG,"FOODS jsON" + foodsJson.toString());

            for (int i = 0; i < foodsJson.length(); i++){
                //SearchResultFood tempFood = mapper.readValue(gotFoods.getJSONObject(i).toString(),SearchResultFood.class);
                EdamamFood tempFood = mapper.readValue(foodsJson.getJSONObject(i).getJSONObject("food").toString(),EdamamFood.class);
                //tempFood.label = tempFood.label;
                foodList.add(tempFood);
                //Log.i(TAG,"Added " + tempFood);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //foodsAdapter = new FoodRecyclerViewAdapter(getActivity(),foodList,searchItemLayout);
        foodsAdapter.setFoodList(foodList);
        foodsAdapter.notifyDataSetChanged();



        //Log.i(TAG,"Refreshing foodList");
    }

    private void cancelACTask()
    {
        edamamACQTask.cancel(true);
        floatingSearchView.clearSearchFocus();
        floatingSearchView.clearSuggestions();
        floatingSearchView.hideProgress();
    }


    @Override
    public void setSuggestions(String query, List<SearchSuggestion> suggestions) {

        previousLists.put(query,suggestions);

        // Handles the wonky reversing from the FloatingSearchView pkg
        Collections.reverse(suggestions);

        floatingSearchView.clearSuggestions();
        floatingSearchView.swapSuggestions(suggestions);
        floatingSearchView.hideProgress();

        Log.i(TAG,"Swapped new " + query+ " suggestions");

    }
}
