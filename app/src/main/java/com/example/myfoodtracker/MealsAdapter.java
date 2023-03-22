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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfoodtracker.UI.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MealsAdapter
        extends RecyclerView.Adapter<MealsAdapter.FoodViewHolder>
        implements Filterable
{
    // TAGS
    public static final String TAG = "MealsAdapter";

    // DATABASE
    protected DatabaseReference dbRef =
            FirebaseDatabase.getInstance(MainActivity.FIREBASE_URL).getReference();
    protected DataSnapshot currentSnapshot;

    // BACKEND
    private Context thisContext;
    private int mealItemLayout = R.layout.meal_item;
    private int mealItemInfoLayout = R.layout.meal_item_info;
    private ArrayList<Meal> mealList;
    private ArrayList<Meal> ogMealList;
    private ArrayList<Integer> clickedItems = new ArrayList();

    // CONSTRUCTOR
    public MealsAdapter(Context context, ArrayList<Meal> foods)
    {
        Log.i(TAG,"Created:" + this.toString());
        this.thisContext = context;
        this.mealList = foods;
        this.ogMealList = foods;
        setListeners();
    }

    // LISTENERS
    private void setListeners()
    {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                currentSnapshot = snapshot;

                ArrayList<Meal> newMealList = new ArrayList<>();

                Iterable<DataSnapshot> children = snapshot.child("Meals").getChildren();
                for (DataSnapshot child : children)
                {
                    Meal tempFood = child.getValue(Meal.class);
                    Log.i(TAG,"ADDED " + tempFood.getLabel() + " " + tempFood.getSubFoods().get(0));
                    newMealList.add(tempFood);
                }
                updateMealList(newMealList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG,"Failed to read firebase foods: " + error.toException());
            }
        });
    }

    // CUSTOM METHODS
    private void updateMealList(ArrayList<Meal> newMealList)
    {
        this.ogMealList = newMealList;
        this.mealList = newMealList;
        notifyDataSetChanged();
        Log.d(TAG, "mealList updated");
    }

    // CUSTOM VIEWHOLDER CLASS FOR EACH ITEM IN RECYCLERVIEW
    public class FoodViewHolder extends RecyclerView.ViewHolder
    {
        // DECLARATIONS
        TextView mealLabel;
        TextView mealCategory;
        ImageView foodIcon;
        ImageButton addButton;
        RecyclerView mealFoodsRv;
        View thisView;
        RecyclerView.LayoutManager layoutManager;
        DividerItemDecoration dividerItemDecoration;

        // CONSTRUCTOR
        public FoodViewHolder(View itemView)
        {
            super(itemView);
            // SET ELEMENTS
            thisView = itemView;
            mealLabel = itemView.findViewById(R.id.mealLabel);
            mealCategory = itemView.findViewById(R.id.foodCategory);
            foodIcon = itemView.findViewById(R.id.foodIcon);
            addButton = itemView.findViewById(R.id.addButton);
            mealFoodsRv = itemView.findViewById(R.id.mealFoodsRV);
                layoutManager = new LinearLayoutManager(mealFoodsRv.getContext());
                mealFoodsRv.setLayoutManager(layoutManager);
                dividerItemDecoration = new DividerItemDecoration(mealFoodsRv.getContext(), LinearLayout.VERTICAL);
                mealFoodsRv.addItemDecoration(dividerItemDecoration);

            // SET LISTENERS
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int pos = getAdapterPosition();
                    if (clickedItems.contains((Integer) pos)) { clickedItems.remove((Integer) pos); }
                    else { clickedItems.add((Integer) pos); }
                    //notifyItemChanged(pos);
                }
            });

            addButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Meal tempMeal = mealList.get(getAdapterPosition());
                    String id = String.valueOf(tempMeal.label);
                    Log.i(TAG,"You clicked meal: " + id);

//                    // Delete (food already added)
//                    if (foodIds.contains(id))
//                    {
//                        dbRef.child(id).removeValue();
//                        foodIds.remove(id);
//                        addButton.setImageResource(android.R.drawable.ic_input_add);
//                        Toast.makeText(thisContext, tempMeal.label + " removed from MyFoods", Toast.LENGTH_SHORT).show();
//                    }
//                    // Add new food
//                    else
//                    {
//                        foodIds.add(id);
//                        dbRef.child(id).setValue(tempMeal);
//                        addButton.setImageResource(android.R.drawable.ic_delete);
//                        Toast.makeText(thisContext, "Added " + tempMeal.label + " to MyFoods!", Toast.LENGTH_SHORT).show();
//                    }
                }
            });

        }
    }

    // RECYCLERVIEW ADAPTER IMPLEMENTATION
    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // HARDCODED
        View view = LayoutInflater.from(thisContext).inflate(mealItemInfoLayout,parent,false);

        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position)
    {
        Meal meal = mealList.get(position);
        Log.i(TAG,meal.mealString());
        holder.mealLabel.setText(meal.label);
        holder.mealCategory.setText(String.valueOf(meal.type));

        ArrayList<PantryFood> mealFoodsList = new ArrayList<>();
        for(String fId : meal.subFoods)
        {
            Log.i(TAG,fId);
            PantryFood mealFood = currentSnapshot.child("Foods").child(fId).getValue(PantryFood.class);
            Log.i(TAG,mealFood.foodString());
            mealFoodsList.add(mealFood);
        }
        holder.mealFoodsRv.setAdapter(new PantryAdapter(thisContext,mealFoodsList,R.layout.pantry_item_small));

    }
    @Override
    public int getItemViewType(int position) {
        if (clickedItems.contains((Integer) position))
        {
            return mealItemInfoLayout;
        }
        return mealItemLayout;
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    // FILTERABLE IMPLEMENTATION
    public Filter getAlphaFilter()
    {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence)
            {
                String constraint = charSequence.toString();
                ArrayList<PantryFood> filteredResults = new ArrayList<>();

                if (charSequence.length() == 0) { mealList = ogMealList; }
                else
                {
                    ArrayList<Meal> results = new ArrayList<>();
                    for(Meal m : ogMealList)
                    {
                        Log.i(TAG,m.label.toLowerCase() + " contains " + constraint + "? " + String.valueOf(m.label.toLowerCase().contains(constraint)));

                        if (m.label.toLowerCase().contains(constraint)){
                            results.add(m);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mealList.clear();
                if (charSequence.length() == 0 ) { mealList = ogMealList; }
                else { mealList.addAll((List)filterResults.values);}
                notifyDataSetChanged();

            }
        };
    }

    @Override
    public Filter getFilter() {
        return getAlphaFilter();
    }

}
