package com.happysanztech.mmm.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class WorkDetailsList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("result")
    @Expose
    private ArrayList<WorkDetails> categoryArrayList = new ArrayList<>();

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
    public ArrayList<WorkDetails> getCategoryArrayList() {
        return categoryArrayList;
    }

    /**
     * @param categoryArrayList The categoryArrayList
     */
    public void setCategoryArrayList(ArrayList<WorkDetails> categoryArrayList) {
        this.categoryArrayList = categoryArrayList;
    }
}