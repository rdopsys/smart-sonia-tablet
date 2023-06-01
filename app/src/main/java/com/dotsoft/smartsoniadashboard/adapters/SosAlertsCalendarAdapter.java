package com.dotsoft.smartsoniadashboard.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dotsoft.smartsoniadashboard.R;
import com.dotsoft.smartsoniadashboard.objects.SosAlert;

import java.util.ArrayList;

public class SosAlertsCalendarAdapter extends RecyclerView.Adapter<SosAlertsCalendarAdapter.ViewHolder> {

    private ArrayList<SosAlert> data;
    private HandleEventClick clickListener;


    public SosAlertsCalendarAdapter(ArrayList<SosAlert> data, HandleEventClick clickListener){
        this.clickListener = clickListener;
        this.data = data;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapterList(ArrayList<SosAlert> newList) {
        this.data = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_sos_alert_item_calendar, parent, false);
        return new ViewHolder(itemView);
    }

    // setting up the sos alert card
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.workerName.setText(this.data.get(position).getAuthor().toLowerCase());
        holder.location.setText(this.data.get(position).getLocation());
        holder.date.setText(this.data.get(position).getDate());
        holder.time.setText(this.data.get(position).getTime());
        holder.location.setOnClickListener(v -> clickListener.itemClickLocationSosAlert(this.data.get(position).getLocation()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_sos_alert_item_calendar;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView workerName, date, time, location;
        public ViewHolder(View view) {
            super(view);
            workerName = (TextView) view.findViewById(R.id.sos_alert_worker);
            date = (TextView) view.findViewById(R.id.sos_alert_date);
            time = (TextView) view.findViewById(R.id.sos_alert_time);
            location = (TextView) view.findViewById(R.id.sos_alert_location);
        }
    }

    public interface HandleEventClick {
        void itemClickLocationSosAlert(String latLog);
    }



}
