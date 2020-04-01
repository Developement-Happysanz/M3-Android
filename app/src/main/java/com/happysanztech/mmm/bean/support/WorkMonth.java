package com.happysanztech.mmm.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WorkMonth implements Serializable {

    @SerializedName("month_id")
    @Expose
    private String month_id;

    @SerializedName("month_name")
    @Expose
    private String month_name;

    /**
     * @return The month_id
     */
    public String getmonth_id() {
        return month_id;
    }

    /**
     * @param month_id The month_id
     */
    public void setmonth_id(String month_id) {
        this.month_id = month_id;
    }

    /**
     * @return The month_name
     */
    public String getmonth_name() {
        return month_name;
    }

    /**
     * @param month_name The month_name
     */
    public void setmonth_name(String month_name) {
        this.month_name = month_name;
    }

}