package com.youtu.sleep.youtubbackground.data.source.remote.listvideo;

import com.youtu.sleep.youtubbackground.data.model.popularvideo.Video;
import com.youtu.sleep.youtubbackground.data.source.YoutubeVideoDataSource;

import java.util.List;

/**
 * Created by thuy on 06/08/2018.
 */
public class HomeVideoRemoteDataSource implements YoutubeVideoDataSource.RemoteDataSource {

    private static final String MY_TAG = HomeVideoRemoteDataSource.class.getSimpleName();
    private CallBack<List<Video>> mCallBack;

    private static HomeVideoRemoteDataSource sInstance;

    public static HomeVideoRemoteDataSource getInstance(){
        if(sInstance == null){
            sInstance = new HomeVideoRemoteDataSource();
        }
        return sInstance;
    }

    @Override
    public void getPopularVideos(CallBack<List<Video>> callBack) {
        this.mCallBack = callBack;
        loadData();
    }

    @Override
    public void searchVideos(String query, CallBack callBack) {
        this.mCallBack = callBack;
        searchVideo(query);
    }

    private void searchVideo(String query) {
        SearchVideoAsyncTask myAsyncTask = new SearchVideoAsyncTask(mCallBack);
        myAsyncTask.execute(query);
    }

    private void loadData() {
        GetPopularVideoAsyncTask myAsyncTask = new GetPopularVideoAsyncTask(mCallBack);
        myAsyncTask.execute();
    }
}
