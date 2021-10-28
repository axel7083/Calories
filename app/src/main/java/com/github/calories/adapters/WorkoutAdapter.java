package com.github.calories.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.calories.R;
import com.github.calories.databinding.RowFoodBinding;
import com.github.calories.databinding.RowWorkoutBinding;
import com.github.calories.models.Food;
import com.github.calories.models.Ingredient;
import com.github.calories.models.Workout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//TODO: use view binding
public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

    private List<Workout> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public WorkoutAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    public void updateData(List<Workout> mData)
    {
        this.mData = mData;
    }

    public List<Workout> getData() {
        return mData;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_workout, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.title.setText(mData.get(position).getName());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if(position%2==0)
            params.setMarginEnd(25);
        else
            params.setMarginStart(25);

        //params.gravity = (position%2==0)?Gravity.START:Gravity.END;
        holder.cvContainer.setLayoutParams(params);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return (mData == null)?0:mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        CardView cvContainer;

        ViewHolder(View itemView) {
            super(itemView);

            title = RowWorkoutBinding.bind(itemView).title;
            cvContainer = RowWorkoutBinding.bind(itemView).cvContainer;
            RowWorkoutBinding.bind(itemView).clickableLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("FoodAdapter","onClick DELETE");
            if (mClickListener == null)
                return;

            mClickListener.onOpen(mData.get(getBindingAdapterPosition()));
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onOpen(Workout workout);
    }

}
