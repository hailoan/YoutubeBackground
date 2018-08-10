package com.youtu.sleep.youtubbackground.data.source.local;

import android.content.Context;

import com.youtu.sleep.youtubbackground.data.model.popularvideo.Video;
import com.youtu.sleep.youtubbackground.data.source.YoutubeVideoDataSource;
import com.youtu.sleep.youtubbackground.data.source.local.db.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thuy on 08/08/2018.
 */
public class YoutubeVideoLocalDataSource implements YoutubeVideoDataSource.LocalDataSource {

    private static YoutubeVideoLocalDataSource sInstance;

    private static DatabaseManager mDatabaseManager;
    private CallBack mCallBack;

    public static YoutubeVideoLocalDataSource getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new YoutubeVideoLocalDataSource();
            mDatabaseManager = new DatabaseManager(context);
        }
        return sInstance;
    }

    @Override
    public void addToFavouriteVideoList(Video video, CallBack callBack) {
        this.mCallBack = callBack;
        if (mDatabaseManager.insertAFavouriteVideo(video)) {
            this.mCallBack.onAddOrRemoveSuccess();
        } else this.mCallBack.onFail(null);
    }

    @Override
    public void getFavouriteVideos(CallBack<List<Video>> callBack) {
        this.mCallBack = callBack;
        ArrayList<Video> videos = mDatabaseManager.getFavouriteVideos();
        if (videos == null || videos.isEmpty()) {
            this.mCallBack.onFail(null);
        } else {
            this.mCallBack.onGetDataSuccess(videos);
        }
    }

    @Override
    public void removeFromFavouriteVideoList(Video video, CallBack callBack) {
        this.mCallBack = callBack;
        mDatabaseManager.removeAFavouriteVideo(video.getVideoId());
        this.mCallBack.onAddOrRemoveSuccess();
    }
}
