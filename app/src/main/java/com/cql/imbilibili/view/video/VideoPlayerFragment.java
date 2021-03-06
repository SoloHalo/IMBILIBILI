package com.cql.imbilibili.view.video;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lh.danmakulibrary.BiliBiliDanmakuParser;
import com.lh.danmakulibrary.Danmaku;
import com.lh.danmakulibrary.DanmakuView;
import com.cql.imbilibili.R;
import com.cql.imbilibili.data.ApiException;
import com.cql.imbilibili.data.helper.PlusHelper;
import com.cql.imbilibili.data.helper.VideoPlayerHelper;
import com.cql.imbilibili.model.video.PlusVideoPlayerData;
import com.cql.imbilibili.model.video.VideoDetail;
import com.cql.imbilibili.model.video.VideoPlayData;
import com.cql.imbilibili.utils.DanmakuUtils;
import com.cql.imbilibili.utils.VideoUtils;
import com.cql.imbilibili.view.BaseFragment;
import com.cql.imbilibili.widget.media.VideoControlView;
import com.cql.imbilibili.widget.media.VideoPlayerView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by CQL on 2016/10/3.
 * 视频播放Fragment
 */

public class VideoPlayerFragment extends BaseFragment implements IMediaPlayer.OnInfoListener, IMediaPlayer.OnErrorListener, VideoControlView.OnPlayControlListener, IMediaPlayer.OnPreparedListener, VideoControlView.OnMediaControlViewVisibleChangeListener, IMediaPlayer.OnCompletionListener {

    private static final int MSG_SYNC_NOW = 1;
    private static final int MSG_SYNC_AT_TIME = 2;

    @BindView(R.id.pre_play_msg)
    TextView mPrePlayMsg;
    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;
    @BindView(R.id.tv_buffering)
    TextView mTvBuffering;
    @BindView(R.id.videoview)
    VideoPlayerView mIjkVideoView;
    @BindView(R.id.video_control_view)
    VideoControlView mVideoControlView;
    @BindView(R.id.danmaku_view)
    DanmakuView mDanmakuView;

    private VideoDetail mVideoDetail;
    private int mPage;

    private ArrayList<Danmaku> mDanmakus;

    private VideoHandler mHandler;
    private OnVideoFragmentStateListener mOnVideoFragmentStateListener;

    private int mLastPlayPosition;
    private int mCurrentQuality = 3;

    private StringBuilder mPreMsgBuilder;
    private PlusVideoPlayerData mPlusPlayerData;
    private int mCurrentSourceIndex;
    private String[] mSourcesList;

