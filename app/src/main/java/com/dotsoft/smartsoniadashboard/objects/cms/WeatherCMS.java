package com.dotsoft.smartsoniadashboard.objects.cms;

import java.util.ArrayList;

public class WeatherCMS {

    public class CustomFields{
        public String height;
        public String identifier;
        public String latitude;
        public String longitude;
        public String timestamp;
        public String bar_trend;
        public String barometer;
        public String temp_in;
        public String temp_out;
        public String hum_in;
        public String hum_out;
        public String wind_speed;
        public String wind_speed_10_min_avg;
        public String wind_direction;
        public String rain_rate;
        public String uv;
        public String solar_radiation;
        public String storm_rain;
        public String rain_day;
        public String rain_month;
        public String rain_year;
        public String et_day;
        public String et_month;
        public String et_year;
        public String version;
        public String postid;
    }

    public class Result{
        public CustomFields custom_fields;
    }

    public class Root{
        public String respond;
        public ArrayList<Result> result;
    }


}
