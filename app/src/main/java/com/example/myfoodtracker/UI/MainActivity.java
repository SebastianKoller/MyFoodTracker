package com.example.myfoodtracker.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.myfoodtracker.BuildConfig;
import com.example.myfoodtracker.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{

    // Ready to be made public

    public final static String TAG = "Main Activity";
    public final static String FIREBASE_URL = BuildConfig.FIREBASE_URL;

    FragmentManager fragmentManager;
    PantryFragment pantryFragment;
    SearchFragment searchFragment;
    MealsFragment mealsFragment;
    public AddMealFragment addMealFragment;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("TAG","Created: " + this.toString());
        setContentView(R.layout.activity_main);
        setFragments();
        setElements();
        setListeners();
    }

    public void setViewFragment(String fTag)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for (Fragment f : fragmentManager.getFragments())
        {
            ft.hide(f);
        }

        switch (fTag)
        {
            case SearchFragment.TAG:
                ft.show(searchFragment).commit();
                break;
            case PantryFragment.TAG:
                ft.show(pantryFragment).commit();
                break;
            case MealsFragment.TAG:
                ft.show(mealsFragment).commit();
                break;
            case AddMealFragment.TAG:
                ft.show(addMealFragment).commit();
                break;
        }

    }

    private void setFragments()
    {
        pantryFragment = new PantryFragment();
        searchFragment = new SearchFragment();
        mealsFragment = new MealsFragment();
        addMealFragment = new AddMealFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.fragmentContainerView, mealsFragment, MealsFragment.TAG).addToBackStack(null).hide(mealsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainerView, pantryFragment, PantryFragment.TAG).addToBackStack(null).hide(pantryFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainerView, addMealFragment, AddMealFragment.TAG).addToBackStack(null).hide(addMealFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainerView, searchFragment, SearchFragment.TAG).addToBackStack(null).commit();
    }

    private void setElements()
    {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.search);
    }

    private void setListeners()
    {
        //listAdapter.setClickListener(this);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.search: setViewFragment(SearchFragment.TAG); Log.i(TAG,"Clicked Search navigation"); return true;
            case R.id.myFoods: setViewFragment(PantryFragment.TAG); Log.i(TAG,"Clicked Pantry navigation"); return true;
            case R.id.myMeals: setViewFragment(MealsFragment.TAG); Log.i(TAG,"Clicked Meals navigation"); return true;
            //case R.id.search: setCurrentFragment(searchFragment); return true;
        }
        return false;
    }

}