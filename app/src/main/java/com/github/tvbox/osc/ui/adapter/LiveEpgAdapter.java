package com.github.tvbox.osc.ui.adapter;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.Epginfo;
import com.github.tvbox.osc.ui.tv.widget.AudioWaveView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LiveEpgAdapter extends BaseQuickAdapter<Epginfo, BaseViewHolder> {
    private int selectedEpgIndex = -1;
    private int focusedEpgIndex = -1;
    public static float fontSize = 20;
    private final int defaultShiyiSelection = 0;
    private boolean ShiyiSelection = false;
    private String shiyiDate = null;
    private final String currentEpgDate = null;
    private final int focusSelection = -1;
    private boolean source_include_back = false;

    SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");

    public LiveEpgAdapter() {
        super(R.layout.item_epglist, new ArrayList<>());
    }

    public void CanBack(Boolean source_include_back) {
        this.source_include_back = source_include_back;
    }

    @Override
    protected void convert(BaseViewHolder holder, Epginfo value) {
        Date now = new Date();
        TextView textview = holder.getView(R.id.tv_epg_name);
        TextView timeview = holder.getView(R.id.tv_epg_time);
        TextView shiyi = holder.getView(R.id.shiyi);
        AudioWaveView wqddg_AudioWaveView = holder.getView(R.id.wqddg_AudioWaveView);
        wqddg_AudioWaveView.setVisibility(View.GONE);

        if (value.index == selectedEpgIndex && value.index != focusedEpgIndex && (value.currentEpgDate.equals(shiyiDate) || value.currentEpgDate.equals(timeFormat.format(now)))) {
            textview.setTextColor(mContext.getResources().getColor(R.color.color_FF0057));
            timeview.setTextColor(mContext.getResources().getColor(R.color.color_FF0057));
        } else {
            textview.setTextColor(mContext.getResources().getColor(R.color.color_FFFFFF));
            timeview.setTextColor(mContext.getResources().getColor(R.color.color_FFFFFF));
        }

        if (now.compareTo(value.startdateTime) >= 0 && now.compareTo(value.enddateTime) <= 0) {
            shiyi.setVisibility(View.VISIBLE);
            shiyi.setBackgroundColor(mContext.getResources().getColor(R.color.color_32364E));
            shiyi.setTextColor(mContext.getResources().getColor(R.color.color_FFFFFF));
            shiyi.setText("直播");
        } else if (now.compareTo(value.enddateTime) > 0 && source_include_back) {
            shiyi.setVisibility(View.VISIBLE);
            shiyi.setBackgroundColor(mContext.getResources().getColor(R.color.color_32364E_40));
            shiyi.setTextColor(mContext.getResources().getColor(R.color.color_FFFFFF));
            shiyi.setText("回看");
        } else if (now.compareTo(value.startdateTime) < 0 && source_include_back) {
            shiyi.setVisibility(View.VISIBLE);
            shiyi.setBackgroundColor(mContext.getResources().getColor(R.color.color_3D3D3D));
            shiyi.setTextColor(mContext.getResources().getColor(R.color.color_FFFFFF));
            shiyi.setText("预约");
        } else {
            shiyi.setVisibility(View.GONE);
        }

        textview.setText(value.title);
        timeview.setText(value.start + " - " + value.end);
        textview.setTextColor(mContext.getResources().getColor(R.color.color_FFFFFF));
        timeview.setTextColor(mContext.getResources().getColor(R.color.color_FFFFFF));
        if (ShiyiSelection == false) {
            if (now.compareTo(value.startdateTime) >= 0 && now.compareTo(value.enddateTime) <= 0) {
                wqddg_AudioWaveView.setVisibility(View.VISIBLE);
                textview.setTextColor(mContext.getResources().getColor(R.color.color_FF0057));
                timeview.setTextColor(mContext.getResources().getColor(R.color.color_FF0057));
                textview.setFreezesText(true);
                timeview.setFreezesText(true);
                shiyi.setText("直播中");
            } else {
                wqddg_AudioWaveView.setVisibility(View.GONE);
            }
        } else {
            if (value.index == this.selectedEpgIndex && value.currentEpgDate.equals(shiyiDate)) {
                wqddg_AudioWaveView.setVisibility(View.VISIBLE);
                textview.setTextColor(mContext.getResources().getColor(R.color.color_FF0057));
                timeview.setTextColor(mContext.getResources().getColor(R.color.color_FF0057));
                textview.setFreezesText(true);
                timeview.setFreezesText(true);
                shiyi.setText("回看中");
                if (now.compareTo(value.startdateTime) >= 0 && now.compareTo(value.enddateTime) <= 0) {
                    shiyi.setText("直播中");
                }
                wqddg_AudioWaveView.setVisibility(View.VISIBLE);
            } else {
                wqddg_AudioWaveView.setVisibility(View.GONE);
            }
        }
    }

    public void setShiyiSelection(int i, boolean t, String currentEpgDate) {
        this.selectedEpgIndex = i;
        this.shiyiDate = t ? currentEpgDate : null;
        ShiyiSelection = t;
        notifyItemChanged(this.selectedEpgIndex);

    }

    public int getSelectedIndex() {
        return selectedEpgIndex;
    }

    public void setSelectedEpgIndex(int selectedEpgIndex) {
        if (selectedEpgIndex == this.selectedEpgIndex) return;
        this.selectedEpgIndex = selectedEpgIndex;
        if (this.selectedEpgIndex != -1)
            notifyItemChanged(this.selectedEpgIndex);
    }

    public int getFocusedEpgIndex() {
        return focusedEpgIndex;
    }

    public void setFocusedEpgIndex(int focusedEpgIndex) {
        this.focusedEpgIndex = focusedEpgIndex;
        if (this.focusedEpgIndex != -1)
            notifyItemChanged(this.focusedEpgIndex);
    }
}
