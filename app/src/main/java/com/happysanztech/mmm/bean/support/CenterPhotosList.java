package com.happysanztech.mmm.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Admin on 11-01-2018.
 */

public class CenterPhotosList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("Photo")
    @Expose
    private ArrayList<CenterPhotosData> data = new ArrayList<CenterPhotosData>();

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
     * @return The CenterPhotosData
     */
    public ArrayList<CenterPhotosData> getCenterPhotosData() {
        return data;
    }

    /**
     * @param data The CenterPhotosData
     */
    public void setCenterPhotosData(ArrayList<CenterPhotosData> data) {
        this.data = data;
    }
}