    public static VideoPlayerFragment newInstance(VideoDetail detail, int page) {
        VideoPlayerFragment fragment = new VideoPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", detail);
        bundle.putInt("page", page);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setOnFullScreemButtonClick(OnVideoFragmentStateListener listener) {
        mOnVideoFragmentStateListener = listener;
    }

    @Override
    protected void initView(View view) {
        mHandler = new VideoHandler(new WeakReference<>(this));
        ButterKnife.bind(this, view);
        mDanmakuView.setShowDebugInfo(false);
        mVideoDetail = getArguments().getParcelable("data");
        mPage = getArguments().getInt("page");
        mLastPlayPosition = 0;
        mCurrentSourceIndex = 1;
        initIjkPlayer();
        mVideoControlView.setVideoView(mIjkVideoView);
        mVideoControlView.setFullScreenButtonVisible(true);
        mVideoControlView.setTopMediaControlViewVisible(false);
        preparePlay();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_videoview;
    }

    private void initIjkPlayer() {
        mPreMsgBuilder = new StringBuilder();
        mPreMsgBuilder.append("初始化播放器...");
        mPrePlayMsg.setText(mPreMsgBuilder);
        mVideoControlView.setVideoTitle(mVideoDetail.getTitle());
        mIjkVideoView.setKeepScreenOn(true);
        mIjkVideoView.setOnPreparedListener(this);
        mIjkVideoView.setOnInfoListener(this);
        mIjkVideoView.setOnErrorListener(this);
        mVideoControlView.setOnPlayControlListener(this);
        mVideoControlView.setOnMediaControlViewVisibleChangeListener(this);
    }

    //加载视频播放所需的所有数据
    private void preparePlay() {
        mSourcesList = new String[]{"官方", "PLUS"};
        mVideoControlView.setSourceList(mSourcesList, mCurrentSourceIndex);
        mPreMsgBuilder.append("【完成】\n解析视频信息...【完成】\n" +
                "解析视频地址...\n" +
                "全舰弹幕填装...");
        mPrePlayMsg.setText(mPreMsgBuilder);
        Observable.merge(loadVideoInfoAccordSource(), downloadDanmaku())
                .subscribeOn(Schedulers.io())
                .ignoreElements()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        startPlaying();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private Observable<String> loadVideoInfoAccordSource() {
        if (mCurrentSourceIndex != 0) {
            return loadVideoInfoFromPlus();
        } else {
            return loadVideoInfo();
        }
    }

    private Observable<String> loadVideoInfo() {
        return VideoPlayerHelper.getInstance()
                .getOfficialService()
                .getPlayData(mVideoDetail.getAid(), 0, 0, 0, mVideoDetail.getPages().get(mPage).getCid() + "", mCurrentQuality, "json")
                .flatMap(new Function<VideoPlayData, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(VideoPlayData videoPlayData) {
                        if (videoPlayData.getDurl() != null && !videoPlayData.getDurl().isEmpty()) {
                            int[] qualities = videoPlayData.getAcceptQuality();
                            List<VideoControlView.QualityItem> qualityItems = new ArrayList<>();
                            for (int quality : qualities) {
                                String name;
                                if (quality == 1) {
                                    name = "流畅";
                                } else if (quality == 2) {
                                    name = "高清";
                                } else if (quality == 3) {
                                    name = "超清";
                                } else {
                                    name = "1080p";
                                }
                                qualityItems.add(new VideoControlView.QualityItem(name, quality));
                            }
                            mVideoControlView.setQualityList(qualityItems);
                            return VideoUtils.concatVideo(videoPlayData.getDurl()).subscribeOn(Schedulers.io());
                        } else {
                            return Observable.error(new ApiException(-1));
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        String target = "解析视频地址...";
                        int startPosition = mPreMsgBuilder.indexOf(target);
                        mPreMsgBuilder.insert(startPosition + target.length(), "【完成】");
                        mPrePlayMsg.setText(mPreMsgBuilder);
                        mIjkVideoView.setVideoPath(s);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        String target = "解析视频地址...";
                        int startPosition = mPreMsgBuilder.indexOf(target);
                        mPreMsgBuilder.insert(startPosition + target.length(), "【失败】");
                        mPrePlayMsg.setText(mPreMsgBuilder);
                    }
                });
    }

    private Observable<String> loadVideoInfoFromPlus() {
        return PlusHelper.getInstance()
                .getPlusService()
                .getPlayData(mVideoDetail.getSeason() == null ? 0 : 1, mVideoDetail.getAid(), mVideoDetail.getPages().get(mPage).getPage() + "")
                .subscribeOn(Schedulers.io())
                .compose(new ObservableTransformer<PlusVideoPlayerData, PlusVideoPlayerData>() {
                    @Override
                    public ObservableSource<PlusVideoPlayerData> apply(Observable<PlusVideoPlayerData> upstream) {
                        if (mPlusPlayerData != null) {
                            return Observable.just(mPlusPlayerData);
                        } else {
                            return upstream;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<PlusVideoPlayerData, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(PlusVideoPlayerData plusVideoPlayerData) {
                        mPlusPlayerData = plusVideoPlayerData;
                        if (!plusVideoPlayerData.getMode().equals("error")) {
                            List<PlusVideoPlayerData.Data> datas = plusVideoPlayerData.getData();
                            List<VideoControlView.QualityItem> qualityItems = new ArrayList<>();
                            for (int i = 0; i < datas.size(); i++) {
                                if (datas.get(i).getName().contains("超清")) {
                                    qualityItems.add(new VideoControlView.QualityItem("超清", 3));
                                } else if (datas.get(i).getName().contains("高清")) {
                                    qualityItems.add(new VideoControlView.QualityItem("高清", 2));
                                } else if (datas.get(i).getName().contains("低清")) {
                                    qualityItems.add(new VideoControlView.QualityItem("流畅", 1));
                                }
                            }
                            mVideoControlView.setQualityList(qualityItems);
                            int index = 0;
                            for (int i = 0; i < qualityItems.size(); i++) {
                                if (qualityItems.get(i).getId() == mCurrentQuality) {
                                    index = i;
                                    break;
                                }
                            }
                            if (TextUtils.isEmpty(plusVideoPlayerData.getData().get(index).getUrl())) {
                                return VideoUtils.concatPlusVideo(plusVideoPlayerData.getData().get(index).getParts()).subscribeOn(Schedulers.io());
                            } else {
                                return Observable.just(plusVideoPlayerData.getData().get(index).getUrl());
                            }
                        } else {
                            return Observable.error(new ApiException(-1, plusVideoPlayerData.getMode()));
                        }
                    }
                })
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {

                    boolean haveRetry = false;

                    @Override
                    public ObservableSource<?> apply(Observable<Throwable> observable) {
                        return observable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public Observable<?> apply(Throwable throwable) {
                                System.out.println("retryWhen：" + haveRetry);
                                if (!haveRetry) {
                                    haveRetry = true;
                                    System.out.println("updateInfo");
                                    return PlusHelper.getInstance().getPlusService().updateInfo(mVideoDetail.getAid(), 1).subscribeOn(Schedulers.io());
                                } else {
                                    return Observable.error(throwable);
                                }
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        System.out.println("Next" + Thread.currentThread().getName());
                        String target = "解析视频地址...";
                        int startPosition = mPreMsgBuilder.indexOf(target);
                        mPreMsgBuilder.insert(startPosition + target.length(), "【完成】");
                        mPrePlayMsg.setText(mPreMsgBuilder);
                        mIjkVideoView.setVideoPath(s);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        String target = "解析视频地址...";
                        int startPosition = mPreMsgBuilder.indexOf(target);
                        mPreMsgBuilder.insert(startPosition + target.length(), "【失败】");
                        mPrePlayMsg.setText(mPreMsgBuilder);
                    }
                });
    }

    private Observable<InputStream> downloadDanmaku() {
        return DanmakuUtils.downLoadDanmaku(mVideoDetail.getPages().get(mPage).getCid() + "")
                .doOnNext(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) {
                        preparDanmaku(inputStream);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends InputStream>>() {
                    @Override
                    public ObservableSource<? extends InputStream> apply(Throwable throwable) {
                        String target = "全舰弹幕填装...";
                        int startPosition = mPreMsgBuilder.indexOf(target);
                        mPreMsgBuilder.insert(startPosition + target.length(), "【失败】");
                        mPrePlayMsg.setText(mPreMsgBuilder);
                        return Observable.empty();
                    }
                })
                .doOnNext(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) {
                        String target = "全舰弹幕填装...";
                        int startPosition = mPreMsgBuilder.indexOf(target);
                        mPreMsgBuilder.insert(startPosition + target.length(), "【完成】");
                        mPrePlayMsg.setText(mPreMsgBuilder);
                    }
                });
    }

    private void preparDanmaku(InputStream stream) {
        if (mDanmakuView != null) {
            BiliBiliDanmakuParser mParser = new BiliBiliDanmakuParser();
            try {
                mDanmakus = mParser.parse(stream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mDanmakus != null) {
                mDanmakuView.setDanmakuSource(mDanmakus);
            }
        }
    }

    private void startPlaying() {
        mPreMsgBuilder.append("\n开始缓冲...");
        mPrePlayMsg.setText(mPreMsgBuilder);
        mHandler.removeMessages(MSG_SYNC_AT_TIME);
        if (mLastPlayPosition != 0) {
            mIjkVideoView.seekTo(mLastPlayPosition);
            mLastPlayPosition = 0;
        }
        mIjkVideoView.start();
        mHandler.sendEmptyMessage(MSG_SYNC_AT_TIME);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLastPlayPosition != 0 && !mIjkVideoView.isPlaying()) {
            mIjkVideoView.seekTo(mLastPlayPosition);
            mIjkVideoView.start();
        }
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mHandler.removeMessages(MSG_SYNC_AT_TIME);
            mDanmakuView.resume();
            mHandler.sendEmptyMessage(MSG_SYNC_AT_TIME);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mIjkVideoView.isPlaying()) {
            mLastPlayPosition = mIjkVideoView.getCurrentPosition();
            mIjkVideoView.pause();
        }
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mHandler.removeMessages(MSG_SYNC_AT_TIME);
            mHandler.removeMessages(MSG_SYNC_NOW);
            mDanmakuView.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mIjkVideoView.release(true);
        if (mDanmakuView != null) {
            mDanmakuView.release();
            mDanmakuView = null;
        }
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        switch (what) {
            case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                mProgressBar.setVisibility(View.GONE);
                mTvBuffering.setVisibility(View.GONE);
                mPrePlayMsg.setVisibility(View.GONE);
                mDanmakuView.start();
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                mHandler.removeMessages(MSG_SYNC_AT_TIME);
                mHandler.removeMessages(MSG_SYNC_NOW);
                mDanmakuView.pause();
                mTvBuffering.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                mHandler.removeMessages(MSG_SYNC_AT_TIME);
                mDanmakuView.resume();
                mHandler.sendEmptyMessage(MSG_SYNC_AT_TIME);
                mProgressBar.setVisibility(View.GONE);
                mTvBuffering.setVisibility(View.GONE);
                mPrePlayMsg.setVisibility(View.GONE);
                break;

        }
        return true;
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
//        startPlaying();
        return true;
    }

    @Override
    public void onQualitySelect(VideoControlView.QualityItem item) {
        if (item.getId() == mCurrentQuality) {
            return;
        }
        mCurrentQuality = item.getId();
        mPreMsgBuilder.delete(0, mPreMsgBuilder.length());
        mPreMsgBuilder.append("初始化播放器...");
        mPrePlayMsg.setText(mPreMsgBuilder);
        mLastPlayPosition = mIjkVideoView.getCurrentPosition();
        mTvBuffering.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mDanmakuView.pause();
        mIjkVideoView.release(true);
        preparePlay();
    }

    @Override
    public void onSourceChange(int index) {
        if (mCurrentSourceIndex == index) {
            return;
        }
        mCurrentSourceIndex = index;
        mPreMsgBuilder.delete(0, mPreMsgBuilder.length());
        mPreMsgBuilder.append("初始化播放器...");
        mPrePlayMsg.setText(mPreMsgBuilder);
        mLastPlayPosition = mIjkVideoView.getCurrentPosition();
        mTvBuffering.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mDanmakuView.pause();
        mIjkVideoView.release(true);
        preparePlay();
    }

    public void changeVideo(int page) {
        mPage = page;
        mVideoControlView.setVideoTitle(mVideoDetail.getTitle());
        mTvBuffering.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mIjkVideoView.release(true);
        preparePlay();
    }

    @Override
    public void onDanamkuShowOrHideClick() {
        if (mDanmakuView.isShow()) {
            mDanmakuView.hide();
        } else {
            mDanmakuView.show();
        }
    }

    @Override
    public void onFullScreenClick() {
        if (mOnVideoFragmentStateListener != null) {
            mOnVideoFragmentStateListener.onFullScreenClick();
        }
    }

    @Override
    public void onVideoPause() {
        mDanmakuView.pause();
    }

    @Override
    public void onVideoStart() {
        mHandler.removeMessages(MSG_SYNC_NOW);
        if (mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
        mHandler.sendEmptyMessage(MSG_SYNC_NOW);
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        mHandler.removeMessages(MSG_SYNC_NOW);
        mHandler.sendEmptyMessage(MSG_SYNC_NOW);
    }

    @Override
    public void onMediaControlViewVisibleChange(boolean isShow) {
        if (mOnVideoFragmentStateListener != null) {
            mOnVideoFragmentStateListener.onMediaControlBarVisibleChanged(isShow);
        }
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        mDanmakuView.pause();
    }

    private static class VideoHandler extends Handler {

        private WeakReference<VideoPlayerFragment> mFragment;

        private VideoHandler(WeakReference<VideoPlayerFragment> fragment) {
            mFragment = fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mFragment.get() == null
                    || mFragment.get().mIjkVideoView == null
                    || mFragment.get().mDanmakuView == null) {
                return;
            }
            switch (msg.what) {
                case MSG_SYNC_NOW:
                    if (mFragment.get().mDanmakuView.isPrepared()) {
                        long videoCurrentPosition = mFragment.get().mIjkVideoView.getCurrentPosition();
                        long danmakuCurrentPosition = mFragment.get().mDanmakuView.getCurrentTime();
                        long diff = danmakuCurrentPosition - videoCurrentPosition;
                        if (diff > 500 && diff < 1000) {
                            mFragment.get().mDanmakuView.pause();
                            sendEmptyMessageDelayed(MSG_SYNC_NOW, diff);
                        } else if (diff > 1000 || diff < -500) {
                            mFragment.get().mDanmakuView.seekTo(videoCurrentPosition);
                            sendEmptyMessage(MSG_SYNC_NOW);
                        } else {
                            if (mFragment.get().mIjkVideoView.isPlaying()) {
                                mFragment.get().mDanmakuView.resume();
                            }
                        }
                    }
                    break;
                case MSG_SYNC_AT_TIME:
                    removeMessages(MSG_SYNC_AT_TIME);
                    sendEmptyMessage(MSG_SYNC_NOW);
                    sendEmptyMessageDelayed(MSG_SYNC_AT_TIME, 1000 * 60);
                    break;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mVideoControlView.setTopMediaControlViewVisible(true);
            mVideoControlView.setFullScreenButtonVisible(false);
        } else {
            mVideoControlView.setTopMediaControlViewVisible(false);
            mVideoControlView.setFullScreenButtonVisible(true);
        }

    }


    public interface OnVideoFragmentStateListener {
        void onFullScreenClick();

        void onMediaControlBarVisibleChanged(boolean isShow);
    }
}
