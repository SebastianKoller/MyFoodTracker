package com.example.myfoodtracker;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Meal
{

    public enum mealTypes {BREAKFAST, LUNCH, DINNER, SNACK}

    @JsonProperty
    protected String label = "Default Meal";
    @JsonProperty
    protected int type= 0;
    @JsonProperty
    protected int stock = 0;
    @JsonProperty
    protected boolean ignoreStock = true;
    @JsonProperty
    protected boolean favourite = false;
    @JsonProperty
    protected String notes = "";
    @JsonProperty
    protected List<String> subMeals = new ArrayList<>();
    @JsonProperty
    protected List<String> subFoods = new ArrayList<>();


    public List<String> getSubMeals() {
        return subMeals;
    }

    public void setSubMeals(List<String> subMeals) {
        this.subMeals = subMeals;
    }

    public List<String> getSubFoods() {
        return subFoods;
    }

    public void setSubFoods(List<String> subFoods) {
        this.subFoods = subFoods;
    }

    public Meal()
    {

    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isIgnoreStock() {
        return ignoreStock;
    }

    public void setIgnoreStock(boolean ignoreStock) {
        this.ignoreStock = ignoreStock;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String mealString()
    {
        return "MEAL: "+this.label+" {"+this.subFoods.get(0)+","+this.subFoods.get(1)+"}";
    }

}