package com.happysanztech.mmm.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WorkDetails implements Serializable {
    
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("mobilizer_id")
    @Expose
    private String mobilizer_id;
    
    @SerializedName("pia_id")
    @Expose
    private String pia_id;
    
    @SerializedName("work_type_id")
    @Expose
    private String work_type_id;
    
    @SerializedName("task_id")
    @Expose
    private String task_id;
    
    @SerializedName("title")
    @Expose
    private String title;
    
    @SerializedName("comments")
    @Expose
    private String comments;
    
    @SerializedName("mobilizer_comments")
    @Expose
    private String mobilizer_comments;
    
    @SerializedName("attendance_date")
    @Expose
    private String attendance_date;
    
    @SerializedName("created_at")
    @Expose
    private String created_at;
        
    @SerializedName("created_by")
    @Expose
    private String created_by;
        
    @SerializedName("work_type")
    @Expose
    private String work_type;
        
    @SerializedName("task_title")
    @Expose
    private String task_title;
    
    @SerializedName("status")
    @Expose
    private String status;

    /**
     * @return The id
     */
    public String getid() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setid(String id) {
        this.id = id;
    }

    /**
     * @return The mobilizer_id
     */
    public String getmobilizer_id() {
        return mobilizer_id;
    }

    /**
     * @param mobilizer_id The mobilizer_id
     */
    public void setmobilizer_id(String mobilizer_id) {
        this.mobilizer_id = mobilizer_id;
    }

    /**
     * @return The pia_id
     */
    public String getpia_id() {
        return pia_id;
    }

    /**
     * @param pia_id The pia_id
     */
    public void setpia_id(String pia_id) {
        this.pia_id = pia_id;
    }

    /**
     * @return The work_type_id
     */
    public String getwork_type_id() {
        return work_type_id;
    }

    /**
     * @param work_type_id The work_type_id
     */
    public void setwork_type_id(String work_type_id) {
        this.work_type_id = work_type_id;
    }

    /**
     * @return The task_id
     */
    public String gettask_id() {
        return task_id;
    }

    /**
     * @param task_id The task_id
     */
    public void settask_id(String task_id) {
        this.task_id = task_id;
    }

    /**
     * @return The title
     */
    public String gettitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void settitle(String title) {
        this.title = title;
    }

    /**
     * @return The comments
     */
    public String getcomments() {
        return comments;
    }

    /**
     * @param comments The comments
     */
    public void setcomments(String comments) {
        this.comments = comments;
    }

    /**
     * @return The mobilizer_comments
     */
    public String getmobilizer_comments() {
        return mobilizer_comments;
    }

    /**
     * @param mobilizer_comments The mobilizer_comments
     */
    public void setmobilizer_comments(String mobilizer_comments) {
        this.mobilizer_comments = mobilizer_comments;
    }

    /**
     * @return The attendance_date
     */
    public String getattendance_date() {
        return attendance_date;
    }

    /**
     * @param attendance_date The attendance_date
     */
    public void setattendance_date(String attendance_date) {
        this.attendance_date = attendance_date;
    }

    /**
     * @return The created_at
     */
    public String getcreated_at() {
        return created_at;
    }

    /**
     * @param created_at The created_at
     */
    public void setcreated_at(String created_at) {
        this.created_at = created_at;
    }

    /**
     * @return The created_by
     */
    public String getcreated_by() {
        return created_by;
    }

    /**
     * @param created_by The created_by
     */
    public void setcreated_by(String created_by) {
        this.created_by = created_by;
    }

    /**
     * @return The work_type
     */
    public String getwork_type() {
        return work_type;
    }

    /**
     * @param work_type The work_type
     */
    public void setwork_type(String work_type) {
        this.work_type = work_type;
    }

    /**
     * @return The task_title
     */
    public String gettask_title() {
        return task_title;
    }

    /**
     * @param task_title The task_title
     */
    public void settask_title(String task_title) {
        this.task_title = task_title;
    }

    /**
     * @return The status
     */
    public String getstatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setstatus(String status) {
        this.status = status;
    }

}