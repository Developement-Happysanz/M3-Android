package com.happysanztech.mmm.serviceinterfaces;

import org.json.JSONObject;

/**
 * Created by Admin on 25-09-2017.
 */

public interface IServiceListener {

    void onResponse(JSONObject response);

    void onError(String error);
}
