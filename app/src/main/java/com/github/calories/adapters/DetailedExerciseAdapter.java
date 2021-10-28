package com.github.calories.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.calories.R;
import com.github.calories.databinding.RowExerciseBinding;
import com.github.calories.databinding.RowExerciseDetailedBinding;
import com.github.calories.models.Exercise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DetailedExerciseAdapter extends RecyclerView.Adapter<DetailedExerciseAdapter.ViewHolder> {

    private List<Exercise> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private ClickEvent clickEvent;

    public DetailedExerciseAdapter(Context context, ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
        this.mInflater = LayoutInflater.from(context);
    }

    public void updateData(List<Exercise> mData)
    {
        System.out.println("DetailedExerciseAdapter received "+ mData.size());
        this.mData = mData;
        notifyDataSetChanged();
    }

    public List<Exercise> getData() {
        return mData;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_exercise_detailed, parent, false);
        return new ViewHolder(view);
    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(mData.get(position).getName());
        holder.description.setText(mData.get(position).getDesc());
        holder.imageContainer.setImageBitmap(mData.get(position).getImage());
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
        ImageView imageContainer;
        LinearLayoutCompat container;


        ViewHolder(View itemView) {
            super(itemView);

            title = RowExerciseDetailedBinding.bind(itemView).titleRow;
            description = RowExerciseDetailedBinding.bind(itemView).descriptionRow;
            imageContainer = RowExerciseDetailedBinding.bind(itemView).imageContainer;
            container = RowExerciseDetailedBinding.bind(itemView).container;

            container.setOnClickListener(this);

            //TODO: make everything clickable
        }

        @Override
        public void onClick(View view) {
            if(clickEvent != null)
                clickEvent.onClick(getData().get(getBindingAdapterPosition()));
        }

    }

    public interface ClickEvent {
        void onClick(Exercise exercise);
    }
}
