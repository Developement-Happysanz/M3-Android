package com.happysanztech.mmm.bean.support;

/**
 * Created by Admin on 06-01-2018.
 */

public class StoreTimings {

    private String timingsId;
    private String timingsName;

    public StoreTimings(String timingsId, String timingsName) {
        this.timingsId = timingsId;
        this.timingsName = timingsName;
    }

    public String getTimingsId() {
        return timingsId;
    }

    public void setTimingsId(String timingsId) {
        this.timingsId = timingsId;
    }

    public String getTimingsName() {
        return timingsName;
    }

    public void setTimingsName(String timingsName) {
        this.timingsName = timingsName;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return timingsName;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof StoreTrade){
            StoreTimings c = (StoreTimings )obj;
            if(c.getTimingsName().equals(timingsName) && c.getTimingsId()==timingsId ) return true;
        }

        return false;
    }
}
