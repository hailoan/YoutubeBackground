package com.youtu.sleep.youtubbackground.screens.video;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.youtu.sleep.youtubbackground.R;
import com.youtu.sleep.youtubbackground.data.model.popularvideo.Video;
import com.youtu.sleep.youtubbackground.data.repository.UrlVideoRepository;
import com.youtu.sleep.youtubbackground.data.repository.YoutubeVideoRepository;
import com.youtu.sleep.youtubbackground.data.source.remote.playvideo.UrlVideoRemoteDataSource;
import com.youtu.sleep.youtubbackground.screens.BaseActivity;
import com.youtu.sleep.youtubbackground.utils.Contants;
import com.youtu.sleep.youtubbackground.utils.common.StringConverter;

import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends BaseActivity implements VideoContract.View,
        SurfaceHolder.Callback,
        VideoCallback {
    public static final int DELAY = 1000;
    private ImageView mImagePlay, mImageNext, mImagePre, mImageFa, mImageLoop, mImageBack;
    private TextView mTextTitle, mTextTimeVideo;
    private ProgressBar mProgressBar;
    private SeekBar mSeekbarVideo;
    private SurfaceView mSurfaceVideo;
    private SurfaceHolder mHolderVideo;
    private VideoContract.Presenter mPresenter;
    private VideoService mVideoService;
    private Runnable mRunnableSeekbar;
    private boolean mIsBound = false;
    private int mPositionVideo = -1;
    private boolean mIsStartByNoti = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        UrlVideoRepository urlRepository = UrlVideoRepository.getInstance(UrlVideoRemoteDataSource.getInstance());
        YoutubeVideoRepository videoRepository = YoutubeVideoRepository.getInstance(getBaseContext());
        mPresenter = new VideoPresenter(urlRepository, videoRepository);
        mPresenter.setView(this);
        setupView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mIsStartByNoti = getIntent().getBooleanExtra(Contants.EXTRA_NOTIFICATION, false);
        if (!mIsStartByNoti) {
            mPresenter.onStart();
        } else {
            startBindService();
        }
    }

    @Override
    public int getCurrentPositionVideo() {
        return mPositionVideo != -1 ? mPositionVideo :
                getIntent().getIntExtra(Contants.EXTRA_POS_VIDEO, 0);
    }

    @Override
    public List<Video> getListVideo() {
        return getIntent().getParcelableArrayListExtra(Contants.EXTRA_LIST_VIDEO);
    }

    @Override
    public String getIdVideo(int position) {
        return getListVideo().get(position).getVideoId();
    }

    @Override
    public void showListVideo(List<Video> videos) {
        mTextTitle.setText(videos.get(getCurrentPositionVideo()).getTitle());
        startBindService(videos);
    }

    @Override
    public void showMessageErrorExtraUrlVideo(String mess) {
        Toast.makeText(VideoActivity.this, mess, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateStatusFavouriteVideo(int status) {
        mVideoService.getListVideos().get(mPositionVideo).setIsFavourite(status);
        if (status == 1) {
            mImageFa.setImageResource(R.drawable.ic_favourite_default);
        } else {
            mImageFa.setImageResource(R.drawable.ic_favourite_unable);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.mHolderVideo = surfaceHolder;
        if (mIsBound) {
            mVideoService.setHolder(this.mHolderVideo);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mIsBound) {
            mVideoService.setHolder(null);
        }
    }

    /**
     * setup View
     */
    public void setupView() {
        mTextTimeVideo = findViewById(R.id.text_current);
        mProgressBar = findViewById(R.id.progress);
        setupToolbar();
        setupControlPlayer();
        setupVideoView();
    }


    @Override
    public void alreadyPlayVideo() {
        mProgressBar.setVisibility(View.GONE);
        mSeekbarVideo.setMax(mVideoService.getDurationVideo());
        mSeekbarVideo.postDelayed(mRunnableSeekbar = new Runnable() {
            @Override
            public void run() {
                mSeekbarVideo.setProgress(mVideoService.getCurrentDurationVideo());
                mTextTimeVideo.setText(StringConverter.convertTimestampToHhmmss(mSeekbarVideo.getProgress() / DELAY));
                mSeekbarVideo.postDelayed(mRunnableSeekbar, DELAY);
            }
        }, DELAY);
    }

    @Override
    public void addHolderSurface(MediaPlayer mp) {
        updateLayoutParameter(mp.getVideoWidth(), mp.getVideoHeight());
        mp.setDisplay(mHolderVideo);
    }

    @Override
    public void updateStatusVideo(boolean status) {
        if (status) {
            mImagePlay.setImageResource(R.drawable.ic_pause);
        } else {
            mImagePlay.setImageResource(R.drawable.ic_play);
        }
    }

    @Override
    public void showMessagePlayVideoError() {
        Toast.makeText(this, getString(R.string.error_play_video), Toast.LENGTH_SHORT).show();
    }

    /**
     * setup toolbar
     */
    public void setupToolbar() {
        mImageBack = findViewById(R.id.image_back);
        mTextTitle = findViewById(R.id.text_name_video);
        mImageBack.setOnClickListener(on_click);
    }

    /**
     * setup video view
     */
    public void setupVideoView() {
        mSurfaceVideo = findViewById(R.id.sf_video);
        mSeekbarVideo = findViewById(R.id.seekbar_video);
        mSeekbarVideo.setOnSeekBarChangeListener(onSeekbarChange);
        mSurfaceVideo.getHolder().addCallback(this);
    }

    /**
     * On Duration Video change
     */
    private SeekBar.OnSeekBarChangeListener onSeekbarChange = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {

            }
            seekBar.setProgress(i);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    /**
     * setup control play video
     */
    public void setupControlPlayer() {
        mImagePlay = findViewById(R.id.image_play);
        mImagePre = findViewById(R.id.image_prev);
        mImageNext = findViewById(R.id.image_next);
        mImageLoop = findViewById(R.id.image_loop);
        mImageFa = findViewById(R.id.image_favourite);
        mImagePlay.setOnClickListener(on_click);
        mImageNext.setOnClickListener(on_click);
        mImagePre.setOnClickListener(on_click);
        mImageFa.setOnClickListener(on_click);
        mImageLoop.setOnClickListener(on_click);
    }

    /**
     * onclick
     */
    private View.OnClickListener on_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.image_play:
                    mVideoService.changeMediaStatus();
                    break;
                case R.id.image_next:
                    mPositionVideo = ListVideoControler.getVideoNext(mPositionVideo, getListVideo().size());
                    stopSeekBar();
                    mVideoService.stopVideo();
                    mVideoService.setPosition(mPositionVideo);
                    mVideoService.playVideo();
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;
                case R.id.image_prev:
                    mPositionVideo = ListVideoControler.getVideoPrevious(mPositionVideo);
                    stopSeekBar();
                    mVideoService.setPosition(mPositionVideo);
                    mVideoService.playVideo();
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;
                case R.id.image_loop:
                    mPresenter.updateSettingLoopVideo();
                    break;
                case R.id.image_favourite:
                    mPresenter.updateVideoFavourite(mVideoService.getCurrentVideo());
                    break;
                case R.id.image_back:
                    break;
            }
        }
    };

    /**
     * start bind service
     */
    public void startBindService(List<Video> videos) {
        VideoService.mCallbackVideo = this;
        mPositionVideo = getCurrentPositionVideo();
        Intent intent = new Intent(VideoActivity.this, VideoService.class);
        intent.putExtra(Contants.EXTRA_POSS, mPositionVideo);
        intent.putParcelableArrayListExtra(Contants.EXTRA_LIST_VIDEOS, (ArrayList<? extends Parcelable>) videos);
        intent.setAction(ActionVideo.PLAY_NEW);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void startBindService() {
        VideoService.mCallbackVideo = this;
        mPositionVideo = getCurrentPositionVideo();
        Intent intent = new Intent(VideoActivity.this, VideoService.class);
        intent.setAction(ActionVideo.ADD_HOLDER);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * update layoutParameter Surface
     */
    public void updateLayoutParameter(int wVideo, int hVideo) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mSurfaceVideo.getLayoutParams();
        if (wVideo > hVideo) {
            params.width = width;
            float scale = width / (float) wVideo;
            params.height = (int) (hVideo * scale);
        } else {
            params.height = height;
            float scale = height / (float) hVideo;
            params.width = (int) (wVideo * scale);
        }
        mSurfaceVideo.setLayoutParams(params);
    }

    /**
     * cancel Handle;
     */
    public void stopSeekBar() {
        mSeekbarVideo.removeCallbacks(mRunnableSeekbar);
        mRunnableSeekbar = null;
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            VideoService.BinderService binder = (VideoService.BinderService) iBinder;
            mVideoService = binder.getService();
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mIsBound = false;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        Intent intentStart = new Intent(this, VideoService.class);
        intentStart.setAction(ActionVideo.CREAT_NOTI);
        startService(intentStart);
        if (mIsBound) {
            unbindService(mServiceConnection);
        }
    }

}
