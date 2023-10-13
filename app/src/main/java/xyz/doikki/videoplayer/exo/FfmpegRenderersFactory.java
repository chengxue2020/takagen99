package xyz.doikki.videoplayer.exo;

import android.content.Context;
import android.os.Handler;
import androidx.media3.decoder.ffmpeg.FfmpegAudioRenderer;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.Renderer;
import androidx.media3.exoplayer.audio.AudioRendererEventListener;
import androidx.media3.exoplayer.audio.AudioSink;
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector;

import java.util.ArrayList;

/**
 * <pre>
 *     author : derek
 *     time   : 2022/12/21
 *     desc   :
 *     version:
 * </pre>
 */
class FfmpegRenderersFactory extends DefaultRenderersFactory {

    public FfmpegRenderersFactory(Context context) {
        super(context);
        setExtensionRendererMode(EXTENSION_RENDERER_MODE_ON);
    }

    @Override
    protected void buildAudioRenderers(Context context, int extensionRendererMode, MediaCodecSelector mediaCodecSelector, boolean enableDecoderFallback, AudioSink audioSink, Handler eventHandler, AudioRendererEventListener eventListener, ArrayList<Renderer> out) {
        out.add(new FfmpegAudioRenderer());
        super.buildAudioRenderers(context, extensionRendererMode, mediaCodecSelector, enableDecoderFallback, audioSink, eventHandler, eventListener, out);

    }
}


