package com.example.mateusz.inteligentnelustro.weather;

import org.json.JSONObject;

/**
 * Created by Mateusz on 2017-06-07.
 */

public class Astronomy implements JsonPopulator {

    private JSONObject data;
    private String sunrise;
    private String sunset;



    public Astronomy(JSONObject data){
        JsonPopulate(data);
    }

    @Override
    public void JsonPopulate(JSONObject data) {
        this.data = data.optJSONObject("astronomy");
        this.sunrise = this.data.optString("sunrise");
        this.sunset = this.data.optString("sunset");

    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset(){
        return sunset;
    }
}
