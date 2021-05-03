package com.yunitski.bookkeeper2;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ElementAdapter extends RecyclerView.Adapter<ElementAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final List<Element> elementList;



    public ElementAdapter(LayoutInflater inflater, List<Element> elementList) {
        this.inflater = inflater;
        this.elementList = elementList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Element element = elementList.get(position);
        holder.valueView.setText(element.getValue());
        holder.totalValueView.setText(element.getTotalValue());
        holder.dateView.setText(element.getDate());
//        if (MainActivity.outcome){
//            holder.valueView.setTextColor(Color.parseColor("#EB4C42"));
//            holder.totalValueView.setTextColor(Color.parseColor("#EB4C42"));
//            holder.dateView.setTextColor(Color.parseColor("#EB4C42"));
//        } else {
//            holder.valueView.setTextColor(Color.parseColor("#50C878"));
//            holder.totalValueView.setTextColor(Color.parseColor("#50C878"));
//            holder.dateView.setTextColor(Color.parseColor("#50C878"));
//        }
    }

    @Override
    public int getItemCount() {
        return elementList.size();
    }

    public void clear() {
        int size = elementList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                elementList.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }
    public void addAll(ArrayList<Element> list){
        elementList.addAll(list);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView valueView, totalValueView, dateView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            valueView = itemView.findViewById(R.id.tv_value);
            totalValueView = itemView.findViewById(R.id.tv_total_value);
            dateView = itemView.findViewById(R.id.tv_date);
        }
    }
}
