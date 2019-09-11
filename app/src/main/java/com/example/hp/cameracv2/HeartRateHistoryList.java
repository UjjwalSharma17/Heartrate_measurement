package com.example.hp.cameracv2;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class HeartRateHistoryList extends ArrayAdapter<HeartRateHistoryInfoActivity> {

    private Activity context;
    private List<HeartRateHistoryInfoActivity> historyList;

    public HeartRateHistoryList(Activity context, List<HeartRateHistoryInfoActivity> historyList){
        super(context,R.layout.list_layout,historyList);
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();

        View listViewItem = layoutInflater.inflate(R.layout.list_layout,null,true);

        TextView dateTextView = listViewItem.findViewById(R.id.item_date);
        TextView timeTextView = listViewItem.findViewById(R.id.item_time);
        TextView heartRateTextView = listViewItem.findViewById(R.id.item_heart_rate);

        HeartRateHistoryInfoActivity heartRateInfo = historyList.get(position);

        dateTextView.setText(heartRateInfo.mDate);
        timeTextView.setText(heartRateInfo.mTime);
        int temp = heartRateInfo.heartRate;
        heartRateTextView.setText(Integer.toString(temp));

        return listViewItem;

    }
}
