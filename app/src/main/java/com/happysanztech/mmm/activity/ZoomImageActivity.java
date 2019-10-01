package com.happysanztech.mmm.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.happysanztech.mmm.R;
import com.happysanztech.mmm.bean.support.CenterPhotosData;
import com.happysanztech.mmm.utils.MobilizerValidator;
import com.squareup.picasso.Picasso;

/**
 * Created by Admin on 18-01-2018.
 */

public class ZoomImageActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivBack, ivZoomImage;
    private CenterPhotosData centerPhotosData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);
        centerPhotosData = (CenterPhotosData) getIntent().getSerializableExtra("eventObj");
        ivBack = findViewById(R.id.back_tic_his);
        ivBack.setOnClickListener(this);
        ivZoomImage = findViewById(R.id.zoom_image);

        if (MobilizerValidator.checkNullString(centerPhotosData.getCenterPhotos())) {
            Picasso.get().load(centerPhotosData.getCenterPhotos()).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(ivZoomImage);
        } else {
            ivZoomImage.setImageResource(R.drawable.ic_profile);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == ivBack) {
            finish();
        }
    }
}
