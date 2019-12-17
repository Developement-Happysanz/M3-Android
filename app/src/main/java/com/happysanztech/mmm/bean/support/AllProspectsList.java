package com.happysanztech.mmm.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AllProspectsList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("studentList")
    @Expose
    private ArrayList<AllProspects> allProspects = new ArrayList<AllProspects>();

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
     * @return The allProspects
     */
    public ArrayList<AllProspects> getAllProspects() {
        return allProspects;
    }

    /**
     * @param allProspects The allProspects
     */
    public void setAllProspects(ArrayList<AllProspects> allProspects) {
        this.allProspects = allProspects;
    }
}