package com.happysanztech.mmm.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Admin on 08-01-2018.
 */

public class BatchData implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("batch_name")
    @Expose
    private String batchName;

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The batchName
     */
    public String getBatchName() {
        return batchName;
    }

    /**
     * @param batchName The batchName
     */
    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

}
