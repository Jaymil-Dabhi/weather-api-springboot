package com.weather.forecast.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Weather {

    private long dt;
    private Main main;
    private List<DetailInfo> weather;
    private Clouds clouds;
    private Wind wind;
    private int visibility;
    private long pop;
    private Sys sys;
    private String dt_txt;

    public static class Main {
        private double feelsLike;
        private double temp;
        private int humidity;

        // Getters and Setters for Main fields
        public double getFeelsLike() {
            return feelsLike;
        }

        public void setFeelsLike(double feelsLike) {
            this.feelsLike = feelsLike;
        }

        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
        }

        public int getHumidity() {
            return humidity;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }
    }
}
