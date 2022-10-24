package com.github.tvbox.osc.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.ui.tv.widget.AudioWaveView;
import com.github.tvbox.osc.ui.tv.widget.Epginfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyEpgAdapter extends BaseAdapter {

    private List<Epginfo> data;
    private final Context context;
    public static float fontSize = 20;
    private int defaultSelection = 0;
    private int defaultShiyiSelection = 0;
    private boolean ShiyiSelection = false;
    private String shiyiDate = null;
    private String currentEpgDate = null;
    private int focusSelection = -1;
    SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");

    public MyEpgAdapter(List<Epginfo> data, Context context, int i, boolean t) {
        this.data = data;
        this.context = context;
        this.defaultSelection = i;
        this.ShiyiSelection = t;
    }

    public void updateData(Date epgDate, List<Epginfo> data) {
        currentEpgDate = timeFormat.format(epgDate);
        focusSelection = -1;
        defaultSelection = -1;
        this.data = data;
        notifyDataSetChanged();
    }

    public void setSelection(int i) {
        this.defaultSelection = i;
        notifyDataSetChanged();
    }

    public void setFocusSelection(int focusSelection) {
        notifyDataSetChanged();
        this.focusSelection = focusSelection;
    }

    public void setShiyiSelection(int i, boolean t) {
        this.defaultShiyiSelection = i;
        this.shiyiDate = t ? currentEpgDate : null;
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
    public Epginfo getItem(int i) {
        return data.get(i);
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
            Epginfo info = data.get(i);
            if (new Date().compareTo(info.startdateTime) >= 0 && new Date().compareTo(info.enddateTime) <= 0) {
                shiyi.setVisibility(View.VISIBLE);
                shiyi.setBackgroundColor(context.getResources().getColor(R.color.color_32364E));
                shiyi.setText("直播");
                shiyi.setTextColor(context.getResources().getColor(R.color.color_FFFFFF));
            } else if (new Date().compareTo(info.enddateTime) > 0) {
                shiyi.setVisibility(View.VISIBLE);
                shiyi.setBackgroundColor(context.getResources().getColor(R.color.color_353744));
                shiyi.setTextColor(context.getResources().getColor(R.color.color_FFFFFF));
                shiyi.setText("回看");
            } else if (new Date().compareTo(info.startdateTime) < 0) {
                shiyi.setVisibility(View.VISIBLE);
                shiyi.setBackgroundColor(context.getResources().getColor(R.color.color_3D3D3D));
                shiyi.setTextColor(context.getResources().getColor(R.color.color_FFFFFF));
                shiyi.setText("预约");
            } else {
                shiyi.setVisibility(View.GONE);
            }

            textview.setText(data.get(i).title);
            timeview.setText(data.get(i).start + " - " + data.get(i).end);
            textview.setTextColor(context.getResources().getColor(R.color.color_FFFFFF));
            timeview.setTextColor(context.getResources().getColor(R.color.color_FFFFFF));
            if (ShiyiSelection == false) {
                Date now = new Date();
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
                    textview.setTextColor(context.getResources().getColor(R.color.color_FFFFFF));
                    timeview.setTextColor(context.getResources().getColor(R.color.color_FFFFFF));
                    textview.setFreezesText(true);
                    timeview.setFreezesText(true);
                    shiyi.setText("回看中");
                    shiyi.setTextColor(context.getResources().getColor(R.color.color_FF0057));
                    shiyi.setBackgroundColor(context.getResources().getColor(R.color.color_26FFFFF));
                    wqddg_AudioWaveView.setVisibility(View.VISIBLE);
                } else {
                    wqddg_AudioWaveView.setVisibility(View.GONE);
                }
            }
        }
        return view;
    }
}


