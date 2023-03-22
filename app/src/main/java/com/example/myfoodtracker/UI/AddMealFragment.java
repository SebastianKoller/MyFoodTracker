package com.example.myfoodtracker.UI;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfoodtracker.Meal;
import com.example.myfoodtracker.MealsAdapter;
import com.example.myfoodtracker.PantryAdapter;
import com.example.myfoodtracker.PantryFood;
import com.example.myfoodtracker.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AddMealFragment extends Fragment
{
    // TAGS
    public static final String TAG = "AddMealFragment";

    // DATABASE
    protected DatabaseReference dbRef =
            FirebaseDatabase.getInstance(MainActivity.FIREBASE_URL).getReference();

    // FRONTEND
    View thisView;
    RecyclerView subFoodsRecyclerView;
    RecyclerView.LayoutManager layoutManager;
    DividerItemDecoration dividerItemDecoration;
    int itemViewType = R.layout.pantry_item_small;

    Button addMealButton;
    TextView mealName;
    TextView mealCategory;

    // BACKEND
    Context thisContext;
    ArrayList<PantryFood> subFoodsList = new ArrayList<>();
    PantryAdapter pantryAdapter;

    // CUSTOM METHODS
    public void setSubFoodsList(ArrayList<PantryFood> subFoodsList) {
        this.subFoodsList = subFoodsList;
        pantryAdapter.updateFoodList(subFoodsList);
    }

    private ArrayList<String> getSubFoodIds()
    {
        ArrayList<String> subFoodIds = new ArrayList<>();
        for (PantryFood f : subFoodsList)
        {
            subFoodIds.add(f.getFoodId());
        }
        return subFoodIds;
    }

    // SETUP
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.i(TAG,"Created: " + this.toString());
        thisView = inflater.inflate(R.layout.fragment_add_meals, container, false);
        setFrontEnd();
        setListeners();
        return thisView;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        thisContext = getActivity();
        pantryAdapter = new PantryAdapter(getActivity(),subFoodsList,itemViewType);
    }

    private void setFrontEnd()
    {
        addMealButton = thisView.findViewById(R.id.addMealButton);
        mealName = thisView.findViewById(R.id.mealNameTextEdit);
        mealCategory = thisView.findViewById(R.id.mealNameTextEdit2);
        subFoodsRecyclerView = thisView.findViewById(R.id.subFoodsRecyclerView);

        pantryAdapter = new PantryAdapter(getActivity(),subFoodsList,itemViewType);
        subFoodsRecyclerView.setAdapter(pantryAdapter);
            layoutManager = new LinearLayoutManager(getActivity());
            subFoodsRecyclerView.setLayoutManager(layoutManager);
            dividerItemDecoration = new DividerItemDecoration(subFoodsRecyclerView.getContext(), LinearLayout.VERTICAL);
            subFoodsRecyclerView.addItemDecoration(dividerItemDecoration);
    }


    // LISTENERS
    private void setListeners()
    {
        addMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Meal newMeal = new Meal();
                newMeal.setLabel(mealName.getText().toString());
                newMeal.setType(0);
                newMeal.setSubFoods(getSubFoodIds());

                DatabaseReference dbRefMeals =
                        FirebaseDatabase.getInstance(MainActivity.FIREBASE_URL).getReference().child("Meals");

                DatabaseReference tempRef = dbRefMeals.push();
                tempRef.setValue(newMeal);

                ((MainActivity) getActivity()).setViewFragment(MealsFragment.TAG);
                Toast.makeText(thisContext,"Added New Meal!",Toast.LENGTH_SHORT);
            }
        });
    }
}
