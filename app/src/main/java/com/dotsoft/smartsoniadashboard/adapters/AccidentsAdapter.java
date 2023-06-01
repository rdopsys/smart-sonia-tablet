package com.dotsoft.smartsoniadashboard.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.dotsoft.smartsoniadashboard.R;
import com.dotsoft.smartsoniadashboard.objects.AccidentObject;

import java.util.ArrayList;

public class AccidentsAdapter extends RecyclerView.Adapter<AccidentsAdapter.ViewHolder> {

    private ArrayList<AccidentObject> data;
    private HandleEventClick clickListener;

    public AccidentsAdapter(ArrayList<AccidentObject> data, HandleEventClick clickListener){
        this.data = data;
        this.clickListener = clickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapterList(ArrayList<AccidentObject> newList) {
        this.data = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_accident_item, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(this.data.get(position).getTitle());
        holder.worker.setText(this.data.get(position).getWorkerName().toLowerCase());
        holder.time.setText(this.data.get(position).getTime());
        holder.notes.setText(this.data.get(position).getNotes());

        if(this.data.get(position).getDangerousZone().equalsIgnoreCase("0")){
            holder.zone.setText("No");
        }else{
            holder.zone.setText("Yes");
        }

        holder.details.setOnClickListener(v -> clickListener.itemClickDetails(this.data.get(position)));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_accident_item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, worker, time, notes, zone;
        private ImageView details;
        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.accident_title);
            worker = (TextView) view.findViewById(R.id.worker_name);
            time = (TextView) view.findViewById(R.id.time_text);
            notes = (TextView) view.findViewById(R.id.note_text);
            zone = (TextView) view.findViewById(R.id.zone_state);
            details = (ImageView) view.findViewById(R.id.accident_details);
        }
    }

    public interface HandleEventClick {
        void itemClickDetails(AccidentObject accidentObject);
    }


}
