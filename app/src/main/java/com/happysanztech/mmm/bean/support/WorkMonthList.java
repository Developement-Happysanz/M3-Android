package com.happysanztech.mmm.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class WorkMonthList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("result")
    @Expose
    private ArrayList<WorkMonth> categoryArrayList = new ArrayList<>();

    /**
     * @return The count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count The count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return The categoryArrayList
     */
    public ArrayList<WorkMonth> getCategoryArrayList() {
        return categoryArrayList;
    }

    /**
     * @param categoryArrayList The categoryArrayList
     */
    public void setCategoryArrayList(ArrayList<WorkMonth> categoryArrayList) {
        this.categoryArrayList = categoryArrayList;
    }
}