package com.example.myfoodtracker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.myfoodtracker.UI.AddMealFragment;
import com.example.myfoodtracker.UI.MainActivity;
import com.example.myfoodtracker.UI.PantryFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PantryAdapter
        extends RecyclerView.Adapter<PantryAdapter.FoodViewHolder>
        implements Filterable
{
    // TAGS
    public static final String TAG = "PantryAdapter";

    // DATABASE
    protected static final String DB_CHILD = "Foods";
    protected DatabaseReference dbRef =
            FirebaseDatabase.getInstance(MainActivity.FIREBASE_URL).getReference().child(DB_CHILD);

    AddMealFragment addMealFragment;

    // BACKEND
    private Context thisContext;
    private ArrayList<PantryFood> foodList;
    private ArrayList<PantryFood> ogFoodList;

    private ArrayList<Integer> clickedItems = new ArrayList();
    private ArrayList<Integer> checkedItems = new ArrayList();

    // LAYOUTS
    private int pantryItemLayout = R.layout.pantry_item;
    private int pantryItemInfoLayout = R.layout.pantry_item_info;
    private int explicitViewType = 0;

    private itemClickedCallback itemClickedCallback;

    // CONSTRUCTORS
    public PantryAdapter(Context context, ArrayList<PantryFood> foods)
    {
        Log.i(TAG,"Created PantryAdapter:" + this.toString());
        this.foodList = foods;
        this.ogFoodList = foods;
        this.thisContext = context;
        setListeners();
    }

    public PantryAdapter(Context context, ArrayList<PantryFood> foods, int explicitViewType)
    {
        Log.i(TAG,"Created PantryAdapter (E):" + this.toString());
        this.foodList = foods;
        this.ogFoodList = foods;
        this.thisContext = context;
        this.explicitViewType = explicitViewType;
        setListeners();
    }

    public PantryAdapter(Context context, ArrayList<PantryFood> foods, itemClickedCallback itemClickedCallback)
    {
        Log.i(TAG,"Created PantryAdapter (C):" + this.toString());
        this.foodList = foods;
        this.ogFoodList = foods;
        this.thisContext = context;
        this.itemClickedCallback = itemClickedCallback;
        setListeners();
    }

    private void setListeners()
    {
//        dbRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot)
//            {
//                ArrayList<PantryFood> newFoodList = new ArrayList<>();
//
//                Iterable<DataSnapshot> children = snapshot.getChildren();
//                for (DataSnapshot child : children) {
//                    PantryFood tempFood = child.getValue(PantryFood.class);
//                    newFoodList.add(tempFood);
//                }
//                updateFoodList(newFoodList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error)
//            { Log.i(TAG,"CANCELLED! " + this.toString()); }
//        });
    }

    public interface itemClickedCallback
    {
        void updateEditMode(boolean newEditMode);
    };

    // CUSTOM METHODS
    private ArrayList<String> getFoodIds()
    {
        ArrayList<String> foodIds = new ArrayList<>();
        for (PantryFood p : ogFoodList)
        {
            foodIds.add(p.getFoodId());
        }
        return foodIds;
    }

    public ArrayList<String> getClickedFoodIds()
    {
        ArrayList<String> clickedFoods = new ArrayList<>();
        for (Integer i : clickedItems)
        {
            clickedFoods.add(foodList.get(i).getFoodId());
        }
        return clickedFoods;
    }

    public void updateFoodList(ArrayList<PantryFood> newFoodList)
    {
        this.ogFoodList = newFoodList;
        this.foodList = newFoodList;
        notifyDataSetChanged();
        Log.d(TAG, "foodList updated");
    }

    public void createMeal(PantryFragment parentFrag)
    {
        ArrayList<PantryFood> subFoods = new ArrayList<>();
        for(int i : checkedItems)
        {
            subFoods.add(foodList.get(i));
            Log.i(TAG,"IS CHECKED: " + foodList.get(i).getLabel());
        }

        ((MainActivity) parentFrag.getActivity()).addMealFragment.setSubFoodsList(subFoods);
        ((MainActivity) parentFrag.getActivity()).setViewFragment(AddMealFragment.TAG);
    }

    public void deleteFoods()
    {
        String deleteCount = String.valueOf(checkedItems.size());
        int i = 0;
        for (i = checkedItems.size() - 1; i > -1; i--);
        {
            int pos = checkedItems.get(i);
            String removeId = ogFoodList.get(pos).getFoodId();
            dbRef.child(removeId).removeValue();
        }

        Toast.makeText(thisContext, "Removed " + deleteCount + " food(s) from pantry", Toast.LENGTH_SHORT).show();
    }

    // CUSTOM VIEWHOLDER CLASS FOR EACH ITEM IN RECYCLERVIEW
    public class FoodViewHolder extends RecyclerView.ViewHolder
    {
        // DECLARATIONS
        TextView foodLabel;
        TextView foodCategory;
        ImageView foodIcon;
        ImageButton confirmButton;
        CheckBox checkBox;
        MultiAutoCompleteTextView multiAutoCompleteTextView;

        // CONSTRUCTOR
        public FoodViewHolder(View itemView)
        {
            super(itemView);

            // SET ELEMENTS
            foodLabel = itemView.findViewById(R.id.foodLabel);
            foodCategory = itemView.findViewById(R.id.foodCategory);
            foodIcon = itemView.findViewById(R.id.foodIcon);
            confirmButton = itemView.findViewById(R.id.confirmButton);
            checkBox = itemView.findViewById(R.id.checkBox);
            multiAutoCompleteTextView = itemView.findViewById(R.id.multiAutoCompleteTextView);

            // SET LISTENERS
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int pos = getAdapterPosition();
                    if (clickedItems.contains((Integer) pos))
                    {
                        clickedItems.remove((Integer) pos);
                    }
                    else
                    {
                        clickedItems.add((Integer) pos);
                    }

                    //notifyItemChanged(pos);
                }
            });

            confirmButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    PantryFood currentFood = foodList.get(getAdapterPosition());
                    String id = currentFood.getFoodId();

                    dbRef.child(id).child("notes").setValue(multiAutoCompleteTextView.getText().toString());
                    Toast.makeText(thisContext, currentFood.getLabel() + " removed from pantry", Toast.LENGTH_SHORT).show();
                }
            });

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (checkBox.isChecked()) { checkedItems.add((Integer) pos);}
                    else { checkedItems.remove((Integer) pos); }


                    boolean editMode = false;
                    if (checkedItems.size() > 0) editMode = true;
                    itemClickedCallback.updateEditMode(editMode);
                }

            });

        }
    }

    // RECYCLERVIEW ADAPTER IMPLEMENTATION
    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(thisContext).inflate(viewType,parent,false);

        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position)
    {
        PantryFood thisFood = foodList.get(position);
        holder.foodLabel.setText(thisFood.getLabel());
        holder.foodCategory.setText(thisFood.getCategory());

        if (checkedItems.contains((Integer) position))
        { holder.checkBox.setChecked(true); }

        Glide.with(thisContext)
                .load(thisFood.getImage())
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
    public int getItemViewType(int position) {
        if (explicitViewType != 0) return explicitViewType;
        if (clickedItems.contains((Integer) position)) return pantryItemInfoLayout;
        return pantryItemLayout;
    }

    @Override
    public int getItemCount() {
        return foodList.size();
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

                if (charSequence.length() == 0) { foodList = ogFoodList; }
                else
                {

                    for(PantryFood f : ogFoodList)
                    {
                        Log.i(TAG,f.getLabel().toLowerCase() + " contains " + constraint + "? " + String.valueOf(f.getLabel().toLowerCase().contains(constraint)));

                        if (f.getLabel().toLowerCase().contains(constraint)){
                            filteredResults.add(f);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                foodList.clear();
                if (charSequence.length() == 0 ) { foodList = ogFoodList; }
                else { foodList.addAll((List)filterResults.values);}
                notifyDataSetChanged();
            }

        };
    }

    @Override
    public Filter getFilter() {
        return getAlphaFilter();
    }

}
