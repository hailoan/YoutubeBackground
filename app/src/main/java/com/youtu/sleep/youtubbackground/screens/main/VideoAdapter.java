package com.youtu.sleep.youtubbackground.screens.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.youtu.sleep.youtubbackground.R;
import com.youtu.sleep.youtubbackground.data.model.popularvideo.Video;

import java.util.ArrayList;
import java.util.List;

import static com.youtu.sleep.youtubbackground.utils.Contants.FALSE;
import static com.youtu.sleep.youtubbackground.utils.Contants.TRUE;

/**
 * Created by thuy on 01/08/2018.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private static List<Video> mVideos;
    private static OnItemClickListener mListener;

    public VideoAdapter(OnItemClickListener listener) {
        this.mListener = listener;
        this.mVideos = new ArrayList<>();
    }

    public VideoAdapter() {
        this.mVideos = new ArrayList<>();
    }

    public void setData(List<Video> videos) {
        this.mVideos.clear();
        this.mVideos.addAll(videos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.bindData(mVideos.get(position));
    }

    public void notifyDataChanged() {
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mVideos == null ? 0 : mVideos.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {

        private boolean isFavourite = false;

        private ImageView mImageVideo, mImageFavourite;
        private TextView mTextDuration, mTextVideoName, mTextChannel, mTextDescription;

        public VideoViewHolder(final View itemView) {
            super(itemView);

            mImageVideo = itemView.findViewById(R.id.image_video);
            mImageFavourite = itemView.findViewById(R.id.image_favourite);
            mTextDuration = itemView.findViewById(R.id.text_duration);
            mTextVideoName = itemView.findViewById(R.id.text_name);
            mTextChannel = itemView.findViewById(R.id.text_channel);
            mTextDescription = itemView.findViewById(R.id.text_description);
        }

        void bindData(final Video video) {
            Glide.with(itemView.getContext()).load(video.getUrlThumbnail()).into(mImageVideo);
            mTextVideoName.setText(video.getTitle());
            mTextChannel.setText(video.getChannelTitle());
            mTextDescription.setText(video.getDescription());

            if (video.getIsFavourite() == TRUE) {
                mImageFavourite.setBackgroundResource(R.drawable.ic_favourite_default);
            } else if (video.getIsFavourite() == FALSE) {
                mImageFavourite.setBackgroundResource(R.drawable.ic_favourite_unable);
            }

            mImageFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (video.getIsFavourite() == FALSE) {
                        video.setIsFavourite(TRUE);
                        mListener.onFavouriteVideoClick(video);

                    } else {
                        video.setIsFavourite(FALSE);
                        mListener.onRemoveFavouriteVideoClick(video);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onFavouriteVideoClick(Video video);

        void onRemoveFavouriteVideoClick(Video video);

    }
}
