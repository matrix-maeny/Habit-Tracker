package com.matrix_maeny.habitdeveloper.habits;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.matrix_maeny.habitdeveloper.HabitTrackingActivity;
import com.matrix_maeny.habitdeveloper.R;
import com.matrix_maeny.habitdeveloper.databinding.HabitModelBinding;
import com.matrix_maeny.habitdeveloper.dialogs.HabitStartDialog;

import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.viewHolder> {

    private final Context context;
    private final List<HabitModel> list;

    private final HabitAdapterListener listener;

    public static HabitModel trackModel;


    public HabitAdapter(Context context, List<HabitModel> list) {
        this.context = context;
        this.list = list;

        listener = (HabitAdapterListener) context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.habit_model, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        HabitModel model = list.get(position);

        if (model.isStarted()) {
            holder.binding.indicatorTv.setTextColor(context.getResources().getColor(R.color.start_color));
        } else {
            holder.binding.indicatorTv.setTextColor(context.getResources().getColor(R.color.start_not_color));

        }

        holder.binding.nameTv.setText(model.getName());
        holder.binding.dayTv.setText(model.getTotalNoOfDays()+"");

        holder.binding.cardView.setOnClickListener(v -> {
            trackModel = model;

            if (model.isStarted()) {

                context.startActivity(new Intent(context.getApplicationContext(), HabitTrackingActivity.class));

            } else {
                HabitStartDialog.habitName = model.getName();
                listener.showHabitStartDialog();
            }

        });

        holder.binding.cardView.setOnLongClickListener(v -> {

            //hello

            PopupMenu popupMenu = new PopupMenu(context, holder.binding.cardView);
            popupMenu.getMenuInflater().inflate(R.menu.habit_popup_menu, popupMenu.getMenu());

            if (model.isStarted()) {
                popupMenu.getMenu().getItem(0).setTitle("Stop Tracking");
            } else popupMenu.getMenu().getItem(0).setVisible(false);

            popupMenu.setOnMenuItemClickListener(item -> {

                switch (item.getItemId()) {
                    case R.id.stop_tracking:
                        // stop tracking
                        listener.stopTracking(model.getName());
                        break;
                    case R.id.delete_habit:
                        // delete habit
                        listener.deleteHabit(model.getName());
                        break;
                }
                return true;
            });

            popupMenu.show();

            return true;
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface HabitAdapterListener {
        void showHabitStartDialog();
        void stopTracking(String habitName);
        void deleteHabit(String habitName);
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        HabitModelBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = HabitModelBinding.bind(itemView);
        }
    }


}
