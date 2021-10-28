package com.github.calories.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.calories.R;
import com.github.calories.databinding.RowExerciseDetailedBinding;
import com.github.calories.databinding.RowSetBinding;
import com.github.calories.models.Exercise;
import com.github.calories.models.ExerciseInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExerciseInputAdapter extends RecyclerView.Adapter<ExerciseInputAdapter.ViewHolder> {

    private List<ExerciseInput> mData = new ArrayList<>();
    private LayoutInflater mInflater;

    public ExerciseInputAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    public void updateData(List<ExerciseInput> mData)
    {
        this.mData = mData;
        notifyDataSetChanged();
    }

    public List<ExerciseInput> getData() {
        return mData;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_set, parent, false);
        return new ViewHolder(view);
    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.weight.setText(mData.get(position).getWeight() + "");
        holder.rep.setText(mData.get(position).getRepetition() + "");
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return (mData == null)?0:mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleWeight;
        TextView titleRep;
        EditText weight;
        EditText rep;
        ImageView delete;

        ViewHolder(View itemView) {
            super(itemView);

            titleWeight = RowSetBinding.bind(itemView).titleWeight;
            titleRep = RowSetBinding.bind(itemView).titleRep;
            weight = RowSetBinding.bind(itemView).weightInput;
            rep = RowSetBinding.bind(itemView).repetitionInput;
            delete = RowSetBinding.bind(itemView).delete;

            weight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    try{
                        getData().get(getBindingAdapterPosition()).setWeight(Integer.parseInt(editable.toString()));
                    }
                    catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            });

            rep.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    try{
                        getData().get(getBindingAdapterPosition()).setRepetition(Integer.parseInt(editable.toString()));
                    }
                    catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            });

            delete.setOnClickListener(view -> {
                getData().remove(getBindingAdapterPosition());
                notifyDataSetChanged();
            });


            //TODO: make everything clickable
        }

        @Override
        public void onClick(View view) {

        }

    }


}
