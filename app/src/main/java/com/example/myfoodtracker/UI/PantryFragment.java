package com.example.myfoodtracker.UI;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
//import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfoodtracker.PantryAdapter;
import com.example.myfoodtracker.PantryFood;
import com.example.myfoodtracker.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PantryFragment extends Fragment implements PantryAdapter.itemClickedCallback
{
    // TAGS
    public static final String TAG = "PantryFragment";
    private PantryFragment thisFrag = this;

    // DATABASE
    protected static final String DB_CHILD = "Foods";
    protected DatabaseReference dbRef =
            FirebaseDatabase.getInstance(MainActivity.FIREBASE_URL).getReference().child(DB_CHILD);

    // FRONTEND
    View thisView;
    SearchView searchView;
    RecyclerView pantryRecyclerView;
    Button addButton;
    RecyclerView.LayoutManager layoutManager;
    DividerItemDecoration dividerItemDecoration;
    BottomNavigationView bottomOptionsMenu;

    // BACKEND
    Context thisContext;
    ArrayList<PantryFood> foodList = new ArrayList<>();
    PantryAdapter pantryAdapter;

    // SETUP
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.i(TAG,"Created: " + this.toString());
        thisView = inflater.inflate(R.layout.fragment_pantry, container, false);
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
        bottomOptionsMenu = thisView.findViewById(R.id.bottomOptionsMenu);
            bottomOptionsMenu.setVisibility(View.GONE);
        searchView = thisView.findViewById(R.id.searchView1);
        addButton = thisView.findViewById(R.id.newFoodButton);
        pantryRecyclerView = thisView.findViewById(R.id.pantryRecyclerView);
        pantryRecyclerView.setClickable(true);
        //pantryAdapter = new PantryAdapter(thisContext,foodList);
        pantryAdapter = new PantryAdapter(thisContext,foodList,this);
        pantryRecyclerView.setAdapter(pantryAdapter);
            layoutManager = new LinearLayoutManager(pantryRecyclerView.getContext());
            pantryRecyclerView.setLayoutManager(layoutManager);
            dividerItemDecoration = new DividerItemDecoration(pantryRecyclerView.getContext(), LinearLayout.VERTICAL);
            pantryRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    // LISTENERS
    private void setListeners()
    {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                Log.i(TAG,"Searched "+query);
                //new UDSAQueryTask(query, thisFrag).execute();
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                pantryAdapter.getFilter().filter(newText);
                pantryAdapter.notifyDataSetChanged();
                return false;
            }
        });

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                ArrayList<PantryFood> newFoodList = new ArrayList<>();

                Iterable<DataSnapshot> children = snapshot.getChildren();
                for (DataSnapshot child : children) {
                    PantryFood tempFood = child.getValue(PantryFood.class);
                    newFoodList.add(tempFood);
                }
                pantryAdapter.updateFoodList(newFoodList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            { Log.i(TAG,"CANCELLED! " + this.toString()); }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                pantryAdapter.createMeal(thisFrag);
            }
        });

        bottomOptionsMenu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.deleteFoodsOption:
                        pantryAdapter.deleteFoods();
                        return true;
                    case R.id.newMealOption:
                        pantryAdapter.createMeal(thisFrag);
                        Log.i(TAG,"Clicked NEW MEAL"); return true;
                }
                return false;
            }
        });

    }


    @Override
    public void updateEditMode(boolean newEditMode) {
        if (newEditMode == true)
        {
            bottomOptionsMenu.setVisibility(View.VISIBLE);
        }
        else
        {
            bottomOptionsMenu.setVisibility(View.GONE);
        }
    }
}
