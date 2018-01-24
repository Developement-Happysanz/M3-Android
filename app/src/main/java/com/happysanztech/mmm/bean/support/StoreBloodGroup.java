package com.happysanztech.mmm.bean.support;

/**
 * Created by Admin on 06-01-2018.
 */

public class StoreBloodGroup {

    private String bloodGroupId;
    private String bloodGroupName;

    public StoreBloodGroup(String bloodGroupId, String bloodGroupName) {
        this.bloodGroupId = bloodGroupId;
        this.bloodGroupName = bloodGroupName;
    }

    public String getBloodGroupId() {
        return bloodGroupId;
    }

    public void setBloodGroupId(String bloodGroupId) {
        this.bloodGroupId = bloodGroupId;
    }

    public String getBloodGroupName() {
        return bloodGroupName;
    }

    public void setBloodGroupName(String bloodGroupName) {
        this.bloodGroupName = bloodGroupName;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return bloodGroupName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StoreTrade) {
            StoreBloodGroup c = (StoreBloodGroup) obj;
            if (c.getBloodGroupName().equals(bloodGroupName) && c.getBloodGroupId() == bloodGroupId)
                return true;
        }

        return false;
    }

}
