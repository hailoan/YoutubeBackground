package com.youtu.sleep.youtubbackground.screens.main.home;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.youtu.sleep.youtubbackground.R;
import com.youtu.sleep.youtubbackground.data.model.popularvideo.Video;
import com.youtu.sleep.youtubbackground.data.repository.YoutubeVideoRepository;
import com.youtu.sleep.youtubbackground.screens.BaseFragment;
import com.youtu.sleep.youtubbackground.screens.main.VideoAdapter;
import com.youtu.sleep.youtubbackground.screens.video.VideoActivity;
import com.youtu.sleep.youtubbackground.utils.Contants;
import com.youtu.sleep.youtubbackground.utils.navigator.Navigator;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements PopularVideosContract.View,
        VideoAdapter.OnClickItemVideoListener {

    private VideoAdapter mAdapter;

    public VideoAdapter getmAdapter() {
        return mAdapter;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializationViews(view);
        getData();
    }

    /**
     * call get video data method
     */

    private void getData() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) (getContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        if (networkInfo != null && networkInfo.isConnected()) {
            YoutubeVideoRepository repository = YoutubeVideoRepository.getInstance(getContext());
            PopularVideosPresenter mPresenter = new PopularVideosPresenter(this, repository);
            mPresenter.getPopularVideos();
        } else {
            Toast.makeText(getContext(), R.string.connect_network_fail_message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * setup view
     */

    private void initializationViews(View view) {
        RecyclerView mRecyclerVideos = view.findViewById(R.id.recycler_most_popular_videos);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mRecyclerVideos.setLayoutManager(llm);
        mAdapter = new VideoAdapter();
        mAdapter.setOnClickVideoListener(this);
        mRecyclerVideos.setAdapter(mAdapter);
    }

    /**
     * show result if get data successfully
     */

    @Override
    public void showPopularVideos(List<Video> videos) {
        mAdapter.setData(videos);
    }

    /**
     * show error message if get data unsuccessfully
     */

    @Override
    public void showGetPopularVideosErrorMessage(String message) {
        Toast.makeText(getContext(), R.string.load_video_data_fail_message + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void insertVideoListSuccessfully() {

    }

    @Override
    public void insertVideoListUnsuccessfully() {

    }

    @Override
    public void removeVideoFromFavouriteListSuccessfully() {

    }

    @Override
    public void onClickItemVideo(int position) {
        Intent intent = new Intent(getActivity(), VideoActivity.class);
        intent.putParcelableArrayListExtra(Contants.EXTRA_LIST_VIDEO
                , (ArrayList<? extends Parcelable>) mAdapter.getVideos());
        intent.putExtra(Contants.EXTRA_POS_VIDEO, position);
        new Navigator(getActivity()).startActivity(intent);
    }
}
