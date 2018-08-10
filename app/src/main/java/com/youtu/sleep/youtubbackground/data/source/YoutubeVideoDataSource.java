package com.youtu.sleep.youtubbackground.data.source;

import com.youtu.sleep.youtubbackground.data.model.popularvideo.Video;

import java.util.List;

/**
 * Created by thuy on 06/08/2018.
 */
public interface YoutubeVideoDataSource {

    interface CallBack<T>{
        void onGetDataSuccess(T data);
        void onAddOrRemoveSuccess();
        void onFail(String message);
    }

    /**
     * Local data for videos
     */

    interface LocalDataSource extends YoutubeVideoDataSource {

        void addToFavouriteVideoList(Video video, CallBack callBack);

        void getFavouriteVideos(CallBack<List<Video>> callBack);

        void removeFromFavouriteVideoList(Video video, CallBack callBack);
    }

    /**
     * Remote data for videos
     */

    interface RemoteDataSource extends YoutubeVideoDataSource {

        void getPopularVideos(CallBack<List<Video>> callBack);

        void searchVideos(String query, CallBack<List<Video>> callBack);
    }
}
