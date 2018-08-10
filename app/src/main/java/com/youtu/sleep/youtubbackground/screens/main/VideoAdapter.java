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
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {
    private static VideoAdapter.OnClickItemVideoListener sOnClickVideo;
    private static List<Video> sVideos;
    private OnItemClick mListener;

    public VideoAdapter(OnItemClick listener) {
        this.mListener = listener;
        this.sVideos = new ArrayList<>();
    }

    public VideoAdapter() {
        this.sVideos = new ArrayList<>();
    }

    public void setOnClickVideoListener(OnClickItemVideoListener onClickVideo) {
        this.sOnClickVideo = onClickVideo;
    }

    public void setData(List<Video> videos) {
        this.sVideos.clear();
        this.sVideos.addAll(videos);
        notifyDataSetChanged();
    }

    public List<Video> getVideos() {
        return sVideos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return sVideos == null ? 0 : sVideos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private boolean isFavourite = false;

        private ImageView mImageVideo, mImageFavourite;
        private TextView mTextDuration, mTextVideoName, mTextChannel, mTextDescription;
        private RelativeLayout mRelativeVideo;

        public MyViewHolder(final View itemView) {
            super(itemView);

            mImageVideo = itemView.findViewById(R.id.image_video);
            mImageFavourite = itemView.findViewById(R.id.image_favourite);
            mTextDuration = itemView.findViewById(R.id.text_duration);
            mTextVideoName = itemView.findViewById(R.id.text_name);
            mTextChannel = itemView.findViewById(R.id.text_channel);
            mTextDescription = itemView.findViewById(R.id.text_description);
            mRelativeVideo = itemView.findViewById(R.id.relative_video);

            mImageFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isFavourite) {
                        checkFavouriteVideo();
                    } else {
                        unCheckFavouriteVideo();
                    }
                    notifyDataSetChanged();
                }
            });
        }

        void checkFavouriteVideo() {
            Video v = sVideos.get(getAdapterPosition());
            v.setIsFavourite(TRUE);
            mListener.onFavouriteVideoClick(v);
            isFavourite = true;
        }

        void unCheckFavouriteVideo() {
            Video v = sVideos.get(getAdapterPosition());
            v.setIsFavourite(FALSE);
            mListener.onRemoveFavouriteVideoClick(v);
            isFavourite = false;
        }

        void bindData(int position) {
            Video video = sVideos.get(position);
            Glide.with(itemView.getContext()).load(video.getUrlThumbnail()).into(mImageVideo);
            mTextVideoName.setText(video.getTitle());
            mTextChannel.setText(video.getChannelTitle());
            mTextDescription.setText(video.getDescription());
            if (video.getIsFavourite() == TRUE) {
                mImageFavourite.setBackgroundResource(R.drawable.ic_favourite_default);
            } else if (video.getIsFavourite() == FALSE) {
                mImageFavourite.setBackgroundResource(R.drawable.ic_favourite_unable);
            }
            mRelativeVideo.setTag(position);
            mRelativeVideo.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            sOnClickVideo.onClickItemVideo(position);
        }
    }

    public interface OnItemClick {
        void onFavouriteVideoClick(Video video);

        void onRemoveFavouriteVideoClick(Video video);

    }

    public interface OnClickItemVideoListener {
        void onClickItemVideo(int position);
    }
}
