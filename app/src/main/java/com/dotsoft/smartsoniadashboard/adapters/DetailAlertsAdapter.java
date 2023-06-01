package com.dotsoft.smartsoniadashboard.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dotsoft.smartsoniadashboard.R;
import com.dotsoft.smartsoniadashboard.objects.AccidentObject;
import com.dotsoft.smartsoniadashboard.objects.AlertObject;
import com.dotsoft.smartsoniadashboard.objects.RecommendationObject;

import java.util.ArrayList;

public class DetailAlertsAdapter extends RecyclerView.Adapter<DetailAlertsAdapter.ViewHolder> {

    private ArrayList<AlertObject> data;

    public DetailAlertsAdapter(ArrayList<AlertObject> data){
        this.data = data;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapterList(ArrayList<AlertObject> newList) {
        this.data = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void addData(AlertObject item){
        int insertIndex = this.data.size();
        data.add(insertIndex, item);
        notifyItemRangeInserted(insertIndex, 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_detail_item, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.time.setText(this.data.get(position).getTime());
        holder.val1.setText(this.data.get(position).getTitle());
        String s = this.data.get(position).getRiskGrading();
        holder.val2.setText(s);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_detail_item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView time, val1, val2;
        public ViewHolder(View view) {
            super(view);
            time = (TextView) view.findViewById(R.id.time);
            val1 = (TextView) view.findViewById(R.id.val_1);
            val2 = (TextView) view.findViewById(R.id.val_2);
        }
    }



}
