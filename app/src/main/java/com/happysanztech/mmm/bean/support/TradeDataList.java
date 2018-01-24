package com.happysanztech.mmm.bean.support;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Admin on 08-01-2018.
 */

public class TradeDataList {

    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("Trades")
    @Expose
    private ArrayList<TradeData> data = new ArrayList<TradeData>();

    /**
     * @return The count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count The count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return The TradeData
     */
    public ArrayList<TradeData> getTradeData() {
        return data;
    }

    /**
     * @param data The TradeData
     */
    public void setTradeData(ArrayList<TradeData> data) {
        this.data = data;
    }
}


