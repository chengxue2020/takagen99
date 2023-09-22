package xyz.doikki.videoplayer.exo;

import androidx.annotation.VisibleForTesting;
import androidx.media3.common.Format;
import androidx.media3.common.util.Assertions;
import androidx.media3.common.util.TimestampAdjuster;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.hls.HlsMediaChunkExtractor;
import androidx.media3.exoplayer.hls.WebvttExtractor;
import androidx.media3.extractor.Extractor;
import androidx.media3.extractor.ExtractorInput;
import androidx.media3.extractor.ExtractorOutput;
import androidx.media3.extractor.PositionHolder;
import androidx.media3.extractor.mp3.Mp3Extractor;
import androidx.media3.extractor.mp4.FragmentedMp4Extractor;
import androidx.media3.extractor.ts.Ac3Extractor;
import androidx.media3.extractor.ts.Ac4Extractor;
import androidx.media3.extractor.ts.AdtsExtractor;

import com.google.androidx.media3.exoplayer.extractor.ts.MyTsExtractor;

import java.io.IOException;

@UnstableApi
/**
 * 作者：By 15968
 * 日期：On 2021/10/6
 * 时间：At 19:16
 */

public class MyBundledHlsMediaChunkExtractor implements HlsMediaChunkExtractor {
    private static final PositionHolder POSITION_HOLDER = new PositionHolder();
    @VisibleForTesting
    final Extractor extractor;
    private final Format masterPlaylistFormat;
    private final TimestampAdjuster timestampAdjuster;

    public MyBundledHlsMediaChunkExtractor(Extractor extractor, Format masterPlaylistFormat, TimestampAdjuster timestampAdjuster) {
        this.extractor = extractor;
        this.masterPlaylistFormat = masterPlaylistFormat;
        this.timestampAdjuster = timestampAdjuster;
    }

    @Override
    public void init(ExtractorOutput extractorOutput) {
        extractor.init(extractorOutput);
    }

    @Override
    public boolean read(ExtractorInput extractorInput) throws IOException {
        try {
            return this.extractor.read(extractorInput, POSITION_HOLDER) == Extractor.RESULT_CONTINUE;
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof androidx.media3.common.ParserException) {
                return false;
            }
            throw e;
        }
    }

    public boolean isPackedAudioExtractor() {
        return extractor instanceof AdtsExtractor
                || extractor instanceof Ac3Extractor
                || extractor instanceof Ac4Extractor
                || extractor instanceof Mp3Extractor;
    }

    @Override
    public boolean isReusable() {
        return extractor instanceof MyTsExtractor || extractor instanceof FragmentedMp4Extractor;
    }

    @Override
    public HlsMediaChunkExtractor recreate() {
        Assertions.checkState(!isReusable());
        Extractor newExtractorInstance;
        if (extractor instanceof WebvttExtractor) {
            newExtractorInstance = new WebvttExtractor(masterPlaylistFormat.language, timestampAdjuster);
        } else if (extractor instanceof AdtsExtractor) {
            newExtractorInstance = new AdtsExtractor();
        } else if (extractor instanceof Ac3Extractor) {
            newExtractorInstance = new Ac3Extractor();
        } else if (extractor instanceof Ac4Extractor) {
            newExtractorInstance = new Ac4Extractor();
        } else if (extractor instanceof Mp3Extractor) {
            newExtractorInstance = new Mp3Extractor();
        } else {
            throw new IllegalStateException(
                    "Unexpected extractor type for recreation: " + extractor.getClass().getSimpleName());
        }
        return new MyBundledHlsMediaChunkExtractor(
                newExtractorInstance, masterPlaylistFormat, timestampAdjuster);
    }

    public void onTruncatedSegmentParsed() {
        this.extractor.seek(0L, 0L);
    }

}