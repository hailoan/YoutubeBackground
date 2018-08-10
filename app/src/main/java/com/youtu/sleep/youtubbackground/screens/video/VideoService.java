package com.youtu.sleep.youtubbackground.screens.video;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.SurfaceHolder;
import android.widget.RemoteViews;

import com.youtu.sleep.youtubbackground.R;
import com.youtu.sleep.youtubbackground.data.model.popularvideo.Video;
import com.youtu.sleep.youtubbackground.utils.Contants;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by DaiPhongPC on 8/8/2018.
 */

public class VideoService extends Service implements MediaPlayer.OnPreparedListener,
        OnDowloadBitmap {
    public static final int ID = 1000;
    public static VideoCallback mCallbackVideo;
    private int mPosition;
    private List<Video> mListVideos;
    private MediaPlayer mMediaPlayer;
    private IBinder mIbind = new BinderService();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        receiveIntent(intent);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        switch (intent.getAction()) {
            case ActionVideo.PLAY_NEW:
                mPosition = intent.getIntExtra(Contants.EXTRA_POSS, 0);
                mListVideos = intent.getParcelableArrayListExtra(Contants.EXTRA_LIST_VIDEOS);
                playVideo();
                break;
            case ActionVideo.ADD_HOLDER:
                mCallbackVideo.addHolderSurface(mMediaPlayer);
                break;
        }
        return mIbind;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        mCallbackVideo.alreadyPlayVideo();
        mCallbackVideo.addHolderSurface(mediaPlayer);
        mCallbackVideo.updateStatusVideo(mediaPlayer.isPlaying());
    }

    @Override
    public void onSuccess(Bitmap bitmap) {
        updateNotification(mListVideos.get(mPosition), bitmap);
    }

    @Override
    public void onFail() {

    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public List<Video> getListVideos() {
        return mListVideos;
    }

    public Video getCurrentVideo() {
        return mListVideos.get(mPosition);
    }

    /**
     * receive Intent from notification
     */
    public void receiveIntent(Intent intent) {
        switch (intent.getAction()) {
            case ActionVideo.CREAT_NOTI:
                startForeground(ID, creatNotification(mListVideos.get(mPosition)));
                break;
            case ActionVideo.PLAY:
                changeMediaStatus();
                updateNotification(mListVideos.get(mPosition), null);
                break;
            case ActionVideo.NEXT:
                mPosition = ListVideoControler.getVideoNext(mPosition, mListVideos.size());
                stopVideo();
                playVideo();
                updateNotification(mListVideos.get(mPosition), null);
                new DowloadThumbnailVideo(this).execute(mListVideos.get(mPosition).getUrlThumbnail());
                break;
            case ActionVideo.PRE:
                mPosition = ListVideoControler.getVideoPrevious(mPosition);
                stopVideo();
                playVideo();
                updateNotification(mListVideos.get(mPosition), null);
                new DowloadThumbnailVideo(this).execute(mListVideos.get(mPosition).getUrlThumbnail());
                break;
        }
    }

    /**
     * play new video
     */
    public void playVideo() {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mListVideos.get(mPosition).getUrl());
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } catch (IOException e) {
            e.printStackTrace();
            mCallbackVideo.showMessagePlayVideoError();
        }
    }

    /**
     * play or pause video
     */
    public void changeMediaStatus() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
        }
        mCallbackVideo.updateStatusVideo(mMediaPlayer.isPlaying());
    }

    /**
     * stop Video
     */
    public void stopVideo() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }

    /**
     * get current duration video
     */
    public int getCurrentDurationVideo() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
    }

    /**
     * get  duration video
     */
    public int getDurationVideo() {
        return mMediaPlayer != null ? mMediaPlayer.getDuration() : 0;
    }

    /**
     * change current time of video
     */
    public void changeCurrenttime(int mms) {
        mMediaPlayer.seekTo(mms);
    }

    /**
     * remove holder
     */
    public void setHolder(SurfaceHolder holder) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(holder);
        }
    }

    /**
     * creat a custom notification
     */
    public Notification creatNotification(Video video) {
        RemoteViews remoteViews = creatRemoteViews(video);
        Intent i = new Intent(this, VideoActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra(Contants.EXTRA_NOTIFICATION, true);
        PendingIntent intent = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(intent);
        Notification notification = builder.build();
        notification.contentView = remoteViews;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification.bigContentView = remoteViews;
        }
        return notification;
    }

    /**
     * update notification
     */
    public void updateNotification(Video video, Bitmap thum) {
        RemoteViews remoteViews = creatRemoteViews(video);
        if (mMediaPlayer.isPlaying()) {
            remoteViews.setImageViewResource(R.id.image_play, R.drawable.ic_pause);
        } else {
            remoteViews.setImageViewResource(R.id.image_play, R.drawable.ic_play);
        }
        if (thum != null) remoteViews.setImageViewBitmap(R.id.image_thumb, thum);
        Intent i = new Intent(this, VideoActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        i.putExtra(Contants.EXTRA_NOTIFICATION, true);
        i.putExtra(Contants.EXTRA_POS_VIDEO, mPosition);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(intent);
        Notification notification = builder.build();
        notification.contentView = remoteViews;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification.bigContentView = remoteViews;
        }
        NotificationManagerCompat.from(this).notify(ID, notification);
    }


    /**
     * creat remoteviews
     */
    public RemoteViews creatRemoteViews(Video video) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_small_layout);
        remoteViews.setTextViewText(R.id.title_video, video.getTitle());
        remoteViews.setTextViewText(R.id.chanel_video, video.getChannelTitle());
        PendingIntent pendingNext = creatPendingIntent(ActionVideo.NEXT);
        PendingIntent pendingPre = creatPendingIntent(ActionVideo.PRE);
        PendingIntent pendingPlay = creatPendingIntent(ActionVideo.PLAY);
        remoteViews.setOnClickPendingIntent(R.id.image_prev, pendingPre);
        remoteViews.setOnClickPendingIntent(R.id.image_next, pendingNext);
        remoteViews.setOnClickPendingIntent(R.id.image_play, pendingPlay);
        return remoteViews;
    }

    private PendingIntent creatPendingIntent(@ActionVideo String action) {
        Intent intent = new Intent(this, VideoService.class);
        intent.setAction(action);
        PendingIntent pb = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pb;
    }

    public class BinderService extends Binder {
        VideoService getService() {
            return VideoService.this;
        }
    }

    public class DowloadThumbnailVideo extends AsyncTask<String, Void, Bitmap> {
        private OnDowloadBitmap mOnDowloadBitmap;
        private Exception exception;

        public DowloadThumbnailVideo(OnDowloadBitmap onDowloadBitmap) {
            this.mOnDowloadBitmap = onDowloadBitmap;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return image;
            } catch (IOException e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (exception == null) mOnDowloadBitmap.onSuccess(bitmap);
            else mOnDowloadBitmap.onFail();
        }
    }
}
