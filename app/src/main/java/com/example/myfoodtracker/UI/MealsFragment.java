package com.example.myfoodtracker.UI;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfoodtracker.Tasks.EdamamQueryTask;
import com.example.myfoodtracker.Meal;
import com.example.myfoodtracker.MealsAdapter;
import com.example.myfoodtracker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MealsFragment extends Fragment
{
    // TAGS
    public static final String TAG = "MealsFragment";

    // FRONTEND
    View thisView;
    SearchView searchView;
    RecyclerView mealRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    DividerItemDecoration dividerItemDecoration;

    // BACKEND
    Context thisContext;
    ArrayList<Meal> mealList = new ArrayList<>();
    MealsAdapter mealsAdapter;

    // SETUP
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.i(TAG,"Created: " + this.toString());
        thisView = inflater.inflate(R.layout.fragment_meals, container, false);
        setFrontEnd();
        setListeners();
        return thisView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        thisContext = getActivity();
    }

    private void setFrontEnd()
    {
        searchView = thisView.findViewById(R.id.searchView1);
        mealRecyclerView = thisView.findViewById(R.id.mealsRecyclerView);
        mealsAdapter = new MealsAdapter(thisContext,mealList);
        mealRecyclerView.setAdapter(mealsAdapter);
            layoutManager = new LinearLayoutManager(getActivity());
            mealRecyclerView.setLayoutManager(layoutManager);
            dividerItemDecoration = new DividerItemDecoration(mealRecyclerView.getContext(), LinearLayout.VERTICAL);
            mealRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    // LISTENERS
    private void setListeners()
    {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                Log.i(TAG,"Searched "+query);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                mealsAdapter.getFilter().filter(newText);
                mealsAdapter.notifyDataSetChanged();
                return false;
            }
        });


    }
}
