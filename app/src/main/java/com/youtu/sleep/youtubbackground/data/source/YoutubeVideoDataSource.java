package com.youtu.sleep.youtubbackground.data.source;

import com.youtu.sleep.youtubbackground.data.model.popularvideo.Video;
import com.youtu.sleep.youtubbackground.screens.main.home.PopularVideosContract;

import java.util.List;

/**
 * Created by thuy on 06/08/2018.
 */
public interface YoutubeVideoDataSource {

    /**
     * Local data for videos
     */

    interface LocalDataSource extends YoutubeVideoDataSource {
        interface OnActionLocalListener {
            void onSuccess();
            void onSuccess(List<Video> list);

            void onFail();
        }

        void addToFavouriteVideoList(Video video, OnActionLocalListener listener);

        void getFavouriteVideos(OnActionLocalListener listener);

        void removeFromFavouriteVideoList(Video video, OnActionLocalListener listener);
    }

    /**
     * Remote data for videos
     */

    interface RemoteDataSource extends YoutubeVideoDataSource {
        interface OnGetPopularVideosListener {
            void onSuccess(List<Video> videos);

            void onFail(String message);
        }

        void getPopularVideos(OnGetPopularVideosListener listener);
    }
}