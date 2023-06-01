package com.dotsoft.smartsoniadashboard.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dotsoft.smartsoniadashboard.R;
import com.dotsoft.smartsoniadashboard.objects.CalendarDay;
import com.dotsoft.smartsoniadashboard.objects.SosAlert;

import java.util.ArrayList;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.ViewHolder> {

    private ArrayList<CalendarDay> data;
    private Context context;
    private HandleEventClick clickListener;


    public DaysAdapter(ArrayList<CalendarDay> data, Context context, HandleEventClick clickListener){
        this.data = data;
        this.context = context;
        this.clickListener = clickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapterList(ArrayList<CalendarDay> newList) {
        this.data = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_day_item, parent, false);
        return new ViewHolder(itemView);
    }

    // setting up the sos alert card
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(this.data.get(position).isActiveDay()){
            holder.dayNum.setText(this.data.get(position).getDayNumber());

            if(this.data.get(position).getSos()>0){
                String sosText = "";
                if(this.data.get(position).getSos()==1) {
                    sosText = this.data.get(position).getSos() + " "+context.getString(R.string.sos_notification_calendar);
                }else{
                    sosText = this.data.get(position).getSos() + " "+context.getString(R.string.sos_notifications_calendar);
                }
                holder.sosTv.setText(sosText);
                holder.sosSection.setVisibility(View.VISIBLE);
                holder.sosSection.setOnClickListener(v -> clickListener.itemClickSosSection(this.data.get(position)));

            }else {
                holder.sosSection.setVisibility(View.GONE);
            }

            if(this.data.get(position).getAlerts()>0){
                String alertsText = "";
                if(this.data.get(position).getAlerts()==1){
                    alertsText = this.data.get(position).getAlerts() + " "+context.getString(R.string.alert_calendar);
                }else {
                    alertsText = this.data.get(position).getAlerts() + " "+context.getString(R.string.alerts_calendar);
                }
                holder.alertsTv.setText(alertsText);
                holder.alertsSection.setVisibility(View.VISIBLE);
                holder.alertsSection.setOnClickListener(v -> clickListener.itemClickAlertSection(this.data.get(position)));
            }else {
                holder.alertsSection.setVisibility(View.GONE);
            }

            if(this.data.get(position).getAccidents()>0){
                String accidentsText = "";
                if(this.data.get(position).getAccidents()==1){
                    accidentsText = this.data.get(position).getAccidents() + " "+context.getString(R.string.accident_calendar);
                }else {
                    accidentsText = this.data.get(position).getAccidents() + " "+context.getString(R.string.accidents_calendar);
                }
                holder.accidentsTv.setText(accidentsText);
                holder.accidentsSection.setVisibility(View.VISIBLE);
                holder.accidentsSection.setOnClickListener(v -> clickListener.itemClickAccidentSection(this.data.get(position)));
            }else {
                holder.accidentsSection.setVisibility(View.GONE);
            }
        }else {
            holder.dayNum.setText(" ");
            holder.alertsSection.setVisibility(View.GONE);
            holder.sosSection.setVisibility(View.GONE);
            holder.accidentsSection.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_day_item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView dayNum, sosTv, alertsTv, accidentsTv;
        private RelativeLayout sosSection, alertsSection, accidentsSection;
        public ViewHolder(View view) {
            super(view);
            dayNum = (TextView) view.findViewById(R.id.day_num);

            alertsSection = (RelativeLayout) view.findViewById(R.id.alerts_section);
            alertsTv = (TextView) view.findViewById(R.id.alert_title);

            sosSection = (RelativeLayout) view.findViewById(R.id.sos_section);
            sosTv = (TextView) view.findViewById(R.id.sos_text);

            accidentsSection = (RelativeLayout) view.findViewById(R.id.accident_section);
            accidentsTv = (TextView) view.findViewById(R.id.accident_text);
        }
    }

    public interface HandleEventClick {
        void itemClickSosSection(CalendarDay day);
        void itemClickAlertSection(CalendarDay day);
        void itemClickAccidentSection(CalendarDay day);
    }


}
