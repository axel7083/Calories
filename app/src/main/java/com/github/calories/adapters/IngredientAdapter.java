package com.github.calories.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.calories.R;
import com.github.calories.databinding.RowIngredientBinding;
import com.github.calories.models.Food;
import com.github.calories.models.Ingredient;

import java.util.List;
import java.util.Locale;

//TODO: use view binding
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private List<Ingredient> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public IngredientAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    public void updateData(List<Ingredient> mData)
    {
        this.mData = mData;
    }

    public List<Ingredient> getData() {
        return mData;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_ingredient, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.title.setText(mData.get(position).getName());
        holder.amount.setText(String.format(Locale.getDefault(),"%.0f",mData.get(position).getPercentEstimate()).concat("%"));

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return (mData == null)?0:mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView amount;

        ViewHolder(View itemView) {
            super(itemView);

            title = RowIngredientBinding.bind(itemView).titleRow;
            amount = RowIngredientBinding.bind(itemView).amountRow;
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onRemove(Food food);
        void onOpen(Food food);
    }

}
