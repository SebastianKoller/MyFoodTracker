package com.example.myfoodtracker.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import com.example.myfoodtracker.PantryAdapter;
import com.example.myfoodtracker.PantryFood;
import com.example.myfoodtracker.R;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RegisterFragment extends Fragment
{
    // TAGS
    public final static String TAG = "Register Fragment";

    // DATABASE
    protected static final String DB_CHILD = "Users";
    protected DatabaseReference dbRef =
            FirebaseDatabase.getInstance(MainActivity.FIREBASE_URL).getReference().child(DB_CHILD);

    View thisView;
    Context thisContext;
    Button registerButton;
    TextView nameTextView;
    TextView emailTextView;
    TextView passwordTextView;

    // SETUP
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.i(TAG,"Created: " + this.toString());
        thisView = inflater.inflate(R.layout.fragment_register, container, false);
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
        nameTextView = thisView.findViewById(R.id.nameTextView);
        registerButton = thisView.findViewById(R.id.registerButton);
        emailTextView = thisView.findViewById(R.id.emailTextView);
        passwordTextView = thisView.findViewById(R.id.passwordTextView);
    }

    // LISTENERS
    private void setListeners()
    {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { register(); }
        });
    }

    private void register()
    {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("name", nameTextView.getText().toString());
        editor.putString("email", emailTextView.getText().toString());
        editor.putString("password", passwordTextView.getText().toString());
        editor.apply();

        Toast.makeText(thisContext,"Registration Complete",Toast.LENGTH_SHORT);

        ((StartActivity)getActivity()).setViewFragment(LoginFragment.TAG);
    }
}