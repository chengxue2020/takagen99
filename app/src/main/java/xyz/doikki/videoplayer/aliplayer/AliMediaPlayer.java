package xyz.doikki.videoplayer.aliplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.media3.common.Player;
import androidx.media3.common.VideoSize;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorCode;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.nativeclass.MediaInfo;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.nativeclass.TrackInfo;
import com.aliyun.player.source.UrlSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import xyz.doikki.videoplayer.player.AbstractPlayer;

/**
 * <pre>
 *     author : derek
 *     time   : 2022/12/07
 *     desc   :
 *     version:
 * </pre>
 */
public class AliMediaPlayer extends AbstractPlayer implements Player.Listener {

    private final AliPlayer aliPlayer;
    private final IPlayer.OnCompletionListener onCompletionListener = new IPlayer.OnCompletionListener() {
        @Override
        public void onCompletion() {
//            notifyOnCompletion();
            aliPlayer.stop();
            //Log.e(TAG, "onCompletionListener onCompletion ");
        }
    };
    private final IPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new IPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(int width, int height) {
//            mVideoWidth = width;
//            mVideoHeight = height;
//            notifyOnVideoSizeChanged(width, height, 1, 1);
            mPlayerEventListener.onVideoSizeChanged(width, height);

            //Log.e(TAG, "onVideoSizeChangedListener " + width + " " + height);
        }
    };
    private final IPlayer.OnSeekCompleteListener onSeekCompleteListener = new IPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete() {
            //Log.e(TAG, "onSeekCompleteListener onSeekComplete ");
        }
    };
    private final IPlayer.OnSubtitleDisplayListener onSubtitleDisplayListener = new IPlayer.OnSubtitleDisplayListener() {
        @Override
        public void onSubtitleExtAdded(int i, String s) {
            Log.d("derek110", "onSubtitleExtAdded: " + s);
        }

        @Override
        public void onSubtitleShow(int i, long l, String s) {
            Log.d("derek110", "onSubtitleShow: " + s);

        }

        @Override
        public void onSubtitleHide(int i, long l) {
            Log.d("derek110", "onSubtitleHide: ");
        }

        @Override
        public void onSubtitleHeader(int i, String s) {
            Log.d("derek110", "onSubtitleHeader: " + s);
        }
    };
    private final IPlayer.OnErrorListener onErrorListener = new IPlayer.OnErrorListener() {
        @Override
        public void onError(ErrorInfo errorInfo) {
            ErrorCode errorCode = errorInfo.getCode(); //错误码。
            String errorMsg = errorInfo.getMsg(); //错误描述。
            //出错后需要停止掉播放器。
            aliPlayer.stop();
            mPlayerEventListener.onError();
        }
    };
    private boolean isAliPlayerStart;
    private long currentPos;
    private int bufferPercent;
    private final IPlayer.OnLoadingStatusListener onLoadingStatusListener = new IPlayer.OnLoadingStatusListener() {
        @Override
        public void onLoadingBegin() {
            //Log.e(TAG, "onLoadingStatusListener onLoadingBegin ");
//            notifyOnInfo(IMediaPlayer.MEDIA_INFO_BUFFERING_START, 0);
        }

        @Override
        public void onLoadingProgress(int percent, float netSpeed) {
            //Log.e(TAG, "onLoadingStatusListener onLoadingProgress percent = " + percent + " netSpeed = " + netSpeed);
            bufferPercent = percent;
        }

        @Override
        public void onLoadingEnd() {
            //Log.e(TAG, "onLoadingStatusListener onLoadingEnd ");
//            notifyOnInfo(IMediaPlayer.MEDIA_INFO_BUFFERING_END, 0);
        }
    };
    private long netSpeedLong;
    private final IPlayer.OnInfoListener onInfoListener = new IPlayer.OnInfoListener() {
        @Override
        public void onInfo(InfoBean infoBean) {
            InfoCode code = infoBean.getCode(); //信息码。
            String msg = infoBean.getExtraMsg();//信息内容。
            long value = infoBean.getExtraValue(); //信息值。
            Log.d("derek110", "onInfo: " + code + " msg " + msg + "value " + value);
            if (infoBean.getCode() == InfoCode.CurrentDownloadSpeed) {
                //当前下载速度
                netSpeedLong = infoBean.getExtraValue();
                //Log.e(TAG, "sourceVideoPlayerInfo CurrentDownloadSpeed = " + netSpeedLong);
            } else if (infoBean.getCode() == InfoCode.BufferedPosition) {
//                //更新bufferedPosition
//                mVideoBufferedPosition = infoBean.getExtraValue();
            } else if (infoBean.getCode() == InfoCode.CurrentPosition) {
                //更新currentPosition
                currentPos = infoBean.getExtraValue();
            }

            //当前进度：InfoCode.CurrentPosition
            //当前缓存位置：InfoCode.BufferedPosition
        }
    };
    private MediaInfo mAliyunMediaInfo;
    private List<TrackInfo> mAudioTrackInfoList;//音频
    private List<TrackInfo> mSubtitleTrackInfoList;//字幕
    private final IPlayer.OnPreparedListener onPreparedListener = new IPlayer.OnPreparedListener() {

        @Override
        public void onPrepared() {
            aliPlayer.start();
            isAliPlayerStart = true;
            mAliyunMediaInfo = aliPlayer.getMediaInfo();
            mPlayerEventListener.onPrepared();

            mAudioTrackInfoList = getTrackInfoListWithTrackInfoType(TrackInfo.Type.TYPE_AUDIO);
//            mBitrateTrackInfoList = getTrackInfoListWithTrackInfoType(TrackInfo.Type.TYPE_VIDEO);
//            mDefinitionTrackInfoList = getTrackInfoListWithTrackInfoType(TrackInfo.Type.TYPE_VOD);
            mSubtitleTrackInfoList = getTrackInfoListWithTrackInfoType(TrackInfo.Type.TYPE_SUBTITLE);


            for (int i = 0; i < mSubtitleTrackInfoList.size(); i++) {
                Log.d("derek110", "mSubtitleTrackInfoList: " + mSubtitleTrackInfoList.get(i).getSubtitleLang());
            }


        }
    };
    //视频播放状态
    private int mPlayState = IPlayer.unknow;
    private final IPlayer.OnStateChangedListener onStateChangedListener = new IPlayer.OnStateChangedListener() {
        @Override
        public void onStateChanged(int i) {
            mPlayState = i;
            //          int idle = 0;
            //          int initalized = 1;
            //          int prepared = 2;
            //          int started = 3;
            //          int paused = 4;
            //          int stopped = 5;
            //          int completion = 6;

            switch (i) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    mPlayerEventListener.onPrepared();
                    break;
                case 3:
                    mPlayerEventListener.onInfo(AbstractPlayer.MEDIA_INFO_RENDERING_START, 0);
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    mPlayerEventListener.onCompletion();
                    break;
            }

            //Log.e(TAG, "onStateChangedListener onStateChanged " + i);
        }
    };

    public AliMediaPlayer(Context context) {
        aliPlayer = AliPlayerFactory.createAliPlayer(context);
//        Android播放器SDK支持使用HTTP/2协议，该协议通过多路复用，避免队头阻塞，以改善播放性能。示例如下：
//        AliPlayerGlobalSettings.setUseHttp2(true);
    }

    @Override
    public void initPlayer() {
        aliPlayer.setOnErrorListener(onErrorListener);
        aliPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        aliPlayer.setOnCompletionListener(onCompletionListener);
        aliPlayer.setOnPreparedListener(onPreparedListener);
        aliPlayer.setOnInfoListener(onInfoListener);
        aliPlayer.setOnStateChangedListener(onStateChangedListener);
        aliPlayer.setOnLoadingStatusListener(onLoadingStatusListener);
        aliPlayer.setOnSeekCompleteListener(onSeekCompleteListener);
        aliPlayer.setOnSubtitleDisplayListener(onSubtitleDisplayListener);
    }

    @Override
    public void setDataSource(String path, Map<String, String> headers) {
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(path);//播放地址，可以是第三方点播地址，或阿里云点播服务中的播放地址，也可以是本地视频地址。
        if (headers != null && !headers.isEmpty()) {
            String[] itemArray = new String[headers.size()];
            Set<Map.Entry<String, String>> entries = headers.entrySet();
            int i = 0;
            for (Map.Entry<String, String> entry : entries) {
                itemArray[i] = entry.getKey() + ":" + entry.getValue();
                i++;
            }
            PlayerConfig config = aliPlayer.getConfig();
            config.setCustomHeaders(itemArray);
            aliPlayer.setConfig(config);
        }
        aliPlayer.setDataSource(urlSource);
    }

    @Override
    public void setDataSource(AssetFileDescriptor fd) {
//        aliPlayer.setDataSource();

    }

    @Override
    public void start() {
        aliPlayer.start();
        isAliPlayerStart = true;
    }

    @Override
    public void pause() {
        aliPlayer.pause();
        isAliPlayerStart = false;

    }

    @Override
    public void stop() {
        aliPlayer.stop();
        isAliPlayerStart = false;

    }

    @Override
    public void prepareAsync() {
        aliPlayer.prepare();

    }

    @Override
    public void reset() {
        aliPlayer.reset();

    }

    @Override
    public boolean isPlaying() {
        return isAliPlayerStart;
    }

    @Override
    public void seekTo(long time) {
        aliPlayer.seekTo(time);
        currentPos = time;

    }

    @Override
    public void release() {
        aliPlayer.release();
    }

    @Override
    public long getCurrentPosition() {
        return currentPos;
    }

    @Override
    public long getDuration() {
        return aliPlayer.getDuration();
    }

    @Override
    public int getBufferedPercentage() {
        return bufferPercent;
    }

    @Override
    public void setSurface(Surface surface) {
        aliPlayer.setSurface(surface);

    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        aliPlayer.setDisplay(holder);

    }

    @Override
    public void setVolume(float v1, float v2) {
        aliPlayer.setVolume(v1);
    }

    @Override
    public void setLooping(boolean isLooping) {
        aliPlayer.setLoop(isLooping);

    }

    @Override
    public void setOptions() {
    }

    @Override
    public float getSpeed() {
        return aliPlayer.getSpeed();
    }

    @Override
    public void setSpeed(float speed) {
        aliPlayer.setSpeed(speed);
    }

    @Override
    public long getTcpSpeed() {
        return netSpeedLong / 10L;
    }

    @Override
    public void onVideoSizeChanged(VideoSize videoSize) {
        int videoWidth = videoSize.width;
        int videoHeight = videoSize.height;
        if (videoWidth != 0 && videoHeight != 0) {
            mPlayerEventListener.onVideoSizeChanged(videoWidth, videoHeight);
        }
    }

    /**
     * 根据TrackInfo.Type 获取对应的TrackInfo集合
     */
    private List<TrackInfo> getTrackInfoListWithTrackInfoType(TrackInfo.Type trackInfoType) {
        List<TrackInfo> trackInfoList = new ArrayList<>();
        if (mAliyunMediaInfo != null && mAliyunMediaInfo.getTrackInfos() != null) {
            for (TrackInfo trackInfo : mAliyunMediaInfo.getTrackInfos()) {
                TrackInfo.Type type = trackInfo.getType();
                if (type == trackInfoType) {

                    if (trackInfoType == TrackInfo.Type.TYPE_SUBTITLE) {
                        //字幕
                        if (!TextUtils.isEmpty(trackInfo.getSubtitleLang())) {
                            trackInfoList.add(trackInfo);
                        }
                    } else if (trackInfoType == TrackInfo.Type.TYPE_AUDIO) {
                        //音轨
                        if (!TextUtils.isEmpty(trackInfo.getAudioLang())) {
                            trackInfoList.add(trackInfo);
                        }
                    } else if (trackInfoType == TrackInfo.Type.TYPE_VIDEO) {
                        //码率
                        if (trackInfo.getVideoBitrate() > 0) {
                            if (trackInfoList.size() == 0) {
                                //添加自动码率
                                trackInfoList.add(trackInfo);
                            }
                            trackInfoList.add(trackInfo);
                        }
                    } else if (trackInfoType == TrackInfo.Type.TYPE_VOD) {
                        //清晰度
                        if (!TextUtils.isEmpty(trackInfo.getVodDefinition())) {
                            trackInfoList.add(trackInfo);
                        }
                    }

                }
            }
        }
        return trackInfoList;
    }
}