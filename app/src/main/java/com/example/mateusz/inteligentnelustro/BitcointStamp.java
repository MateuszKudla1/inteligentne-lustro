package com.example.mateusz.inteligentnelustro;

import org.json.JSONObject;

/**
 * Created by Mateusz on 2017-06-13.
 */

public class BitcointStamp {

    private float last;









    public BitcointStamp(JSONObject data){
        last = (float) data.optDouble("bid");


    }





    public float getLast() {
        return last;
    }

}
