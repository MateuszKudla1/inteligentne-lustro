package com.example.mateusz.inteligentnelustro.weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateusz on 2017-06-07.
 */

public class Forecast implements JsonPopulator {

    private JSONObject data;
    private JSONArray dataArray;
    private List<ForecastDay> list = new ArrayList<ForecastDay>();



    public Forecast(JSONObject data){
        JsonPopulate(data);
        forecastMethod();
    }

    @Override
    public void JsonPopulate(JSONObject data) {
        this.data = data.optJSONObject("item");
        this.dataArray = this.data.optJSONArray("forecast");

    }

    public void forecastMethod() {

        for(int i = 0; i < dataArray.length(); i++){
            try {
                ForecastDay fday = new ForecastDay();
                JSONObject day = (JSONObject) dataArray.get(i);
                fday.setCode(day.optInt("code"));
                fday.setDayW(day.optString("day"));
                fday.setHigh(day.optInt("high"));
                fday.setLow(day.optInt("low"));
                fday.setText(day.optString("text"));
                list.add(fday);


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

    public List<ForecastDay> getList() {
        return list;
    }
}
