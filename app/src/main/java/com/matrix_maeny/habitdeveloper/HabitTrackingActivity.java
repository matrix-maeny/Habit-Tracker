package com.matrix_maeny.habitdeveloper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.database.DatabaseReference;
import com.matrix_maeny.habitdeveloper.databinding.ActivityHabitTrackingBinding;
import com.matrix_maeny.habitdeveloper.databinding.HabitCompletedDialogBinding;
import com.matrix_maeny.habitdeveloper.dialogs.CongratulationsDialog;
import com.matrix_maeny.habitdeveloper.dialogs.DayCompleteDialog;
import com.matrix_maeny.habitdeveloper.dialogs.HabitCompletedDialog;
import com.matrix_maeny.habitdeveloper.habits.DateAdapter;
import com.matrix_maeny.habitdeveloper.habits.DateModel;
import com.matrix_maeny.habitdeveloper.habits.HabitAdapter;

import java.util.List;
import java.util.Objects;

public class HabitTrackingActivity extends AppCompatActivity implements DateAdapter.DateAdapterListener {

    private ActivityHabitTrackingBinding binding;

    private ExposeDialogs exposeDialogs;

    private DateAdapter adapter;
    private List<DateModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHabitTrackingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(HabitTrackingActivity.this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());


        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Track: " + HabitAdapter.trackModel.getName());

        initialize();
    }

    private void initialize() {
        exposeDialogs = new ExposeDialogs(HabitTrackingActivity.this);
        list = HabitAdapter.trackModel.getDateList();

        adapter = new DateAdapter(HabitTrackingActivity.this, HabitAdapter.trackModel.getDateList());

        binding.recyclerView.setLayoutManager(new GridLayoutManager(HabitTrackingActivity.this, 3));
        binding.recyclerView.setAdapter(adapter);

    }

    @Override
    public void setDateCompleted(String habitName) {
        exposeDialogs.showProgressDialog("Updating...", "wait...");

        final DatabaseReference habitRef = MainActivity.firebaseDatabase.getReference()
                .child("Habits").child(MainActivity.currentUserUid)
                .child(habitName);

        if (HabitAdapter.trackModel.getDateList().get(HabitAdapter.trackModel.getDateList().size() - 1).isCompleted()) {
            HabitAdapter.trackModel.getDateList().clear();
            HabitAdapter.trackModel.setStarted(false);
            uploadData(habitRef,true);
        } else
            uploadData(habitRef, false);


    }

    @Override
    public void showDayDialog() {
        DayCompleteDialog dialog = new DayCompleteDialog();
        dialog.show(getSupportFragmentManager(),"Day Complete Dialog");
    }

    private void uploadData(@NonNull DatabaseReference habitRef, boolean isCompleted) {
        habitRef.setValue(HabitAdapter.trackModel).addOnCompleteListener(task -> {

            exposeDialogs.dismissProgressDialog();

            if (task.isSuccessful()) {
                if (isCompleted) {

                    showHabitCompletedDialog();

                } else {
                    showCongratulationsDialog();
//                    exposeDialogs.showToast("...Congratulations...,You are progressing ", 1);
                }

            } else
                exposeDialogs.showToast(Objects.requireNonNull(task.getException()).getMessage(), 1);


        }).addOnFailureListener(e -> {
            exposeDialogs.dismissProgressDialog();
            exposeDialogs.showToast(e.getMessage(), 1);
        });
    }

    private void showCongratulationsDialog() {
        CongratulationsDialog dialog = new CongratulationsDialog();
        dialog.show(getSupportFragmentManager(),"Congratulations dialog");
    }

    private void showHabitCompletedDialog() {
        HabitCompletedDialog dialog = new HabitCompletedDialog();
        dialog.show(getSupportFragmentManager(),"Habit complete dialog");
    }
}