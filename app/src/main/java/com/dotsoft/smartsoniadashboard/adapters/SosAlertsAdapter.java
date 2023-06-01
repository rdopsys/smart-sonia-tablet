package com.dotsoft.smartsoniadashboard.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dotsoft.smartsoniadashboard.R;
import com.dotsoft.smartsoniadashboard.objects.Beacon;
import com.dotsoft.smartsoniadashboard.objects.RecommendationObject;
import com.dotsoft.smartsoniadashboard.objects.SosAlert;

import java.util.ArrayList;

public class SosAlertsAdapter extends RecyclerView.Adapter<SosAlertsAdapter.ViewHolder> {

    private ArrayList<SosAlert> data;
    private HandleEventClick clickListener;

    public SosAlertsAdapter(ArrayList<SosAlert> data, HandleEventClick clickListener){
        this.data = data;
        this.clickListener = clickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapterList(ArrayList<SosAlert> newList) {
        this.data = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void addData(SosAlert item){
        int insertIndex = this.data.size();
        data.add(insertIndex, item);
        notifyItemRangeInserted(insertIndex, 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_sos_alert_item, parent, false);
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
        holder.delete.setOnClickListener(v -> clickListener.itemClickDeleteSosAlert(this.data.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_sos_alert_item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView workerName, date, time, location;
        private ImageView delete;
        public ViewHolder(View view) {
            super(view);
            workerName = (TextView) view.findViewById(R.id.sos_alert_worker);
            date = (TextView) view.findViewById(R.id.sos_alert_date);
            time = (TextView) view.findViewById(R.id.sos_alert_time);
            location = (TextView) view.findViewById(R.id.sos_alert_location);
            delete = (ImageView) view.findViewById(R.id.sos_alert_delete);

        }
    }

    public interface HandleEventClick {
        void itemClickDeleteSosAlert(String id);
        void itemClickLocationSosAlert(String latLog);
    }


}
