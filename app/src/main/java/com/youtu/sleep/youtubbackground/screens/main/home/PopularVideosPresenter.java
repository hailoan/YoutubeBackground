package com.youtu.sleep.youtubbackground.screens.main.home;

import com.youtu.sleep.youtubbackground.data.model.popularvideo.Video;
import com.youtu.sleep.youtubbackground.data.repository.YoutubeVideoRepository;
import com.youtu.sleep.youtubbackground.data.source.YoutubeVideoDataSource;

import java.util.List;

/**
 * Created by thuy on 06/08/2018.
 */
public class PopularVideosPresenter implements PopularVideosContract.Presenter {

    private PopularVideosContract.View mView;
    private YoutubeVideoRepository mModel;

    public PopularVideosPresenter(PopularVideosContract.View mView, YoutubeVideoRepository repository) {
        this.mView = mView;
        this.mModel = repository;
    }

    @Override
    public void getPopularVideos() {
        mModel.getPopularVideos(new YoutubeVideoDataSource.CallBack<List<Video>>() {
            @Override
            public void onGetDataSuccess(List<Video> videos) {
                mView.showPopularVideos(videos);
            }

            @Override
            public void onAddOrRemoveSuccess() {
                //nothing todo
            }

            @Override
            public void onFail(String message) {
                mView.showGetPopularVideosErrorMessage(message);
            }
        });
    }

    @Override
    public void insertVideo(Video video) {
        mModel.addToFavouriteVideoList(video, new YoutubeVideoDataSource.CallBack<List<Video>>() {
            @Override
            public void onGetDataSuccess(List<Video> data) {
                //nothing todo
            }

            @Override
            public void onAddOrRemoveSuccess() {
                mView.insertVideoListSuccessfully();
            }

            @Override
            public void onFail(String message) {
                mView.insertVideoListUnsuccessfully();
            }
        });
    }

    @Override
    public void removeVideo(Video video) {
        mModel.removeFromFavouriteVideoList(video, new YoutubeVideoDataSource.CallBack<List<Video>>() {
            @Override
            public void onGetDataSuccess(List<Video> data) {
                //nothing todo
            }

            @Override
            public void onAddOrRemoveSuccess() {
                mView.removeVideoFromFavouriteListSuccessfully();
            }

            @Override
            public void onFail(String message) {
                mView.removeVideoFromFavouriteListUnsuccessfully();
            }
        });
    }

}
