package com.example.mateusz.inteligentnelustro.weather;

import org.json.JSONObject;

/**
 * Created by Mateusz on 2017-06-07.
 */

public class Location implements JsonPopulator {

    private JSONObject data;
    private String city;




    public Location(JSONObject data){
        JsonPopulate(data);

    }

    @Override
    public void JsonPopulate(JSONObject data) {
        this.data = data.optJSONObject("location");
        this.city = this.data.optString("city");


    }

    public String getCity() {
        return city;
    }
}
