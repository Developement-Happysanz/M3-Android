package com.happysanztech.mmm.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.happysanztech.mmm.R;
import com.happysanztech.mmm.app.AppController;
import com.happysanztech.mmm.bean.support.TaskPicture;
import com.happysanztech.mmm.utils.MobilizerValidator;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

/**
 * Created by Admin on 10-01-2018.
 */

public class TaskPictureListAdapter extends BaseAdapter {

    private static final String TAG = TaskPictureListAdapter.class.getName();
    private final Transformation transformation;
    private Context context;
    private ArrayList<TaskPicture> taskPictures;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();
    private ImageLoader imageLoader = AppController.getInstance().getUniversalImageLoader();

    public TaskPictureListAdapter(Context context, ArrayList<TaskPicture> taskPictures) {
        this.context = context;
        this.taskPictures = taskPictures;

        transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(0)
                .oval(false)
                .build();
        mSearching = false;
    }

    @Override
    public int getCount() {
        if (mSearching) {
            // Log.d("Event List Adapter","Search count"+mValidSearchIndices.size());
            if (!mAnimateSearch) {
                mAnimateSearch = true;
            }
            return mValidSearchIndices.size();

        } else {
            // Log.d(TAG,"Normal count size");
            return taskPictures.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mSearching) {
            return taskPictures.get(mValidSearchIndices.get(position));
        } else {
            return taskPictures.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.task_picture_list_item, parent, false);

            holder = new ViewHolder();
            holder.txtId = (TextView) convertView.findViewById(R.id.txt_id);
            holder.imageView = (ImageView) convertView.findViewById(R.id.img_logo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TaskPicture taskPicture = taskPictures.get(position);

        holder.txtId.setText(taskPictures.get(position).getId());

        if (MobilizerValidator.checkNullString(taskPictures.get(position).getTaskImage())) {
            Picasso.get().load(taskPictures.get(position).getTaskImage()).fit().transform(this.transformation).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_profile);
        }

        return convertView;
    }

    public class ViewHolder {
        public TextView txtId;
        public ImageView imageView;
    }
}
