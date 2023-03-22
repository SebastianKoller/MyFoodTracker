package com.example.myfoodtracker;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PantryFood extends EdamamFood
{
    @JsonIgnore
    protected int stock = 0;
    @JsonIgnore
    protected boolean ignoreStock = false;
    @JsonIgnore
    protected boolean favourite = false;
    @JsonIgnore
    protected String notes = "";

    PantryFood()
    {

    }

    public String foodString()
    {
        return "FOOD: "+this.label+" {"+this.categoryLabel+"}";
    }
}
