package com.dotsoft.smartsoniadashboard.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.dotsoft.smartsoniadashboard.R;
import com.dotsoft.smartsoniadashboard.objects.Beacon;
import java.util.ArrayList;

public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.ViewHolder> {

    private ArrayList<Beacon> data;
    private HandleEventClick clickListener;

    public BeaconAdapter(ArrayList<Beacon> data,HandleEventClick clickListener){
        this.data = data;
        this.clickListener = clickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAdapterList(ArrayList<Beacon> newList) {
        this.data = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        if(viewType == R.layout.adapter_beacon_item){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_beacon_item, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_new_beacon, parent, false);
        }
        return new ViewHolder(itemView);
    }

    /// setting up the species card
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(position == data.size()) {
            holder.addButton.setOnClickListener(v -> clickListener.itemClickAdd());
        }
        else {
            int selectedDayIndex = -1;

            if(!this.data.get(position).getSelectedDay().equals("")){
                for(int i=0;i<this.data.get(position).getBeaconInfo().size();i++){
                    String[] spitTimestamp = this.data.get(position).getBeaconInfo().get(i).getTimestamp().split(" ",2);
                    if(spitTimestamp[0].compareTo(this.data.get(position).getSelectedDay())<=0){
                        selectedDayIndex = i;
                    }
                    if(selectedDayIndex!=-1){
                        break;
                    }
                }
                if(selectedDayIndex==-1){
                    selectedDayIndex = this.data.get(position).getBeaconInfo().size()-1;
                }
            }else {
                selectedDayIndex = 0;
            }
            if(this.data.get(position).getBeaconInfo().get(selectedDayIndex).isActive()){
                holder.active.setImageResource(R.drawable.ic_beacon_on);
                holder.radar.setImageResource(R.drawable.ic_beacon_radar);
            }else {
                holder.active.setImageResource(R.drawable.ic_beacon_off);
                holder.radar.setImageResource(R.drawable.ic_radar_inactive);
            }
            holder.id.setText(this.data.get(position).getMajorID());
            holder.location.setText(this.data.get(position).getBeaconInfo().get(selectedDayIndex).getLocation());
            holder.notes.setText(this.data.get(position).getBeaconInfo().get(selectedDayIndex).getNote());
            holder.delete.setOnClickListener(v -> clickListener.itemClickDelete(this.data.get(position).getPostID(),this.data.get(position).getMajorID()));
            holder.edit.setOnClickListener(v -> clickListener.itemClickEdit(this.data.get(position).getPostID(),this.data.get(position).getMajorID(),
                    this.data.get(position).getCurrentBeaconInfo().getLocation(),this.data.get(position).getCurrentBeaconInfo().getNote(),
                    this.data.get(position).getCurrentBeaconInfo().isActive(),this.data.get(position).getInfoTextFormat()));

        }
    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == data.size()) ? R.layout.adapter_new_beacon : R.layout.adapter_beacon_item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView id, location, notes;
        private RelativeLayout addButton;
        private ImageView delete, edit,active,radar;
        public ViewHolder(View view) {
            super(view);
            id = (TextView) view.findViewById(R.id.beacon_id);
            location = (TextView) view.findViewById(R.id.beacon_location);
            notes = (TextView) view.findViewById(R.id.beacon_notes);
            delete = (ImageView) view.findViewById(R.id.beacon_delete);
            edit = (ImageView) view.findViewById(R.id.beacon_edit);
            addButton = (RelativeLayout) view.findViewById(R.id.add_beacon);
            active = (ImageView) view.findViewById(R.id.beacon_active);
            radar = (ImageView) view.findViewById(R.id.radar_icon);
        }
    }

    public interface HandleEventClick {
        void itemClickAdd();
        void itemClickEdit(String id,String m, String l, String n, boolean a,String i);
        void itemClickDelete(String id, String m);
    }


}
