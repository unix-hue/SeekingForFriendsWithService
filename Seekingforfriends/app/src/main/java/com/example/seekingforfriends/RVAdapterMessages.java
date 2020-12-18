package com.example.seekingforfriends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class RVAdapterMessages extends RecyclerView.Adapter <RVAdapterMessages.ViewHolder> {
    private final list_messages listMessages;
    private final List<Map> values;

    RVAdapterMessages(list_messages parent, List<Map> items)
    {
        listMessages = parent;
        values = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_message_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        String temp = values.get(position).get("time").toString();
        Date date = new Date(Long.parseLong(temp));

        holder.name.setText("От: " + values.get(position).get("name").toString());
        holder.text.setText(values.get(position).get("text").toString());
        holder.time.setText(date.toString());
    }

    @Override
    public int getItemCount()
    {
        return values.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        final TextView name;
        final TextView text;
        final TextView time;

        ViewHolder(View view)
        {
            super(view);
            name = view.findViewById(R.id.name_human);
            text = view.findViewById(R.id.message);
            time = view.findViewById(R.id.time);
        }
    }
}