package com.github.calories.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private ItemClickListener mClickListener;

    private HashMap<Exercise, Boolean> selected = new HashMap();

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
            holder.title.setTextColor(ContextCompat.getColor(mInflater.getContext(),R.color.blue));
        }
        else
        {
            holder.title.setTextColor(ContextCompat.getColor(mInflater.getContext(),R.color.textColorPrimary));
        }

        // Setup position
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if(position%2==0)
            params.setMarginEnd(25);
        else
            params.setMarginStart(25);

        holder.cvContainer.setLayoutParams(params);
        holder.imageContainer.setImageBitmap(mData.get(position).getImage());

        GradientDrawable border = new GradientDrawable();

        if(selected.containsKey(mData.get(position)) && selected.get(mData.get(position)))
            border.setColor(ContextCompat.getColor(mInflater.getContext(),R.color.colorAccent));
        else
            border.setColor(Color.TRANSPARENT);

        holder.flContainer.setBackground(border);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return (mData == null)?0:mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener /*implements View.OnClickListener*/ {
        TextView title;
        ImageView imageContainer;
        CardView cvContainer;
        FrameLayout flContainer;

        ViewHolder(View itemView) {
            super(itemView);

            title = RowExerciseBinding.bind(itemView).title;
            imageContainer = RowExerciseBinding.bind(itemView).imageContainer;
            cvContainer = RowExerciseBinding.bind(itemView).cvContainer;
            flContainer = RowExerciseBinding.bind(itemView).clContainer;
            //layout = RowExerciseBinding.bind(itemView).layout;
            cvContainer.setOnClickListener(this);
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
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onSelect(Exercise exercise);
    }

}
