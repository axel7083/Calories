package com.github.calories.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.calories.R;
import com.github.calories.activities.FoodDetailsActivity;
import com.github.calories.databinding.RowRecordBinding;
import com.github.calories.models.Food;
import com.github.calories.models.Record;
import com.github.calories.utils.UtilsTime;
import com.google.gson.Gson;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import static com.github.calories.utils.UtilsTime.format;
import static com.github.calories.utils.UtilsTime.toCalendar;

//TODO: use view binding
public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    private List<Record> mData;
    private LayoutInflater mInflater;
    private OnRemoveListener removeListener;


    // data is passed into the constructor
    public RecordAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    public void updateData(List<Record> mData)
    {
        this.mData = mData;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_record, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            holder.title.setText(format(toCalendar(mData.get(position).getDate(), UtilsTime.SQL_PATTERN).toInstant(),TimeZone.getDefault().getID(),UtilsTime.SIMPLE_PATTERN));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ((FoodAdapter) holder.rv.getAdapter()).updateData(mData.get(position).getFoods());
        ((FoodAdapter) holder.rv.getAdapter()).notifyDataSetChanged();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return (mData == null)?0:mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements FoodAdapter.ItemClickListener {
        TextView title;
        RecyclerView rv;
        FoodAdapter adapter;

        ViewHolder(View itemView) {
            super(itemView);

            title = RowRecordBinding.bind(itemView).recordTitle;
            rv = RowRecordBinding.bind(itemView).rvFood;
            rv.setLayoutManager(new LinearLayoutManager(mInflater.getContext()));

            adapter = new FoodAdapter(mInflater.getContext());
            adapter.setReadOnly(true);
            adapter.setClickListener(this);
            rv.setAdapter(adapter);
        }

        @Override
        public void onRemove(Food food) {
            int pos = getBindingAdapterPosition();
            mData.get(pos).removeFood(food);

            if(removeListener == null)
                return;

            if(removeListener.onFoodRemoveListener(mData.get(pos).getId() + "", food.getId())) {
                adapter.updateData(mData.get(pos).removeFood(food));
            }

            if(Objects.requireNonNull(mData.get(pos).getFoods()).size() == 0) {
                if(removeListener.onRecordRemoveListener(mData.get(pos).getId() + "")) {
                    mData.remove(pos);
                    notifyDataSetChanged();
                }
            }
            else
            {
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onOpen(Food food) {
            Intent intent = new Intent(mInflater.getContext(), FoodDetailsActivity.class);
            intent.putExtra("food",new Gson().toJson(food));
            mInflater.getContext().startActivity(intent);
        }
    }

    public void setRemoveListener(OnRemoveListener removeListener) {
        this.removeListener = removeListener;
    }

    public interface OnRemoveListener {
        Boolean onRecordRemoveListener(String record_id);
        Boolean onFoodRemoveListener(String record_id, String food_id);
    }

}
