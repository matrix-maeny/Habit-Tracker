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

import com.matrix_maeny.habitdeveloper.R;
import com.matrix_maeny.habitdeveloper.databinding.DayCompleteDialogBinding;
import com.matrix_maeny.habitdeveloper.habits.DateAdapter;

public class DayCompleteDialog extends AppCompatDialogFragment {

    private DayCompleteDialogBinding binding;



    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        ContextThemeWrapper wrapper = new ContextThemeWrapper(requireContext(), androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert);
        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);

        @SuppressLint("InflateParams") View root = requireActivity().getLayoutInflater().inflate(R.layout.day_complete_dialog, null);
        binding = DayCompleteDialogBinding.bind(root);
        builder.setView(binding.getRoot());


        binding.dateTv.setText(DateAdapter.dateModel.getDate());

        if(DateAdapter.dateModel.isCompleted()){
            binding.statusTv.setText("Status: Completed");
        }else binding.statusTv.setText("Status: Pending, Not Today");


        return builder.create();
    }
}
