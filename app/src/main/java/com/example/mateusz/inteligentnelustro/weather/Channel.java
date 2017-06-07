package com.example.mateusz.inteligentnelustro.weather;

import org.json.JSONObject;

/**
 * Created by Mateusz on 2017-06-07.
 */

public class Channel implements JsonPopulator {

   public Location location;
   public Astronomy astronomy;
   public Condition condition;
    public Forecast forecast;
   public JSONObject data;

    public Channel(JSONObject data){
        JsonPopulate(data);
        location = new Location(this.data);
        astronomy = new Astronomy(this.data);
        condition =new Condition(this.data);
        forecast = new Forecast(data);

    }


    @Override
    public void JsonPopulate(JSONObject data) {
        this.data = data;

    }
}
