package com.example.mateusz.inteligentnelustro.weather;

/**
 * Created by Mateusz on 2017-06-07.
 */

public class ForecastDay {

    private int high;
    private int low;
    private String text;
    private String dayW;
    private int code;


    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDayW() {
        return dayW;
    }

    public void setDayW(String dayW) {
        this.dayW = dayW;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }




}
