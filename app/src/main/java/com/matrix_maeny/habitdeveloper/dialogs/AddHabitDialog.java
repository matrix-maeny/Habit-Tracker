package com.matrix_maeny.habitdeveloper.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.matrix_maeny.habitdeveloper.ExposeDialogs;
import com.matrix_maeny.habitdeveloper.R;
import com.matrix_maeny.habitdeveloper.databinding.AddHabitDialogBinding;

import java.util.Objects;

public class AddHabitDialog extends AppCompatDialogFragment {

    private AddHabitDialogBinding binding;
    private String name;
    private ExposeDialogs exposeDialogs;
    private int days = 21;

    private AddHabitDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        ContextThemeWrapper wrapper = new ContextThemeWrapper(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert);
        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);

        @SuppressLint("InflateParams") View root = requireActivity().getLayoutInflater().inflate(R.layout.add_habit_dialog, null);
        binding = AddHabitDialogBinding.bind(root);
        builder.setView(binding.getRoot());

        exposeDialogs = new ExposeDialogs(requireContext());
        listener = (AddHabitDialogListener) requireContext();


        binding.day21Cb.setChecked(true);

        binding.day21Cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.day45Cb.setChecked(false);
                days = 21;
            }
        });
        binding.day45Cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.day21Cb.setChecked(false);
                days = 45;
            }
        });

        binding.createBtn.setOnClickListener(v -> {
            if(checkName()){
                listener.addHabit(name,days);
                dismiss();
            }
        });

        return builder.create();
    }

    private boolean checkName() {
        name = null;

        try {
            name = Objects.requireNonNull(binding.createHabitNameEt.getText()).toString().trim();
            if (!name.equals(""))
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        exposeDialogs.showToast("Please enter Name", 1);

        return false;
    }

    public interface AddHabitDialogListener {
        void addHabit(String name, int days);
    }
}
