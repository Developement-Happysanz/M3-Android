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

    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("task_title")
    @Expose
    private String taskTitle;

    @SerializedName("task_description")
    @Expose
    private String taskDescription;

    @SerializedName("task_date")
    @Expose
    private String taskDate;

    @SerializedName("pia_id")
    @Expose
    private String pia_id;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("assigned_to")
    @Expose
    private String assigned_to;

    @SerializedName("assigned_from")
    @Expose
    private String assigned_from;

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

    /**
     * @return The user_id
     */
    public String getUser_id() {
        return user_id;
    }

    /**
     * @param user_id The user_id
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /**
     * @return The pia_id
     */
    public String getPia_id() {
        return pia_id;
    }

    /**
     * @param pia_id The pia_id
     */
    public void setPia_id(String pia_id) {
        this.pia_id = pia_id;
    }

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return The assigned_to
     */
    public String getAssigned_to() {
        return assigned_to;
    }

    /**
     * @param assigned_to The assigned_to
     */
    public void setAssigned_to(String assigned_to) {
        this.assigned_to = assigned_to;
    }

    /**
     * @return The assigned_from
     */
    public String getAssigned_from() {
        return assigned_from;
    }

    /**
     * @param assigned_from The assigned_from
     */
    public void setAssigned_from(String assigned_from) {
        this.assigned_from = assigned_from;
    }

}
