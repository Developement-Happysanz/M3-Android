package com.happysanztech.mmm.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 11-01-2018.
 */

public class CenterVideo {

    @SerializedName("video_title")
    @Expose
    private String videoTitle;

    @SerializedName("video_url")
    @Expose
    private String videoURL;

    /**
     * @return The videoTitle
     */
    public String getVideoTitle() {
        return videoTitle;
    }

    /**
     * @param videoTitle The videoTitle
     */
    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    /**
     * @return The videoURL
     */
    public String getVideoURL() {
        return videoURL;
    }

    /**
     * @param videoURL The videoURL
     */
    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

}
