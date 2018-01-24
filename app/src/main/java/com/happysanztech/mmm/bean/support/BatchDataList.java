package com.happysanztech.mmm.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Admin on 08-01-2018.
 */

public class BatchDataList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("Batches")
    @Expose
    private ArrayList<BatchData> data = new ArrayList<BatchData>();

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
     * @return The BatchData
     */
    public ArrayList<BatchData> getBatchData() {
        return data;
    }

    /**
     * @param data The BatchData
     */
    public void setBatchData(ArrayList<BatchData> data) {
        this.data = data;
    }
}
