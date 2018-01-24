package com.happysanztech.mmm.bean.support;

/**
 * Created by Admin on 06-01-2018.
 */

public class StoreTrade {

    private String tradeId;
    private String tradeName;

    public StoreTrade(String tradeId, String tradeName) {
        this.tradeId = tradeId;
        this.tradeName = tradeName;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return tradeName;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof StoreTrade){
            StoreTrade c = (StoreTrade )obj;
            if(c.getTradeName().equals(tradeName) && c.getTradeId()==tradeId ) return true;
        }

        return false;
    }

}
