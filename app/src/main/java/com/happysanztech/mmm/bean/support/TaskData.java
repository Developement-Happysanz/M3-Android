package com.happysanztech.mmm.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Admin on 08-01-2018.
 */

public class TaskData implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("task_title")
    @Expose
    private String taskTitle;

    @SerializedName("task_description")
    @Expose
    private String taskDescription;

    @SerializedName("task_date")
    @Expose
    private String taskDate;

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
     * @return The taskTitle
     */
    public String getTaskTitle() {
        return taskTitle;
    }

    /**
     * @param taskTitle The taskTitle
     */
    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    /**
     * @return The taskDescription
     */
    public String getTaskDescription() {
        return taskDescription;
    }

    /**
     * @param taskDescription The taskDescription
     */
    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    /**
     * @return The taskDate
     */
    public String getTaskDate() {
        return taskDate;
    }

    /**
     * @param taskDate The taskDate
     */
    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

}
