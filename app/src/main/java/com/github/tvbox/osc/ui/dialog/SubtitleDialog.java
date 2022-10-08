package com.github.tvbox.osc.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.ui.activity.HomeActivity;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.github.tvbox.osc.util.SubtitleHelper;

import org.jetbrains.annotations.NotNull;

public class SubtitleDialog extends BaseDialog {

    private TextView subtitleOption;
    public TextView selectInternal;
    public TextView selectLocal;
    public TextView selectRemote;
    private ImageView subtitleSizeMinus;
    private TextView subtitleSizeText;
    private ImageView subtitleSizePlus;
    private ImageView subtitleTimeMinus;
    private TextView subtitleTimeText;
    private ImageView subtitleTimePlus;
    private TextView subtitleStyleOne;
    private TextView subtitleStyleTwo;

    private SubtitleViewListener mSubtitleViewListener;
    private LocalFileChooserListener mLocalFileChooserListener;
    private SearchSubtitleListener mSearchSubtitleListener;

    public SubtitleDialog(@NonNull @NotNull Context context) {
        super(context);
        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }
        setContentView(R.layout.dialog_subtitle);
        initView(context);
    }

    private void initView(Context context) {
        subtitleOption = findViewById(R.id.title);
        selectInternal = findViewById(R.id.selectInternal);
        selectLocal = findViewById(R.id.selectLocal);
        selectRemote = findViewById(R.id.selectRemote);
        subtitleSizeMinus = findViewById(R.id.subtitleSizeMinus);
        subtitleSizeText = findViewById(R.id.subtitleSizeText);
        subtitleSizePlus = findViewById(R.id.subtitleSizePlus);
        subtitleTimeMinus = findViewById(R.id.subtitleTimeMinus);
        subtitleTimeText = findViewById(R.id.subtitleTimeText);
        subtitleTimePlus = findViewById(R.id.subtitleTimePlus);
        subtitleStyleOne = findViewById(R.id.subtitleStyleOne);
        subtitleStyleTwo = findViewById(R.id.subtitleStyleTwo);

        // Set Title Tip
        subtitleOption.setText(HomeActivity.getRes().getString(R.string.vod_sub_option));
        selectInternal.setText(HomeActivity.getRes().getString(R.string.vod_sub_int));
        selectLocal.setText(HomeActivity.getRes().getString(R.string.vod_sub_ext));
        selectRemote.setText(HomeActivity.getRes().getString(R.string.vod_sub_remote));
        subtitleSizeText.setText(HomeActivity.getRes().getString(R.string.vod_sub_size));
        subtitleTimeText.setText(HomeActivity.getRes().getString(R.string.vod_sub_delay));

        // Internal Subtitle from Video File
        selectInternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FastClickCheckUtil.check(view);
                dismiss();
                mSubtitleViewListener.selectInternalSubtitle();
            }
        });
        // Local Drive Subtitle
        selectLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FastClickCheckUtil.check(view);
                dismiss();
                mLocalFileChooserListener.openLocalFileChooserDialog();
            }
        });
        // Remote Search Subtitle
        selectRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FastClickCheckUtil.check(view);
                dismiss();
                mSearchSubtitleListener.openSearchSubtitleDialog();
            }
        });

        int size = SubtitleHelper.getTextSize(getOwnerActivity());
        subtitleSizeText.setText(Integer.toString(size));

        subtitleSizeMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sizeStr = subtitleSizeText.getText().toString();
                int curSize = Integer.parseInt(sizeStr);
                curSize -= 2;
                if (curSize <= 10) {
                    curSize = 10;
                }
                subtitleSizeText.setText(Integer.toString(curSize));
                SubtitleHelper.setTextSize(curSize);
                mSubtitleViewListener.setTextSize(curSize);
            }
        });
        subtitleSizePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sizeStr = subtitleSizeText.getText().toString();
                int curSize = Integer.parseInt(sizeStr);
                curSize += 2;
                if (curSize >= 60) {
                    curSize = 60;
                }
                subtitleSizeText.setText(Integer.toString(curSize));
                SubtitleHelper.setTextSize(curSize);
                mSubtitleViewListener.setTextSize(curSize);
            }
        });

        int timeDelay = SubtitleHelper.getTimeDelay();
        String timeStr = "0";
        if (timeDelay != 0) {
            double dbTimeDelay = timeDelay/1000;
            timeStr = Double.toString(dbTimeDelay);
        }
        subtitleTimeText.setText(timeStr);

        subtitleTimeMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FastClickCheckUtil.check(view);
                String timeStr = subtitleTimeText.getText().toString();
                double time = Float.parseFloat(timeStr);
                double oneceDelay = -0.5;
                time += oneceDelay;
                if (time == 0.0) {
                    timeStr = "0";
                } else {
                    timeStr = Double.toString(time);
                }
                subtitleTimeText.setText(timeStr);
                int mseconds = (int)(oneceDelay*1000);
                SubtitleHelper.setTimeDelay((int)(time*1000));
                mSubtitleViewListener.setSubtitleDelay(mseconds);
            }
        });
        subtitleTimePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FastClickCheckUtil.check(view);
                String timeStr = subtitleTimeText.getText().toString();
                double time = Float.parseFloat(timeStr);
                double oneceDelay = 0.5;
                time += oneceDelay;
                if (time == 0.0) {
                    timeStr = "0";
                } else {
                    timeStr = Double.toString(time);
                }
                subtitleTimeText.setText(timeStr);
                int mseconds = (int)(oneceDelay*1000);
                SubtitleHelper.setTimeDelay((int)(time*1000));
                mSubtitleViewListener.setSubtitleDelay(mseconds);
            }
        });

        subtitleStyleOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int style = 0;
                dismiss();
                mSubtitleViewListener.setTextStyle(style);
                Toast.makeText(getContext(), "设置样式成功", Toast.LENGTH_SHORT).show();
            }
        });
        subtitleStyleTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int style = 1;
                dismiss();
                mSubtitleViewListener.setTextStyle(style);
                Toast.makeText(getContext(), "设置样式成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setLocalFileChooserListener(LocalFileChooserListener localFileChooserListener) {
        mLocalFileChooserListener = localFileChooserListener;
    }

    public interface LocalFileChooserListener {
        void openLocalFileChooserDialog();
    }

    public void setSearchSubtitleListener(SearchSubtitleListener searchSubtitleListener) {
        mSearchSubtitleListener = searchSubtitleListener;
    }

    public interface SearchSubtitleListener {
        void openSearchSubtitleDialog();
    }

    public void setSubtitleViewListener(SubtitleViewListener subtitleViewListener) {
        mSubtitleViewListener = subtitleViewListener;
    }

    public interface SubtitleViewListener {
        void setTextSize(int size);
        void setSubtitleDelay(int milliseconds);
        void selectInternalSubtitle();
        void setTextStyle(int style);
    }

}