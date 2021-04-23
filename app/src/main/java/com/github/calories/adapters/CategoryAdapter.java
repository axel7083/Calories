package com.github.calories.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.calories.R;
import com.github.calories.databinding.RowCategoryBinding;
import com.github.calories.models.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private HashMap<Category, Boolean> selected = new HashMap();

    public CategoryAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    public void updateData(List<Category> mData)
    {
        this.mData = mData;
    }

    public List<Category> getData() {
        return mData;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_category, parent, false);

        return new ViewHolder(view);
    }

    public ArrayList<Category> getSelected() {
        ArrayList<Category> categories = new ArrayList<>();

        Iterator it = selected.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            if(((Boolean) pair.getValue())) {
                categories.add((Category) pair.getKey());
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        return categories;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(mData.get(position).getName());

        if(selected.containsKey(mData.get(position)) && selected.get(mData.get(position))) {
            holder.title.setTextColor(ContextCompat.getColor(mInflater.getContext(),R.color.blue));
        }
        else
        {
            holder.title.setTextColor(ContextCompat.getColor(mInflater.getContext(),R.color.textColorPrimary));
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return (mData == null)?0:mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        LinearLayout layout;

        ViewHolder(View itemView) {
            super(itemView);

            title = RowCategoryBinding.bind(itemView).title;
            layout = RowCategoryBinding.bind(itemView).layout;
            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            Category cat = mData.get(getBindingAdapterPosition());
            if(selected.containsKey(cat) && selected.get(cat)) {
                title.setTextColor(ContextCompat.getColor(mInflater.getContext(),R.color.textColorPrimary));
                selected.put(cat, false);
            }
            else
            {
                title.setTextColor(ContextCompat.getColor(mInflater.getContext(),R.color.blue));
                selected.put(cat, true);
            }

            if (mClickListener == null)
                return;

            mClickListener.onSelect(mData.get(getBindingAdapterPosition()));
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onSelect(Category category);
    }

}
