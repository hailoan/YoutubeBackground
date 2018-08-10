package com.youtu.sleep.youtubbackground.screens.main.favourite;

import com.youtu.sleep.youtubbackground.data.model.popularvideo.Video;
import com.youtu.sleep.youtubbackground.data.repository.YoutubeVideoRepository;
import com.youtu.sleep.youtubbackground.data.source.YoutubeVideoDataSource;

import java.util.List;

/**
 * Created by thuy on 09/08/2018.
 */
public class FavouriteVideosPresenter implements FavouriteVideosContract.Presenter {

    private FavouriteVideosContract.View mView;
    private YoutubeVideoRepository mModel;

    public FavouriteVideosPresenter(FavouriteVideosContract.View mView, YoutubeVideoRepository repository) {
        this.mView = mView;
        this.mModel = repository;
    }

    @Override
    public void getFavouriteVideos() {
        mModel.getFavouriteVideos(new YoutubeVideoDataSource.CallBack<List<Video>>() {
            @Override
            public void onGetDataSuccess(List<Video> videos) {
                mView.showFavouriteVideos(videos);
            }

            @Override
            public void onAddOrRemoveSuccess() {

            }

            @Override
            public void onFail(String message) {
                mView.showFavouriteVideosErrorMessage();
            }
        });
    }
}
