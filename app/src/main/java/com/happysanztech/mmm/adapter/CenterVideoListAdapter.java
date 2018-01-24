package com.happysanztech.mmm.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.happysanztech.mmm.R;
import com.happysanztech.mmm.app.AppController;
import com.happysanztech.mmm.bean.support.CenterVideo;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

/**
 * Created by Admin on 11-01-2018.
 */

public class CenterVideoListAdapter extends BaseAdapter {

    //    private final Transformation transformation;
    private static final String TAG = TaskPictureListAdapter.class.getName();
    private final Transformation transformation;
    private Context context;
    private ArrayList<CenterVideo> centerVideos;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();
    private ImageLoader imageLoader = AppController.getInstance().getUniversalImageLoader();

    public CenterVideoListAdapter(Context context, ArrayList<CenterVideo> centerVideos) {
        this.context = context;
        this.centerVideos = centerVideos;
        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(0)
                .oval(false)
                .build();
        mSearching = false;
    }


    @Override
    public int getCount() {
        if (mSearching) {
            if (!mAnimateSearch) {
                mAnimateSearch = true;
            }
            return mValidSearchIndices.size();

        } else {
            return centerVideos.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mSearching) {
            return centerVideos.get(mValidSearchIndices.get(position));
        } else {
            return centerVideos.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.center_videos_list_item, parent, false);

            holder = new ViewHolder();
            holder.videoView = convertView.findViewById(R.id.videoView);
            holder.textView = convertView.findViewById(R.id.textView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        /*if (mSearching) {
            position = mValidSearchIndices.get(position);
        } else {
            Log.d("Event List Adapter", "getview pos called" + position);
        }*/

        CenterVideo centerVideo = centerVideos.get(position);

        holder.textView.setText(centerVideos.get(position).getVideoTitle());

       /* holder.videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.videoView.start();
            }
        });

        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // This is just to show image when loaded
                mp.start();
                mp.pause();
            }
        });

        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // setLooping(true) didn't work, thats why this workaround
                holder.videoView.setVideoPath(centerVideos.get(position).getVideoURL());
                holder.videoView.start();*/

                MediaController mediaController= new MediaController(context);
                mediaController.setAnchorView(holder.videoView);
                Uri uri=Uri.parse("rtsp://v6.cache4.c.youtube.com/CigLENy73wIaHwmh5W2TKCuN2RMYDSANFEgGUgx1c2VyX3VwbG9hZHMM/0/0/0/video.3gp");
                holder.videoView.setMediaController(mediaController);
                holder.videoView.setVideoURI(uri);
                holder.videoView.requestFocus();

                holder.videoView.start();
          /*  }
        });*/

        /*Uri uri = Uri.parse(centerVideos.get(position).getVideoURL());
        holder.videoView.setVideoURI(uri);
        holder.videoView.requestFocus();
        holder.videoView.start();*/

       /* MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(videoView);
        Uri uri=Uri.parse("rtsp://r2---sn-a5m7zu76.c.youtube.com/Ck0LENy73wIaRAnTmlo5oUgpQhMYESARFEgGUg5yZWNvbW1lbmRhdGlvbnIhAWL2kyn64K6aQtkZVJdTxRoO88HsQjpE1a8d1GxQnGDmDA==/0/0/0/video.3gp");
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();

        videoView.start();*/

        return convertView;
    }

    public void startSearch(String eventName) {
        mSearching = true;
        mAnimateSearch = false;
        Log.d("EventListAdapter", "serach for event" + eventName);
        mValidSearchIndices.clear();
        for (int i = 0; i < centerVideos.size(); i++) {
            String classStudent = centerVideos.get(i).getVideoTitle();
            if ((classStudent != null) && !(classStudent.isEmpty())) {
                if (classStudent.toLowerCase().contains(eventName.toLowerCase())) {
                    mValidSearchIndices.add(i);
                }
            }
        }
        Log.d("Event List Adapter", "notify" + mValidSearchIndices.size());
    }

    public void exitSearch() {
        mSearching = false;
        mValidSearchIndices.clear();
        mAnimateSearch = false;
    }

    public void clearSearchFlag() {
        mSearching = false;
    }

    public class ViewHolder {
        public VideoView videoView;
        public TextView textView;
    }

    public boolean ismSearching() {
        return mSearching;
    }

    public int getActualEventPos(int selectedSearchpos) {
        if (selectedSearchpos < mValidSearchIndices.size()) {
            return mValidSearchIndices.get(selectedSearchpos);
        } else {
            return 0;
        }
    }
}
