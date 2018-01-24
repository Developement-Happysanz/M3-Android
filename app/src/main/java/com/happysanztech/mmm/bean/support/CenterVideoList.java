package com.happysanztech.mmm.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Admin on 11-01-2018.
 */

public class CenterVideoList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("Videos")
    @Expose
    private ArrayList<CenterVideo> data = new ArrayList<CenterVideo>();

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
     * @return The CenterVideo
     */
    public ArrayList<CenterVideo> getCenterVideo() {
        return data;
    }

    /**
     * @param data The CenterVideo
     */
    public void setCenterVideo(ArrayList<CenterVideo> data) {
        this.data = data;
    }

}
