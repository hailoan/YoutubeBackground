package com.youtu.sleep.youtubbackground.screens.main.home;

import com.youtu.sleep.youtubbackground.data.model.popularvideo.Video;

import java.util.List;

/**
 * Created by thuy on 06/08/2018.
 */
public interface PopularVideosContract {
    interface View {
        void showPopularVideos(List<Video> videos);
        void showGetPopularVideosErrorMessage(String message);

        void insertVideoListSuccessfully();
        void insertVideoListUnsuccessfully();

        void removeVideoFromFavouriteListSuccessfully();
        void removeVideoFromFavouriteListUnsuccessfully();

    }

    interface Presenter {
        void getPopularVideos();

        void insertVideo(Video video);

        void removeVideo(Video video);
    }
}
