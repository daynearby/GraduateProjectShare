package com.young.myInterface;

import com.android.volley.VolleyError;

/**
 * Created by Nearby Yang on 2015-11-16.
 */
public interface JSONResquestResponse {
    void onSuceess();
    void onFaile(VolleyError error);
}
