package com.happysanztech.mmm.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Admin on 10-01-2018.
 */

public class TaskPicture implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("task_id")
    @Expose
    private String taskId;

    @SerializedName("task_image")
    @Expose
    private String taskImage;

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
     * @return The taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @param taskId The taskId
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * @return The taskImage
     */
    public String getTaskImage() {
        return taskImage;
    }

    /**
     * @param taskImage The taskImage
     */
    public void setTaskImage(String taskImage) {
        this.taskImage = taskImage;
    }

}
