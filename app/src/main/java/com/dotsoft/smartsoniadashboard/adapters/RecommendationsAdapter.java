package com.dotsoft.smartsoniadashboard.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dotsoft.smartsoniadashboard.R;
import com.dotsoft.smartsoniadashboard.objects.RecommendationObject;

import java.util.ArrayList;

public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.ViewHolder> {

    private ArrayList<RecommendationObject> data;

    public RecommendationsAdapter(ArrayList<RecommendationObject> data){
        this.data = data;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapterList(ArrayList<RecommendationObject> newList) {
        this.data = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recommendation_item, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(this.data.get(position).getTitle());
        holder.worker.setText(this.data.get(position).getWorkerName().toLowerCase());

        holder.time.setText(this.data.get(position).getTime());

        if(this.data.get(position).getRead().equalsIgnoreCase("0")){
            holder.read.setText("Read");
        }else{
            holder.read.setText("Unread");
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_recommendation_item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, worker, time, read;
        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.recommendation_title);
            worker = (TextView) view.findViewById(R.id.worker_name);
            time = (TextView) view.findViewById(R.id.time_text);
            read = (TextView) view.findViewById(R.id.read_state);
        }
    }


}
