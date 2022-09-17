package com.github.tvbox.osc.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.ui.tv.widget.AudioWaveView;
import com.github.tvbox.osc.ui.tv.widget.Epginfo;

import java.util.Date;
import java.util.List;

public class MyEpgAdapter extends BaseAdapter {

    private List<Epginfo> data;
    private Context context;
    public static float fontSize = 20;
    private int defaultSelection = 0;
    private int defaultShiyiSelection = 0;
    private boolean ShiyiSelection = false;

    public MyEpgAdapter(List<Epginfo> data, Context context, int i, boolean t) {
        this.data = data;
        this.context = context;
        this.defaultSelection = i;
        this.ShiyiSelection = t;
    }

    public void setSelection(int i) {
        this.defaultSelection = i;
        notifyDataSetChanged();
    }

    public void setShiyiSelection(int i, boolean t) {
        this.defaultShiyiSelection = i;
        ShiyiSelection = t;
        notifyDataSetChanged();
    }

    public void setFontSize(float f) {
        fontSize = f;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.epglist_item, viewGroup, false);
        }
        TextView textview = (TextView) view.findViewById(R.id.tv_epg_name);
        TextView timeview = (TextView) view.findViewById(R.id.tv_epg_time);
        TextView shiyi = (TextView) view.findViewById(R.id.shiyi);
        AudioWaveView wqddg_AudioWaveView = (AudioWaveView) view.findViewById(R.id.wqddg_AudioWaveView);
        wqddg_AudioWaveView.setVisibility(View.GONE);
        if (i < data.size()) {
            if (new Date().compareTo(((Epginfo) data.get(i)).startdateTime) >= 0 && new Date().compareTo(((Epginfo) data.get(i)).enddateTime) <= 0) {
                shiyi.setVisibility(View.VISIBLE);
                shiyi.setBackgroundColor(Color.YELLOW);
                shiyi.setBackgroundColor(Color.YELLOW);
                shiyi.setText("直播");
                shiyi.setTextColor(Color.RED);
            } else if (new Date().compareTo(((Epginfo) data.get(i)).enddateTime) > 0) {
                shiyi.setVisibility(View.VISIBLE);
                shiyi.setBackgroundColor(Color.BLUE);
                shiyi.setTextColor(Color.WHITE);
                shiyi.setText("回看");
            } else if (new Date().compareTo(((Epginfo) data.get(i)).startdateTime) < 0) {
                shiyi.setVisibility(View.VISIBLE);
                shiyi.setBackgroundColor(Color.GRAY);
                shiyi.setTextColor(Color.BLACK);
                shiyi.setText("预约");
            } else {
                shiyi.setVisibility(View.GONE);
            }

            textview.setText(data.get(i).title);
            timeview.setText(data.get(i).start + " - " + data.get(i).end);
            textview.setTextColor(Color.WHITE);
            timeview.setTextColor(Color.WHITE);
            Log.e("roinlong", "getView: " + i);
            if (ShiyiSelection == false) {
                if (i == this.defaultSelection) {
                    wqddg_AudioWaveView.setVisibility(View.VISIBLE);
                    textview.setTextColor(context.getResources().getColor(R.color.color_FF0057));
                    timeview.setTextColor(context.getResources().getColor(R.color.color_FF0057));
                    textview.setFreezesText(true);
                    timeview.setFreezesText(true);
                } else {
                    wqddg_AudioWaveView.setVisibility(View.GONE);
                }
            } else {
                if (i == this.defaultSelection || i == this.defaultShiyiSelection) {
                    wqddg_AudioWaveView.setVisibility(View.VISIBLE);
                    textview.setTextColor(Color.rgb(0, 153, 255));
                    timeview.setTextColor(Color.rgb(0, 153, 255));
                    textview.setFreezesText(true);
                    timeview.setFreezesText(true);
                    shiyi.setText("回看中");
                    shiyi.setTextColor(Color.RED);
                    shiyi.setBackgroundColor(Color.rgb(12, 255, 0));
                    wqddg_AudioWaveView.setVisibility(View.VISIBLE);
                } else {
                    wqddg_AudioWaveView.setVisibility(View.GONE);
                }
            }
        }
        return view;
    }
}


