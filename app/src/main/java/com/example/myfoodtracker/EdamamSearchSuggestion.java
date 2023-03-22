package com.example.myfoodtracker;

import android.os.Parcel;
import android.os.Parcelable;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.util.Comparator;

public class EdamamSearchSuggestion implements SearchSuggestion, Comparator {
    String string;
    public static final Parcelable.Creator<EdamamSearchSuggestion> CREATOR
            = new Parcelable.Creator<EdamamSearchSuggestion>()
    {

        @Override
        public EdamamSearchSuggestion createFromParcel(Parcel parcel) {
            return new EdamamSearchSuggestion(parcel);
        }

        @Override
        public EdamamSearchSuggestion[] newArray(int i) {
            return new EdamamSearchSuggestion[i];
        }
    };

    public EdamamSearchSuggestion(String string){
        this.string = string;
    }

    public EdamamSearchSuggestion(Parcel in){
        string = in.readString();
    }

    public EdamamSearchSuggestion(){}

    @Override
    public String getBody() {
        return string;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(string);
    }

    @Override
    public int compare(Object o, Object t1)
    {
        try{
            SearchSuggestion new_o = (SearchSuggestion) o;
            SearchSuggestion new_t = (SearchSuggestion) t1;
            return new_o.getBody().compareTo(new_t.getBody());
        }
        catch(Exception e)
        {
            return 0;
        }
    }
}
