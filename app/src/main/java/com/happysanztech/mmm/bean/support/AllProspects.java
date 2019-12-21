package com.happysanztech.mmm.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AllProspects  implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("sex")
    @Expose
    private String sex;

    @SerializedName("mobile")
    @Expose
    private String mobile;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("enrollment")
    @Expose
    private String enrollment;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("profile_pic")
    @Expose
    private String profile_pic;

    @SerializedName("aadhaar_card_number")
    @Expose
    private String aadhaar_card_number;

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
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The sex
     */
    public String getSex() {
        return sex;
    }

    /**
     * @param sex The sex
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * @return The mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile The mobile
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The enrollment
     */
    public String getEnrollment() {
        return enrollment;
    }

    /**
     * @param enrollment The enrollment
     */
    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
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
     * @return The profile_pic
     */
    public String getProfile_pic() {
        return profile_pic;
    }

    /**
     * @param profile_pic The profile_pic
     */
    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    /**
     * @return The aadhaar_card_number
     */
    public String getAadhaar_card_number() {
        return aadhaar_card_number;
    }

    /**
     * @param aadhaar_card_number The aadhaar_card_number
     */
    public void setAadhaar_card_number(String aadhaar_card_number) {
        this.aadhaar_card_number = aadhaar_card_number;
    }

}