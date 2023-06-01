package com.dotsoft.smartsoniadashboard.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dotsoft.smartsoniadashboard.R;
import com.dotsoft.smartsoniadashboard.objects.Worker;

import java.util.ArrayList;

public class WorkerDataAdapter extends RecyclerView.Adapter<WorkerDataAdapter.ViewHolder> {

    private ArrayList<Worker> data;
    private boolean today = true;

    public WorkerDataAdapter(ArrayList<Worker> data,boolean today){
        this.data = data;
        this.today = today;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapterList(ArrayList<Worker> newList, boolean today) {
        this.today = today;
        this.data = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_worker_data_item, parent, false);
        return new ViewHolder(itemView);
    }

    // setting up the worker card
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // set worker
        Worker w = this.data.get(position);
        holder.name.setText(w.getWorkerProfileInfo().getUserName().toLowerCase());

        // set watch data
        if(w.getData()!=null){
            if(w.getData().size()!=0){
                if(w.getData().get(0).getHeartRate().equals("N/A")){
                    String locationHeight = "Floor: - , Height: -";
                    holder.location.setText(locationHeight);
                    holder.hearRate.setText("N/A");
                    // set red area
                    holder.area.setImageResource(R.drawable.ic_grey_area);
                    String timeDate = " - ";
                    holder.workerTimestamp.setText(timeDate);
                }else {
                    String locationHeight = "Floor: " + w.getData().get(0).getFloor() + ", Height: " + w.getData().get(0).getHeight() + "m";
                    holder.location.setText(locationHeight);
                    holder.hearRate.setText(w.getData().get(0).getHeartRate());
                    String timeDate = w.getData().get(0).getTime() + " " + w.getData().get(0).getDate();
                    holder.workerTimestamp.setText(timeDate);

                    // set red area
                    if (w.getData().get(0).isAreaRed()) {
                        holder.area.setImageResource(R.drawable.ic_red_area);
                    } else {
                        holder.area.setImageResource(R.drawable.ic_green_area);
                    }
                }
            }
        }
        // set weather data
        if(w.getWeatherData()!=null){
            if(w.getWeatherData().result.size()!=0){
                holder.weatherTimestamp.setText(w.getWeatherData().result.get(0).custom_fields.timestamp);
                holder.barometer.setText(w.getWeatherData().result.get(0).custom_fields.barometer);
                holder.etDay.setText(w.getWeatherData().result.get(0).custom_fields.et_day);
                holder.tempIn.setText(w.getWeatherData().result.get(0).custom_fields.temp_in);
                holder.tempOut.setText(w.getWeatherData().result.get(0).custom_fields.temp_out);
                holder.humIn.setText(w.getWeatherData().result.get(0).custom_fields.hum_in);
                holder.humOut.setText(w.getWeatherData().result.get(0).custom_fields.hum_out);
                holder.windSpeed.setText(w.getWeatherData().result.get(0).custom_fields.wind_speed);
                holder.windSpeedAvg.setText(w.getWeatherData().result.get(0).custom_fields.wind_speed_10_min_avg);
                holder.rainRate.setText(w.getWeatherData().result.get(0).custom_fields.rain_rate);
                holder.uv.setText(w.getWeatherData().result.get(0).custom_fields.uv);
                holder.solar.setText(w.getWeatherData().result.get(0).custom_fields.solar_radiation);
                holder.rainDay.setText(w.getWeatherData().result.get(0).custom_fields.rain_day);
            }
        }

        if(!today){
            holder.noWeatherData.setVisibility(View.VISIBLE);
            holder.weatherDataSection.setVisibility(View.INVISIBLE);
        }else {
            holder.noWeatherData.setVisibility(View.GONE);
            holder.weatherDataSection.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_worker_data_item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name, location, hearRate, weatherTimestamp, tempIn, tempOut, humIn, humOut,
                windSpeed, windSpeedAvg, rainRate, uv, solar, rainDay, barometer, etDay, workerTimestamp,
                noWeatherData;
        private ImageView area;
        private RelativeLayout weatherDataSection;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.worker_id);
            location = (TextView) view.findViewById(R.id.worker_location);
            hearRate = (TextView) view.findViewById(R.id.heart_rate_value);
            weatherTimestamp = (TextView) view.findViewById(R.id.ed_timestamp);
            barometer = (TextView) view.findViewById(R.id.barometer_value);
            etDay = (TextView) view.findViewById(R.id.et_day_value);
            tempIn = (TextView) view.findViewById(R.id.temp_in_value);
            tempOut = (TextView) view.findViewById(R.id.temp_out_value);
            humIn = (TextView) view.findViewById(R.id.hum_in_value);
            humOut = (TextView) view.findViewById(R.id.hum_out_value);
            windSpeed = (TextView) view.findViewById(R.id.wind_speed_value);
            windSpeedAvg = (TextView) view.findViewById(R.id.wind_speed_avg_value);
            rainRate = (TextView) view.findViewById(R.id.rain_value);
            uv = (TextView) view.findViewById(R.id.uv_value);
            solar = (TextView) view.findViewById(R.id.solar_value);
            rainDay = (TextView) view.findViewById(R.id.rain_day_value);
            workerTimestamp = (TextView) view.findViewById(R.id.data_timestamp_value);
            area = (ImageView) view.findViewById(R.id.area_icon);
            noWeatherData = (TextView) view.findViewById(R.id.no_env_data);
            weatherDataSection = (RelativeLayout) view.findViewById(R.id.environmental_data_values_section);
        }
    }


}
