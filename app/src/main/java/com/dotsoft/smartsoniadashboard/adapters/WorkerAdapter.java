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
import com.dotsoft.smartsoniadashboard.objects.Worker;

import java.util.ArrayList;
import java.util.Locale;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.ViewHolder> {

    private ArrayList<Worker> data;
    private HandleEventClick clickListener;
    private Context context;

    public WorkerAdapter(ArrayList<Worker> data, HandleEventClick clickListener,Context context){
        this.data = data;
        this.clickListener = clickListener;
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapterList(ArrayList<Worker> newList) {
        this.data = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        if(viewType == R.layout.adapter_worker_item){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_worker_item, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_new_worker, parent, false);
        }
        return new ViewHolder(itemView);
    }

    // setting up the worker card
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(position == data.size()) {
            holder.addButton.setOnClickListener(v -> clickListener.itemClickAdd());
        }
        else {
            holder.id.setText(this.data.get(position).getWorkerProfileInfo().getUserName().toLowerCase());
            holder.delete.setOnClickListener(n -> clickListener.itemClickDelete(this.data.get(position).getWorkerProfileInfo().getUserName()));
            holder.edit.setOnClickListener(n -> clickListener.itemClickEdit(this.data.get(position).getWorkerProfileInfo().getUserName(),this.data.get(position).getWorkerProfileInfo().getUserId(),
                    this.data.get(position).getCurrentWorkerInfo().getSmartwatchID(),this.data.get(position).getCurrentWorkerInfo().isActive(),this.data.get(position).getInfoTextFormat()));

            int selectedDayIndex = -1;

            if(!this.data.get(position).getSelectedDay().equals("")){
                for(int i=0;i<this.data.get(position).getWorkerInfo().size();i++){
                    String[] spitTimestamp = this.data.get(position).getWorkerInfo().get(i).getTimestamp().split(" ",2);
                    if(spitTimestamp[0].compareTo(this.data.get(position).getSelectedDay())<=0){

                        selectedDayIndex = i;
                    }else {

                    }
                    if(selectedDayIndex!=-1){
                        break;
                    }
                }
                if(selectedDayIndex==-1){
                    selectedDayIndex = this.data.get(position).getWorkerInfo().size()-1;
                }
            }else {
                selectedDayIndex = 0;
            }
            if(this.data.get(position).getWorkerInfo().get(selectedDayIndex).getSmartwatchID().equals("")
                    || this.data.get(position).getWorkerInfo().get(selectedDayIndex).getSmartwatchID().equals("NONE")
            ){
                holder.watchIcon.setImageResource(R.drawable.ic_watch_disable);
                holder.watchTv.setText(context.getString(R.string.match_smartwatch_none));
            }else {
                holder.watchIcon.setImageResource(R.drawable.ic_watch_enable);
                String wID = this.data.get(position).getWorkerInfo().get(selectedDayIndex).getSmartwatchID();
                holder.watchTv.setText(wID);
            }
            if(this.data.get(position).getWorkerInfo().get(selectedDayIndex).isActive()){
                holder.checkInIcon.setImageResource(R.drawable.ic_check_in_on);
                holder.checkInTv.setText(context.getString(R.string.check_in_confirmed));
            }else{
                holder.checkInIcon.setImageResource(R.drawable.ic_check_in_off);
                holder.checkInTv.setText(context.getString(R.string.check_in_not_confirmed));

            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == data.size()) ? R.layout.adapter_new_worker : R.layout.adapter_worker_item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView id, watchTv, checkInTv;
        private RelativeLayout addButton;
        private ImageView delete, edit, workerIcon, watchIcon, checkInIcon;
        public ViewHolder(View view) {
            super(view);
            id = (TextView) view.findViewById(R.id.worker_id);
            watchTv = (TextView) view.findViewById(R.id.watch_state);
            checkInTv = (TextView) view.findViewById(R.id.check_in_state);
            delete = (ImageView) view.findViewById(R.id.worker_delete);
            edit = (ImageView) view.findViewById(R.id.worker_edit);
            addButton = (RelativeLayout) view.findViewById(R.id.add_beacon);
            workerIcon = (ImageView) view.findViewById(R.id.worker_icon);
            watchIcon = (ImageView) view.findViewById(R.id.watch_icon);
            checkInIcon = (ImageView) view.findViewById(R.id.check_in_icon);
        }
    }

    public interface HandleEventClick {
        void itemClickAdd();
        void itemClickEdit(String username, String id, String s, boolean c, String i);
        void itemClickDelete(String id);
    }


}
