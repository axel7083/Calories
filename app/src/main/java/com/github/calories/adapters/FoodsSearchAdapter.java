package com.github.calories.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.calories.R;
import com.github.calories.models.Food;
import com.github.calories.models.Ingredient;

import java.util.List;
import java.util.Locale;

//TODO: use view binding
public class FoodsSearchAdapter extends RecyclerView.Adapter<FoodsSearchAdapter.ViewHolder> {

    private List<Food> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public FoodsSearchAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    public void updateData(List<Food> mData)
    {
        this.mData = mData;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_search_results_food, parent, false);
        return new ViewHolder(view);
    }

    public void clear() {
        if(mData != null)
            mData.clear();

        notifyDataSetChanged();
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.title.setText(mData.get(position).getName());

        StringBuilder builder= new StringBuilder();

        for(Ingredient ingredient : mData.get(position).getIngredients()) {
            builder.append(ingredient.getName()).append(" (").append(String.format(Locale.getDefault(),"%.1f",ingredient.getPercentEstimate())).append("%), ");
        }

        if(builder.length() > 2)
            holder.description.setText(builder.subSequence(0, builder.length() - 2));

        //holder.myTextView.setText(mData.get(position).getName());
        //holder.smiley.setText(mData.get(position).getSmiley());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return (mData == null)?0:mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView description;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_row);
            description = itemView.findViewById(R.id.description_row);
            itemView.findViewById(R.id.add_btn).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("FoodAdapter","onClick ADD");

            if (mClickListener != null) {
                Food item = mData.get(getBindingAdapterPosition());
                if(mClickListener.onAdd(item) ) {
                    mData.remove(item);
                    notifyDataSetChanged();
                }
            }
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        boolean onAdd(Food item);
    }

}
