package com.example.myfoodtracker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.myfoodtracker.UI.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EdamamRVAdapter
        extends RecyclerView.Adapter<EdamamRVAdapter.FoodViewHolder>
        implements Filterable
{

    public static final String TAG = "FoodRVAdapter";
    public static final String PREF_NAME = "MyFoodTracker";
    public static final String PREF_KEY = "FoodIDs";

    public final static String FIREBASE_URL = MainActivity.FIREBASE_URL;

    DatabaseReference dbRef;

    private Context myContext;
    private int itemLayout;
    public ArrayList<EdamamFood> foodList;
    protected ArrayList<EdamamFood> ogFoodList;
    private LayoutInflater  mInflater;
    public static final String DB_CHILD = "Foods";

    private ArrayList<String> foodIds;

    public EdamamRVAdapter(
            Context context, ArrayList<EdamamFood> foods, int itemLayout){
        Log.i(TAG,"Created:" + this.toString());
        this.mInflater = LayoutInflater.from(context);
        this.foodList = foods;
        this.ogFoodList = foods;
        this.myContext = context;
        this.itemLayout = itemLayout;
        this.dbRef = FirebaseDatabase.getInstance(MainActivity.FIREBASE_URL).getReference().child(DB_CHILD);
        this.foodIds = new ArrayList<>();
        setElements();
        setListeners();
    }

    private void setElements()
    {

    }

    private void setListeners(){

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Iterable<DataSnapshot> children = snapshot.getChildren();

                for (DataSnapshot child : children)
                {
                    String tempKey = child.getKey();
                    foodIds.add(tempKey);
                }
                //Log.d(TAG, "Updated foodIds");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(itemLayout,parent,false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position)
    {
        EdamamFood f = foodList.get(position);
        holder.myTextView.setText(f.label);
        if (foodIds.contains(String.valueOf(f.foodId))) { holder.addButton.setImageResource(android.R.drawable.ic_delete); }

        Glide.with(myContext)
                .load(f.image)
                .listener(new RequestListener() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object o, Target target, boolean b) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Object o, Object o2, Target target, DataSource dataSource, boolean b) {
                        //Log.i(TAG,"Glide loaded " + simpleName +" image from " +  dataSource.toString());
                        return false;
                    }
                })
                .into(holder.foodIcon)
                ;
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public Filter getAlphaFilter()
    {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence)
            {
                String constraint = charSequence.toString();
                ArrayList<EdamamFood> filteredResults = new ArrayList<>();

                if (charSequence.length() == 0) { foodList = ogFoodList; }
                else
                {
                    ArrayList<EdamamFood> results = new ArrayList<>();
                    for(EdamamFood f : ogFoodList)
                    {
                        Log.i(TAG,f.label.toLowerCase() + " contains " + constraint + "? " + String.valueOf(f.label.toLowerCase().contains(constraint)));

                        if (f.label.toLowerCase().contains(constraint)){
                            results.add(f);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                if (filterResults.count == 0) { foodList = new ArrayList<>(); }
//                else { foodList = (ArrayList<SearchResultFood>) filterResults.values; }

            }
        };
    }

    public void setFoodList(ArrayList<EdamamFood> foodList) { this.foodList = foodList; }

    @Override
    public Filter getFilter() {
        return getAlphaFilter();
    }

    public class FoodViewHolder
            extends RecyclerView.ViewHolder
    {
        TextView myTextView;
        ImageView foodIcon;
        ImageButton addButton;

        public FoodViewHolder(View itemView)
        {
            super(itemView);
            myTextView = itemView.findViewById(R.id.mealLabel);
            foodIcon = itemView.findViewById(R.id.foodIcon);
            addButton = itemView.findViewById(R.id.addButton);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int pos = getAdapterPosition();
                    Log.d(TAG, "onClick: You clicked " + pos);
                    Toast.makeText(myContext, "You clicked " + foodList.get(pos), Toast.LENGTH_SHORT).show();
                }
            });

            addButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    EdamamFood tempFood = foodList.get(getAdapterPosition());
                    String id = String.valueOf(tempFood.foodId);

                    // Delete (food already added)
                    if (foodIds.contains(id))
                    {
                        dbRef.child(id).removeValue();
                        foodIds.remove(id);
                        addButton.setImageResource(android.R.drawable.ic_input_add);
                        Toast.makeText(myContext, tempFood.label + " removed from MyFoods", Toast.LENGTH_SHORT).show();
                    }
                    // Add new food
                    else
                    {
                        foodIds.add(id);
                        dbRef.child(id).setValue(tempFood);
                        addButton.setImageResource(android.R.drawable.ic_delete);
                        Toast.makeText(myContext, "Added " + tempFood.label + " to MyFoods!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    EdamamFood getItem(int id) {
        return foodList.get(id);
    }
}
