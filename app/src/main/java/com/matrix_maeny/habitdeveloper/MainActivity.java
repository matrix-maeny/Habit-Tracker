package com.matrix_maeny.habitdeveloper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.matrix_maeny.habitdeveloper.databinding.ActivityMainBinding;
import com.matrix_maeny.habitdeveloper.dialogs.AddHabitDialog;
import com.matrix_maeny.habitdeveloper.dialogs.HabitStartDialog;
import com.matrix_maeny.habitdeveloper.habits.DateModel;
import com.matrix_maeny.habitdeveloper.habits.HabitAdapter;
import com.matrix_maeny.habitdeveloper.habits.HabitModel;
import com.matrix_maeny.habitdeveloper.registerActivities.LoginActivity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AddHabitDialog.AddHabitDialogListener,
        HabitAdapter.HabitAdapterListener, HabitStartDialog.HabitStartDialogListener {

    private ActivityMainBinding binding;
    private ExposeDialogs exposeDialogs;

    public static FirebaseDatabase firebaseDatabase;
    public static String currentUserUid;


    private HabitAdapter adapter;
    private List<HabitModel> list;

    private boolean newlyStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(MainActivity.this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());

        setSupportActionBar(binding.toolbar);

        initialize();

        binding.swipeRefreshLayout.setOnRefreshListener(this::fetchHabits);
    }


    private void initialize() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        currentUserUid = FirebaseAuth.getInstance().getUid();

        exposeDialogs = new ExposeDialogs(MainActivity.this);

        list = new ArrayList<>();
        adapter = new HabitAdapter(MainActivity.this, list);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        binding.recyclerView.setAdapter(adapter);

        fetchHabits();
    }

    private void fetchHabits() {
        exposeDialogs.showProgressDialog("Fetching Data...", "Please wait");

        final DatabaseReference habitRef = firebaseDatabase.getReference().child("Habits").child(currentUserUid);

        habitRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                if (snapshot.exists()) {

                    for (DataSnapshot s : snapshot.getChildren()) {
                        HabitModel model = s.getValue(HabitModel.class);

                        if (model != null) list.add(model);
                    }

                }

                binding.swipeRefreshLayout.setRefreshing(false);
                exposeDialogs.dismissProgressDialog();
                refreshAdapter();

                if (newlyStart)
                    exposeDialogs.showToast("click again to start",1);
                newlyStart = false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                exposeDialogs.dismissProgressDialog();
                exposeDialogs.showToast(error.getMessage(), 0);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshAdapter() {
        adapter.notifyDataSetChanged();

        if (list.isEmpty()) binding.emptyTv.setVisibility(View.VISIBLE);
        else binding.emptyTv.setVisibility(View.GONE);

    }


    @Override
    public void addHabit(String name, int days) {
        exposeDialogs.showProgressDialog("Creating Habit..", "Please wait few seconds...");

        final DatabaseReference habitRef = firebaseDatabase.getReference().child("Habits").child(currentUserUid);

        habitRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (checkIfNameExists(snapshot, name)) return;
                }

                HabitModel newHabit = new HabitModel(name, days);
                uploadHabit(newHabit, habitRef);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                exposeDialogs.dismissProgressDialog();
                exposeDialogs.showToast(error.getMessage(), 0);
            }
        });
    }

    private boolean checkIfNameExists(@NonNull DataSnapshot snapshot, String name) {
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

            HabitModel habitModel = dataSnapshot.getValue(HabitModel.class);

            if (habitModel != null && habitModel.getName().equals(name)) {
                exposeDialogs.dismissProgressDialog();
                exposeDialogs.showToast("Habit already exists", 1);
                return true;
            }
        }
        return false;
    }

    private void uploadHabit(@NonNull HabitModel newHabit, @NonNull DatabaseReference habitRef) {
        habitRef.child(newHabit.getName()).setValue(newHabit).addOnCompleteListener(task -> {

                    exposeDialogs.dismissProgressDialog();
                    if (task.isSuccessful()) {
                        exposeDialogs.showToast("Habit created", 1);
                    } else {
                        exposeDialogs.showToast(Objects.requireNonNull(task.getException()).getMessage(), 1);
                    }

                    fetchHabits();

                })
                .addOnFailureListener(e -> {
                    exposeDialogs.dismissProgressDialog();
                    exposeDialogs.showToast(e.getMessage(), 0);
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_habit:
                showAddHabitDialog();
                break;
            case R.id.log_out:
                logout();
                break;
            case R.id.clear_all:
                clearAllHabits();
                break;
            case R.id.about_app:
                startActivity(new Intent(MainActivity.this,AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearAllHabits() {

        exposeDialogs.showProgressDialog("Deleting Habits...", "Please wait...");
        final DatabaseReference habitRef = firebaseDatabase.getReference().child("Habits").child(currentUserUid);//.child(habitName);
        habitRef.removeValue().addOnCompleteListener(task -> {

            exposeDialogs.dismissProgressDialog();

            if (task.isSuccessful()) {
                exposeDialogs.showToast("All Deleted", 1);
                fetchHabits();
            } else
                exposeDialogs.showToast(Objects.requireNonNull(task.getException()).getMessage(), 1);

        }).addOnFailureListener(e -> {
            exposeDialogs.dismissProgressDialog();
            exposeDialogs.showToast(e.getMessage(), 0);
        });
    }

    private void showAddHabitDialog() {
        AddHabitDialog dialog = new AddHabitDialog();
        dialog.show(getSupportFragmentManager(), "Add Habit Dialog");
    }


    public void logout() {
        FirebaseAuth.getInstance().signOut();

        exposeDialogs.showProgressDialog("Logging out", "Please wait");

        new Handler().postDelayed(() -> {
            exposeDialogs.dismissProgressDialog();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }, 1500);
    }


    @Override
    public void showHabitStartDialog() {
        HabitStartDialog dialog = new HabitStartDialog();
        dialog.show(getSupportFragmentManager(), "Habit start dialog");
    }

    @Override
    public void stopTracking(String habitName) {
        exposeDialogs.showProgressDialog("Stopping Track...", "Please wait...");

        final DatabaseReference habitRef = firebaseDatabase.getReference().child("Habits").child(currentUserUid).child(habitName);
        habitRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    HabitModel habitModel = snapshot.getValue(HabitModel.class);

                    if (habitModel != null) {
                        habitModel.setStarted(false);
                        habitModel.getDateList().clear();
                    }
                    updateAndStart(habitModel, habitRef);

                } else exposeDialogs.dismissProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                exposeDialogs.dismissProgressDialog();
                exposeDialogs.showToast(error.getMessage(), 0);
            }
        });
    }

    @Override
    public void deleteHabit(String habitName) {
        exposeDialogs.showProgressDialog("Stopping Track...", "Please wait...");

        final DatabaseReference habitRef = firebaseDatabase.getReference().child("Habits").child(currentUserUid).child(habitName);
        habitRef.removeValue().addOnCompleteListener(task -> {
            exposeDialogs.dismissProgressDialog();
            if (task.isSuccessful()) {
                exposeDialogs.showToast("Habit deleted", 1);
                fetchHabits();
            } else {
                exposeDialogs.showToast(Objects.requireNonNull(task.getException()).getMessage(), 0);
            }
        }).addOnFailureListener(e -> {
            exposeDialogs.dismissProgressDialog();
            exposeDialogs.showToast(e.getMessage(), 0);
        });
    }

    @Override
    public void setDatesAndStartTracking(String habitName) {
        exposeDialogs.showProgressDialog("Initializing data..", "Please wait...");

        final DatabaseReference habitRef = firebaseDatabase.getReference().child("Habits").child(currentUserUid).child(habitName);

        habitRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HabitModel habitModel = snapshot.getValue(HabitModel.class);

                    if (habitModel != null) {

                        for (int i = 0; i < habitModel.getTotalNoOfDays(); i++) {
                            habitModel.addDate(new DateModel(LocalDate.now().plusDays(i).toString()));
                        }

                        habitModel.setStarted(true);
                        updateAndStart(habitModel, habitRef);
                    }
                }//else exposeDialogs.dismissProgressDialog();

//                exposeDialogs.dismissProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                exposeDialogs.dismissProgressDialog();
                exposeDialogs.showToast(error.getMessage(), 0);
            }
        });

    }

    private void updateAndStart(HabitModel habitModel, @NonNull DatabaseReference habitRef) {
        habitRef.setValue(habitModel).addOnCompleteListener(task -> {
            exposeDialogs.dismissProgressDialog();
            if (task.isSuccessful()) {

                if (habitModel.isStarted()) {
                    exposeDialogs.showToast("Habit started", 1);
//                    startActivity(new Intent(MainActivity.this, HabitTrackingActivity.class));
                    newlyStart = true;
                } else
                    exposeDialogs.showToast("Habit stopped", 1);
                fetchHabits();


            } else
                exposeDialogs.showToast(Objects.requireNonNull(task.getException()).getMessage(), 1);

        }).addOnFailureListener(e -> {
            exposeDialogs.dismissProgressDialog();
            exposeDialogs.showToast(e.getMessage(), 1);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchHabits();
    }
}