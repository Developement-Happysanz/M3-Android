package com.happysanztech.mmm.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.happysanztech.mmm.R;
import com.happysanztech.mmm.app.AppController;
import com.happysanztech.mmm.bean.support.CenterPhotosData;
import com.happysanztech.mmm.utils.MobilizerValidator;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

/**
 * Created by Admin on 11-01-2018.
 */

public class CenterPhotosListAdapter extends BaseAdapter {

    //    private final Transformation transformation;
    private static final String TAG = TaskPictureListAdapter.class.getName();
    private final Transformation transformation;
    private Context context;
    private ArrayList<CenterPhotosData> centerPhotosData;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();
    private ImageLoader imageLoader = AppController.getInstance().getUniversalImageLoader();


    public CenterPhotosListAdapter(Context context, ArrayList<CenterPhotosData> centerPhotosData) {
        this.context = context;
        this.centerPhotosData = centerPhotosData;
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
            return centerPhotosData.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mSearching) {
            return centerPhotosData.get(mValidSearchIndices.get(position));
        } else {
            return centerPhotosData.get(position);
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
            convertView = inflater.inflate(R.layout.center_photos_list_item, parent, false);

            holder = new ViewHolder();
            holder.imageView =  convertView.findViewById(R.id.img_logo);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mSearching) {
            position = mValidSearchIndices.get(position);
        } else {
            Log.d("Event List Adapter", "getview pos called" + position);
        }

        CenterPhotosData centerPhotosDatas = centerPhotosData.get(position);

//        holder.txtId.setText(taskPictures.get(position).getId());

        if (MobilizerValidator.checkNullString(centerPhotosData.get(position).getCenterPhotos())) {
            Picasso.with(this.context).load(centerPhotosData.get(position).getCenterPhotos()).fit().transform(this.transformation).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_profile);
        }

        return convertView;
    }

    public void startSearch(String eventName) {
        mSearching = true;
        mAnimateSearch = false;
        Log.d("EventListAdapter", "serach for event" + eventName);
        mValidSearchIndices.clear();
        for (int i = 0; i < centerPhotosData.size(); i++) {
            String classStudent = centerPhotosData.get(i).getCenterPhotos();
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
        public ImageView imageView;
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
