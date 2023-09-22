package com.google.androidx.media3.exoplayer.extractor.ts;

import androidx.media3.extractor.ExtractorInput;
import androidx.media3.extractor.PositionHolder;
import androidx.media3.extractor.SeekMap;

public class TsBinarySearchSeeker {
    public TsBinarySearchSeeker(Object pcrTimestampAdjuster, long durationUs, long inputLength, int pcrPid, int timestampSearchBytes) {
    }

    public void setSeekTargetUs(long timeUs) {
    }

    public boolean isSeeking() {
        return false;
    }

    public int handlePendingSeek(ExtractorInput input, PositionHolder seekPosition) {
        return 0;
    }

    public SeekMap getSeekMap() {
        return null;
    }
}
