package com.matrix_maeny.habitdeveloper.habits;

public class DateModel {

    private String date;
    private boolean isCompleted = false;

    public DateModel() {
    }

    public DateModel(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
