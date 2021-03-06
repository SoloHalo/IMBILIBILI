package com.cql.imbilibili.widget.media;

/**
 * Created by CQL on 2016/10/23.
 */

public interface MediaPlayerControl {
    void start();

    void pause();

    int getDuration();

    int getCurrentPosition();

    void seekTo(int pos);

    boolean isPlaying();

    int getBufferPercentage();

    void showMediaInfo();
}
