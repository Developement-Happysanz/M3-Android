package com.happysanztech.mmm.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.happysanztech.mmm.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 19-01-2018.
 */

public class CenterVideos extends AppCompatActivity implements YouTubeThumbnailView.OnInitializedListener, YouTubeThumbnailLoader.OnThumbnailLoadedListener, YouTubePlayer.OnInitializedListener {

    YouTubePlayerFragment playerFragment;
    YouTubePlayer Player;
    YouTubeThumbnailView thumbnailView;
    YouTubeThumbnailLoader thumbnailLoader;
    RecyclerView VideoList;
    RecyclerView.Adapter adapter;
    List<Drawable> thumbnailViews;
    List<String> VideoId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center_videos);
        thumbnailViews = new ArrayList<>();
        VideoList = (RecyclerView) findViewById(R.id.VideoList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        VideoList.setLayoutManager(layoutManager);
        adapter = new VideoListAdapter();
        VideoList.setAdapter(adapter);
        VideoId = new ArrayList<>();
        thumbnailView = new YouTubeThumbnailView(this);
        thumbnailView.initialize("AIzaSyCXfjl783QE_I5uqzot4fJXHc3dUO_Z2mc", this);
        playerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.VideoFragment);
        playerFragment.initialize("AIzaSyCXfjl783QE_I5uqzot4fJXHc3dUO_Z2mc", this);
        findViewById(R.id.back_tic_his).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
        thumbnailLoader = youTubeThumbnailLoader;
        youTubeThumbnailLoader.setOnThumbnailLoadedListener(CenterVideos.this);
        thumbnailLoader.setPlaylist("Y_UmWdcTrrc");
        thumbnailLoader.setPlaylist("Y_UmWdcTrrc");
    }

    @Override
    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

    }

    public void add() {
        adapter.notifyDataSetChanged();
        if (thumbnailLoader.hasNext())
            thumbnailLoader.next();
    }

    @Override
    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
        thumbnailViews.add(youTubeThumbnailView.getDrawable());
        VideoId.add(s);
        add();
    }

    @Override
    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        Player = youTubePlayer;
        Player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
            @Override
            public void onFullscreen(boolean b) {
                VideoList.setVisibility(b ? View.GONE : View.VISIBLE);
            }
        });
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.MyView> {

        public class MyView extends RecyclerView.ViewHolder {

            ImageView imageView;

            public MyView(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.thumbnailView);
            }

        }

        @Override
        public VideoListAdapter.MyView onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_row, parent, false);
            return new MyView(itemView);
        }

        @Override
        public void onBindViewHolder(VideoListAdapter.MyView holder, final int position) {
            holder.imageView.setImageDrawable(thumbnailViews.get(position));
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Player.cueVideo(VideoId.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return thumbnailViews.size();
        }
    }
}
