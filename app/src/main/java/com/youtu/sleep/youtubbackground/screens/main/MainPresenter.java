package com.youtu.sleep.youtubbackground.screens.main;

import com.youtu.sleep.youtubbackground.data.model.popularvideo.Video;
import com.youtu.sleep.youtubbackground.data.repository.YoutubeVideoRepository;
import com.youtu.sleep.youtubbackground.data.source.YoutubeVideoDataSource;

import java.util.List;

/**
 * Created by thuy on 01/08/2018.
 */
public class MainPresenter implements MainContract.Presenter {

    private MainContract.View mView;
    private YoutubeVideoRepository mModel;

    public MainPresenter(MainContract.View mView, YoutubeVideoRepository mModel) {
        this.mView = mView;
        this.mModel = mModel;
    }

    @Override
    public void searchVideo(String query) {
        mModel.searchVideos(query, new YoutubeVideoDataSource.CallBack<List<Video>>() {

            @Override
            public void onGetDataSuccess(List<Video> videos) {
                mView.showResultSearch(videos);
            }

            @Override
            public void onAddOrRemoveSuccess() {

            }

            @Override
            public void onFail(String message) {
                mView.showFailedSearchMessage(message);
            }
        });
    }

    @Override
    public void setView(MainContract.View view) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}
