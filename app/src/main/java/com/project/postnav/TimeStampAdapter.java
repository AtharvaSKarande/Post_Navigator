package com.project.postnav;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TimeStampAdapter extends RecyclerView.Adapter<TimeStampAdapter.TimeStampViewHolder>{
    private final Context context;
    private final ArrayList<TimeStamp> TSList;
    public TimeStampAdapter(Context c, ArrayList<TimeStamp> q)
    {
        context = c;
        TSList = q;
    }
    @NonNull
    @Override
    public TimeStampAdapter.TimeStampViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TimeStampViewHolder(LayoutInflater.from(context).inflate(R.layout.stamp, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TimeStampAdapter.TimeStampViewHolder holder,final int position) {
        holder.TS_date.setText(TSList.get(position).getDate());
        holder.TS_Location.setText(TSList.get(position).getLocation());
        holder.TS_time.setText(TSList.get(position).getTime());
        holder.TS_Remark.setText(TSList.get(position).getRemark());
    }

    @Override
    public int getItemCount() {
        return TSList.size();
    }

    static class TimeStampViewHolder extends RecyclerView.ViewHolder{
        TextView TS_date,TS_time,TS_Location,TS_Remark;
        TimeStampViewHolder(View itemView) {
            super(itemView);
            //itemView.setOnClickListener(this);
            TS_date = itemView.findViewById(R.id.stamp_date);
            TS_time = itemView.findViewById(R.id.stamp_time);
            TS_Location = itemView.findViewById(R.id.stamp_location);
            TS_Remark = itemView.findViewById(R.id.stamp_remark);
        }
    }
}

