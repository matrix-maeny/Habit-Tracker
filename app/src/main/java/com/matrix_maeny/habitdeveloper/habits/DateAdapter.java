package com.matrix_maeny.habitdeveloper.habits;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.matrix_maeny.habitdeveloper.R;
import com.matrix_maeny.habitdeveloper.databinding.DateModelBinding;

import java.time.LocalDate;
import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.viewHolder> {

    private final Context context;
    private final List<DateModel> list;

    private final DateAdapterListener listener;

    public static DateModel dateModel;

    public DateAdapter(Context context, List<DateModel> list) {
        this.context = context;
        this.list = list;

        listener = (DateAdapterListener) context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.date_model, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        DateModel model = list.get(position);
        holder.binding.checkBox.setText("Day " + (position + 1));

        if (model.isCompleted()) {
            holder.binding.checkBox.setChecked(true);
            holder.binding.checkBox.setClickable(false);
        }

        holder.binding.checkBox.setOnClickListener(v -> {

            dateModel = model;
            if (!model.isCompleted()) {
                if (model.getDate().equals(LocalDate.now().toString())) {
                    model.setCompleted(true);
                    listener.setDateCompleted(HabitAdapter.trackModel.getName());
                }else{
                    holder.binding.checkBox.setChecked(false);
                    listener.showDayDialog();
//                    Toast.makeText(context, "Not today", Toast.LENGTH_SHORT).show();

                }
            }else {
                holder.binding.checkBox.setChecked(true);
                listener.showDayDialog();
//                Toast.makeText(context, "Completed", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface DateAdapterListener {
        void setDateCompleted(String habitName);
        void showDayDialog();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        DateModelBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = DateModelBinding.bind(itemView);
        }
    }

}
