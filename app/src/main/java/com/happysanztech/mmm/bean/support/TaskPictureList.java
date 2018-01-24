package com.happysanztech.mmm.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Admin on 11-01-2018.
 */

public class TaskPictureList {
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("Taskpictures")
    @Expose
    private ArrayList<TaskPicture> data = new ArrayList<TaskPicture>();

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
     * @return The TaskPicture
     */
    public ArrayList<TaskPicture> getTaskPicture() {
        return data;
    }

    /**
     * @param data The TaskPicture
     */
    public void setTaskPicture(ArrayList<TaskPicture> data) {
        this.data = data;
    }
}
