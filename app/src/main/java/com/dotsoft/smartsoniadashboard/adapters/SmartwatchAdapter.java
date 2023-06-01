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
import com.dotsoft.smartsoniadashboard.objects.Smartwatch;

import java.util.ArrayList;

public class SmartwatchAdapter extends RecyclerView.Adapter<SmartwatchAdapter.ViewHolder> {

    private ArrayList<Smartwatch> data;
    private HandleEventClick clickListener;

    public SmartwatchAdapter(ArrayList<Smartwatch> data, HandleEventClick clickListener){
        this.data = data;
        this.clickListener = clickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapterList(ArrayList<Smartwatch> newList) {
        this.data = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_smartwatch_item, parent, false);
        return new ViewHolder(itemView);
    }

    /// setting up the species card
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            holder.code.setText(this.data.get(position).getID());
            holder.code.setOnClickListener(v -> clickListener.itemClick(this.data.get(position).getID()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_beacon_item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView code;
        public ViewHolder(View view) {
            super(view);
            code = (TextView) view.findViewById(R.id.watch_code);
        }
    }

    public interface HandleEventClick {
        void itemClick(String code);
    }


}
