package com.happysanztech.mmm.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Admin on 11-01-2018.
 */

public class NotificationData implements Serializable {

    @SerializedName("circular_title")
    @Expose
    private String circularTitle;

    @SerializedName("circular_description")
    @Expose
    private String circularDescription;

    /**
     * @return The circularTitle
     */
    public String getCircularTitle() {
        return circularTitle;
    }

    /**
     * @param circularTitle The circularTitle
     */
    public void setCircularTitle(String circularTitle) {
        this.circularTitle = circularTitle;
    }

    /**
     * @return The circularDescription
     */
    public String getCircularDescription() {
        return circularDescription;
    }

    /**
     * @param circularDescription The circularDescription
     */
    public void setCircularDescription(String circularDescription) {
        this.circularDescription = circularDescription;
    }
}
