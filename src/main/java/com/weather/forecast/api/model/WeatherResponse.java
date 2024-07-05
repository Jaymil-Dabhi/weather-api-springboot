package com.weather.forecast.api.model;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherResponse {

    private int cod;
    private String message;
    private int cnt;
    private City city;
    private List<Weather> list = new ArrayList<>();

    public static class Weather {

        private Main main;
        private Wind wind;
        private String dt_txt;

        // Getters and Setters for Weather fields
        public Main getMain() {
            return main;
        }

        public void setMain(Main main) {
            this.main = main;
        }

        public Wind getWind() {
            return wind;
        }

        public void setWind(Wind wind) {
            this.wind = wind;
        }

        public String getDt_txt() {
            return dt_txt;
        }

        public void setDt_txt(String dt_txt) {
            this.dt_txt = dt_txt;
        }

        public static class Main {
            private double temp;
            private int humidity;
            private double feels_like;

            // Getters and Setters for Main fields
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

            public double getFeels_like() {
                return feels_like;
            }

            public void setFeels_like(double feels_like) {
                this.feels_like = feels_like;
            }
        }

        public static class Wind {
            private double speed;

            // Getters and Setters for Wind fields
            public double getSpeed() {
                return speed;
            }

            public void setSpeed(double speed) {
                this.speed = speed;
            }
        }

    }
}


