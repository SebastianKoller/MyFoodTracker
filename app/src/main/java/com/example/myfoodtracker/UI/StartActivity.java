package com.example.myfoodtracker.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myfoodtracker.R;

import android.os.Bundle;
import android.util.Log;

public class StartActivity extends AppCompatActivity {

    public final static String TAG = "Start Activity";
    public final static String FIREBASE_URL = MainActivity.FIREBASE_URL;

    FragmentManager fragmentManager;
    LoginFragment loginFragment;
    RegisterFragment registerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("TAG","Created: " + this.toString());
        setContentView(R.layout.activity_start);
        setFragments();
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
            case LoginFragment.TAG:
                ft.show(loginFragment).commit();
                break;
            case RegisterFragment.TAG:
                ft.show(registerFragment).commit();
                break;
        }

    }

    private void setFragments()
    {
        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.startFragmentContainerView, registerFragment, RegisterFragment.TAG).addToBackStack(null).hide(registerFragment).commit();
        fragmentManager.beginTransaction().add(R.id.startFragmentContainerView, loginFragment, LoginFragment.TAG).addToBackStack(null).commit();
    }

    private void setListeners()
    {
        //listAdapter.setClickListener(this);
    }

}