package com.dotsoft.smartsoniadashboard.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dotsoft.smartsoniadashboard.R;
import com.dotsoft.smartsoniadashboard.objects.Worker;

import java.util.ArrayList;

public class SelectWorkerAdapter extends RecyclerView.Adapter<SelectWorkerAdapter.ViewHolder> {

    private ArrayList<Worker> data;
    private HandleEventClick clickListener;


    public SelectWorkerAdapter(ArrayList<Worker> data, HandleEventClick clickListener){
        this.data = data;
        this.clickListener = clickListener;

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapterList(ArrayList<Worker> newList) {
        this.data = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_send_messages_worker_item, parent, false);
        return new ViewHolder(itemView);
    }

    // setting up the worker card
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // set worker
        holder.name.setText(this.data.get(position).getWorkerProfileInfo().getUserName().toLowerCase());
        holder.sendButton.setOnClickListener(v -> clickListener.itemClickAddAlert(this.data.get(position).getWorkerProfileInfo().getUserName()));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_send_messages_worker_item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private RelativeLayout sendButton;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.worker_id);
            sendButton = (RelativeLayout) view.findViewById(R.id.send_alert_button);
        }
    }

    public interface HandleEventClick {
        void itemClickAddAlert(String id);
    }

}
