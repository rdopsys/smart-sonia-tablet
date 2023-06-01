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
import com.dotsoft.smartsoniadashboard.objects.MessageObject;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private ArrayList<MessageObject> data;

    public MessagesAdapter(ArrayList<MessageObject> data){
        this.data = data;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapterList(ArrayList<MessageObject> newList) {
        this.data = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_message_item, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(this.data.get(position).getTitle());
        holder.worker.setText(this.data.get(position).getWorkerName().toLowerCase());
        holder.time.setText(this.data.get(position).getTime());
        holder.notes.setText(this.data.get(position).getNotes());

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
        return R.layout.adapter_message_item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, worker, time, notes, read;
        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.message_title);
            worker = (TextView) view.findViewById(R.id.worker_name);
            time = (TextView) view.findViewById(R.id.time_text);
            notes = (TextView) view.findViewById(R.id.note_text);
            read = (TextView) view.findViewById(R.id.read_state);
        }
    }


}
