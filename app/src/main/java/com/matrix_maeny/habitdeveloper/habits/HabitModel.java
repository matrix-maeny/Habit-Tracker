package com.matrix_maeny.habitdeveloper.habits;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HabitModel {

    private String name;
    private boolean isStarted;
    private int totalNoOfDays = 0;
    private List<DateModel> dateList = null;

    public HabitModel() {
    }

    public HabitModel(String name, int totalNoOfDays) {
        this.name = name;
        this.totalNoOfDays = totalNoOfDays;
        this.isStarted = false;


    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public List<DateModel> getDateList() {
        return dateList;
    }

    public void setDateList(List<DateModel> dateList) {
        this.dateList = dateList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalNoOfDays() {
        return totalNoOfDays;
    }

    public void setTotalNoOfDays(int totalNoOfDays) {
        this.totalNoOfDays = totalNoOfDays;
    }

    public void addDate(DateModel date){
        if(dateList == null) dateList = new ArrayList<>();
        dateList.add(date);
    }
}
