package com.youtu.sleep.youtubbackground.data.repository;

import android.content.Context;

import com.youtu.sleep.youtubbackground.data.model.popularvideo.Video;
import com.youtu.sleep.youtubbackground.data.source.YoutubeVideoDataSource;
import com.youtu.sleep.youtubbackground.data.source.local.YoutubeVideoLocalDataSource;
import com.youtu.sleep.youtubbackground.data.source.remote.listvideo.HomeVideoRemoteDataSource;

import java.util.List;

/**
 * Created by thuy on 09/08/2018.
 */
public class YoutubeVideoRepository implements YoutubeVideoDataSource.RemoteDataSource, YoutubeVideoDataSource.LocalDataSource {

    private static YoutubeVideoRepository sInstance;

    private HomeVideoRemoteDataSource mYoutubeVideoRemoteDataSource;
    private YoutubeVideoLocalDataSource mYoutubeVideoLocalDataSource;

    private YoutubeVideoRepository(Context context) {
        mYoutubeVideoRemoteDataSource = HomeVideoRemoteDataSource.getInstance();
        mYoutubeVideoLocalDataSource = YoutubeVideoLocalDataSource.getInstance(context);
    }

    public static YoutubeVideoRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new YoutubeVideoRepository(context);
        }
        return sInstance;
    }

    @Override
    public void addToFavouriteVideoList(Video video, CallBack callBack) {
        mYoutubeVideoLocalDataSource.addToFavouriteVideoList(video, callBack);
    }

    @Override
    public void getFavouriteVideos(CallBack<List<Video>> callBack) {
        mYoutubeVideoLocalDataSource.getFavouriteVideos(callBack);
    }

    @Override
    public void removeFromFavouriteVideoList(Video video, CallBack callBack) {
        mYoutubeVideoLocalDataSource.removeFromFavouriteVideoList(video, callBack);
    }

    @Override
    public void searchVideos(String query, CallBack<List<Video>> callBack) {
        mYoutubeVideoRemoteDataSource.searchVideos(query, callBack);
    }

    @Override
    public void getPopularVideos(CallBack listener) {
        mYoutubeVideoRemoteDataSource.getPopularVideos(listener);
    }

}
