package com.github.calories.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.calories.R;
import com.github.calories.databinding.RowFoodBinding;
import com.github.calories.models.Food;
import com.github.calories.models.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//TODO: use view binding
public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private List<Food> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private boolean isReadOnly = false;

    // data is passed into the constructor
    public FoodAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    public void updateData(List<Food> mData)
    {
        this.mData = mData;
    }

    public void addFood(Food food) {
        if(mData == null)
            mData= new ArrayList<>();
        mData.add(food);
    }

    public void setReadOnly(boolean val) {
        isReadOnly = val;
    }

    public List<Food> getData() {
        return mData;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_food, parent, false);
        return new ViewHolder(view);
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
        else
            holder.description.setText(mData.get(position).getName());

        holder.amount_input.setEnabled(!isReadOnly);
        holder.amount_input.setText(String.format(Locale.getDefault(),"%d",mData.get(position).getQuantity()));
        //holder.delete.setVisibility(isReadOnly?View.INVISIBLE:View.VISIBLE);

        //holder.myTextView.setText(mData.get(position).getName());
        //holder.smiley.setText(mData.get(position).getSmiley());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return (mData == null)?0:mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, TextWatcher {
        TextView title;
        TextView description;
        EditText amount_input;
        ImageView delete;

        ViewHolder(View itemView) {
            super(itemView);

            title = RowFoodBinding.bind(itemView).titleRow;
            description = RowFoodBinding.bind(itemView).descriptionRow;
            amount_input = RowFoodBinding.bind(itemView).amountInput;
            delete = RowFoodBinding.bind(itemView).deleteRow;

            amount_input.addTextChangedListener(this);
            delete.setOnClickListener(this);
            RowFoodBinding.bind(itemView).layoutRow.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("FoodAdapter","onClick DELETE");
            if (mClickListener == null)
                return;

            if(view.getId() == R.id.delete_row) {
                mClickListener.onRemove(mData.get(getBindingAdapterPosition()));
            }
            else if(view.getId() == R.id.layout_row) {
                mClickListener.onOpen(mData.get(getBindingAdapterPosition()));
            }
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if(charSequence == null || charSequence.length() == 0) {
                mData.get(getBindingAdapterPosition()).setQuantity(0);
            }
            else
            {
                try {
                    mData.get(getBindingAdapterPosition()).setQuantity(Integer.parseInt(charSequence.toString()));
                }
                catch (NumberFormatException ignored) {
                    mData.get(getBindingAdapterPosition()).setQuantity(0);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) { }
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
