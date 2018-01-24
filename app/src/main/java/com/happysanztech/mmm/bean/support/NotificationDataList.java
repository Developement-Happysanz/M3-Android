package com.happysanztech.mmm.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Admin on 11-01-2018.
 */

public class NotificationDataList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("circularDetails")
    @Expose
    private ArrayList<NotificationData> data = new ArrayList<NotificationData>();

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
     * @return The NotificationData
     */
    public ArrayList<NotificationData> getNotificationData() {
        return data;
    }

    /**
     * @param data The NotificationData
     */
    public void setNotificationData(ArrayList<NotificationData> data) {
        this.data = data;
    }
}
