package com.dotsoft.smartsoniadashboard.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dotsoft.smartsoniadashboard.R;
import com.dotsoft.smartsoniadashboard.objects.AlertObject;

import java.util.ArrayList;

public class AlertsCalendarAdapter extends RecyclerView.Adapter<AlertsCalendarAdapter.ViewHolder> {

    private ArrayList<AlertObject> data;

    public AlertsCalendarAdapter(ArrayList<AlertObject> data){
        this.data = data;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapterList(ArrayList<AlertObject> newList) {
        this.data = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_alert_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(this.data.get(position).getTitle().toUpperCase());
        holder.worker.setText(this.data.get(position).getWorkerName().toLowerCase());
        holder.time.setText(this.data.get(position).getTime());
        holder.risk.setText(this.data.get(position).getRiskGrading());

        if(this.data.get(position).getRiskGrading().equalsIgnoreCase("very low")){
            holder.area.setImageResource(R.drawable.ic_risk_very_low);
        }else if(this.data.get(position).getRiskGrading().equalsIgnoreCase("low")){
            holder.area.setImageResource(R.drawable.ic_risk_low);
        }else if(this.data.get(position).getRiskGrading().equalsIgnoreCase("medium")){
            holder.area.setImageResource(R.drawable.ic_risk_medium);
        }else if(this.data.get(position).getRiskGrading().equalsIgnoreCase("high")){
            holder.area.setImageResource(R.drawable.ic_risk_high);
        }else if(this.data.get(position).getRiskGrading().equalsIgnoreCase("very high")){
            holder.area.setImageResource(R.drawable.ic_risk_very_high);
        }else{
            holder.area.setImageResource(R.drawable.ic_risk_very_low);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_alert_item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, worker, time, risk;
        private ImageView area;
        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.alert_title);
            worker = (TextView) view.findViewById(R.id.worker_name);
            time = (TextView) view.findViewById(R.id.time_text);
            risk = (TextView) view.findViewById(R.id.risk_grading_text);
            area = (ImageView) view.findViewById(R.id.area_icon);
        }
    }

}
