package com.youtu.sleep.youtubbackground.screens.video;

import com.youtu.sleep.youtubbackground.data.model.popularvideo.Video;
import com.youtu.sleep.youtubbackground.data.repository.UrlVideoRepository;
import com.youtu.sleep.youtubbackground.data.repository.YoutubeVideoRepository;
import com.youtu.sleep.youtubbackground.data.source.YoutubeVideoDataSource;

import java.util.List;

/**
 * Created by DaiPhongPC on 8/6/2018.
 */

public class VideoPresenter implements VideoContract.Presenter {
    private VideoContract.View mView;
    private UrlVideoRepository mUrlVideoRepository;
    private YoutubeVideoRepository mVideoRepository;

    public VideoPresenter(UrlVideoRepository mUrlVideoRepository
            , YoutubeVideoRepository videoRepository) {
        this.mUrlVideoRepository = mUrlVideoRepository;
        this.mVideoRepository = videoRepository;
    }

    @Override
    public void setView(VideoContract.View view) {
        this.mView = view;
    }

    @Override
    public void onStart() {
        getListUrlVideo();
    }

    @Override
    public void onStop() {

    }

    @Override

    public void getListUrlVideo() {
        mUrlVideoRepository.getListUrlVideo(mView.getListVideo(), new OnGetListUrlVideoYoutube() {
            @Override
            public void onSuccess(List<Video> videos) {
                mView.showListVideo(videos);
            }

            @Override
            public void onError(String mess) {
                mView.showMessageErrorExtraUrlVideo(mess);
            }
        });
    }

    @Override
    public void updateVideoFavourite(Video video) {
        mVideoRepository.addToFavouriteVideoList(video, new YoutubeVideoDataSource.CallBack() {
            @Override
            public void onGetDataSuccess(Object data) {

            }

            @Override
            public void onAddOrRemoveSuccess() {

            }

            @Override
            public void onFail(String message) {

            }
        });
    }

    @Override
    public void updateSettingLoopVideo() {

    }
}
