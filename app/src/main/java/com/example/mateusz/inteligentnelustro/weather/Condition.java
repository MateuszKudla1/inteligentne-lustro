package com.example.mateusz.inteligentnelustro.weather;

import org.json.JSONObject;

/**
 * Created by Mateusz on 2017-06-07.
 */

public class Condition implements JsonPopulator {

    private JSONObject data;
    private String temp;
    private String text;


    public Condition(JSONObject data){
        JsonPopulate(data);
    }

    @Override
    public void JsonPopulate(JSONObject data) {
        this.data = data
                .optJSONObject("item")
                .optJSONObject("condition");

        this.temp = Integer.toString(this.data.optInt("temp"));
        this.text = this.data.optString("text");

    }

    public String getTemp() {
        return temp;
    }

    public String getText() {
        return text;
    }
}
