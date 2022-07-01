package com.matrix_maeny.habitdeveloper.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.matrix_maeny.habitdeveloper.HabitTrackingActivity;
import com.matrix_maeny.habitdeveloper.R;
import com.matrix_maeny.habitdeveloper.databinding.HabitStartDialogBinding;

public class HabitStartDialog extends AppCompatDialogFragment {

    private HabitStartDialogBinding binding;

    private HabitStartDialogListener listener;

    public static String habitName;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        ContextThemeWrapper wrapper = new ContextThemeWrapper(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert);
        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);

        @SuppressLint("InflateParams") View root = requireActivity().getLayoutInflater().inflate(R.layout.habit_start_dialog, null);
        binding = HabitStartDialogBinding.bind(root);
        builder.setView(binding.getRoot());

        listener = (HabitStartDialogListener) requireContext();

        binding.yesBtn.setOnClickListener(v -> {
           listener.setDatesAndStartTracking(habitName);
           dismiss();
        });

        return builder.create();
    }

    public interface HabitStartDialogListener{
        void setDatesAndStartTracking(String habitName);
    }
}
