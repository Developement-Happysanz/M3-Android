package com.happysanztech.mmm.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Admin on 11-01-2018.
 */

public class CenterPhotosData implements Serializable {

    @SerializedName("center_photos")
    @Expose
    private String centerPhotos;

    /**
     * @return The centerPhotos
     */
    public String getCenterPhotos() {
        return centerPhotos;
    }

    /**
     * @param centerPhotos The centerPhotos
     */
    public void setCenterPhotos(String centerPhotos) {
        this.centerPhotos = centerPhotos;
    }
}
