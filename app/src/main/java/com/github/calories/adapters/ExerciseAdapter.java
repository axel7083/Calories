package com.github.calories.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.calories.R;
import com.github.calories.databinding.RowExerciseBinding;
import com.github.calories.models.Exercise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private List<Exercise> mData;
    private LayoutInflater mInflater;
    private ItemClickListener longClickListener;

    private HashMap<Exercise, Boolean> selected = new HashMap();

    public void setSelected(Exercise exercise, Boolean value) {
        Log.d("ExerciseAdapter", "setSelected: " + exercise.getId() + " value " + value);
        selected.put(exercise, value);
    }

    public ExerciseAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    public void updateData(List<Exercise> mData)
    {
        this.mData = mData;
    }

    public List<Exercise> getData() {
        return mData;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_exercise, parent, false);

        return new ViewHolder(view);
    }

    public ArrayList<Exercise> getSelected() {
        ArrayList<Exercise> categories = new ArrayList<>();

        Iterator it = selected.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            if(((Boolean) pair.getValue())) {
                categories.add((Exercise) pair.getKey());
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
            holder.checkbox.setImageDrawable(ContextCompat.getDrawable(mInflater.getContext(),R.drawable.ic_circle));
        }
        else
        {
            holder.checkbox.setImageDrawable(ContextCompat.getDrawable(mInflater.getContext(),R.drawable.ic_checked));
        }

        holder.imageContainer.setImageBitmap(mData.get(position).getImage());

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return (mData == null)?0:mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener /*implements View.OnClickListener*/ {
        TextView title;
        ImageView imageContainer;
        ImageView checkbox;
        CardView cvContainer;


        ViewHolder(View itemView) {
            super(itemView);

            title = RowExerciseBinding.bind(itemView).title;
            imageContainer = RowExerciseBinding.bind(itemView).imageContainer;
            cvContainer = RowExerciseBinding.bind(itemView).cvContainer;
            checkbox = RowExerciseBinding.bind(itemView).checkbox;
            cvContainer.setOnClickListener(this);
            cvContainer.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if(selected.containsKey(mData.get(getBindingAdapterPosition())))
            {
                if(selected.get(mData.get(getBindingAdapterPosition())))
                    selected.put(mData.get(getBindingAdapterPosition()),false);
                else
                    selected.put(mData.get(getBindingAdapterPosition()),true);
            }
            else
            {
                selected.put(mData.get(getBindingAdapterPosition()),true);
            }
            notifyDataSetChanged();

        }

        @Override
        public boolean onLongClick(View view) {
            if(longClickListener != null) {
                longClickListener.onLongClick(mData.get(getBindingAdapterPosition()));
            }
            return false;
        }

        /*@Override
        public void onClick(View view) {

            Exercise exercise = mData.get(getBindingAdapterPosition());
            if(selected.containsKey(exercise) && selected.get(exercise)) {
                title.setTextColor(ContextCompat.getColor(mInflater.getContext(),R.color.textColorPrimary));
                selected.put(exercise, false);
            }
            else
            {
                title.setTextColor(ContextCompat.getColor(mInflater.getContext(),R.color.blue));
                selected.put(exercise, true);
            }

            if (mClickListener == null)
                return;

            mClickListener.onSelect(mData.get(getBindingAdapterPosition()));
        }*/
    }

    // allows clicks events to be caught
    public void setLongClickListener(ItemClickListener itemClickListener) {
        this.longClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onLongClick(Exercise exercise);
    }

}
