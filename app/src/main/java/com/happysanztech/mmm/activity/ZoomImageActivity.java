package com.happysanztech.mmm.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.happysanztech.mmm.R;
import com.happysanztech.mmm.bean.support.CenterPhotosData;
import com.happysanztech.mmm.bean.support.TaskPicture;
import com.happysanztech.mmm.utils.MobilizerValidator;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by Admin on 18-01-2018.
 */

public class ZoomImageActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivBack, ivZoomImage;
    private CenterPhotosData centerPhotosData;
    private TaskPicture taskPicture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);
        ivBack = findViewById(R.id.back_tic_his);
        ivBack.setOnClickListener(this);
        ivZoomImage = findViewById(R.id.zoom_image);

        String page = "";
        page = getIntent().getStringExtra("page");
        if (page.equalsIgnoreCase("task")) {
            taskPicture = (TaskPicture) getIntent().getSerializableExtra("eventObj");
            if (MobilizerValidator.checkNullString(taskPicture.getTaskImage())) {
                Picasso.get().load(taskPicture.getTaskImage()).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(ivZoomImage);
            } else {
                ivZoomImage.setImageResource(R.drawable.ic_profile);
            }
        } else {
            centerPhotosData = (CenterPhotosData) getIntent().getSerializableExtra("eventObj");
            if (MobilizerValidator.checkNullString(centerPhotosData.getCenterPhotos())) {
                Picasso.get().load(centerPhotosData.getCenterPhotos()).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(ivZoomImage);
            } else {
                ivZoomImage.setImageResource(R.drawable.ic_profile);
            }
        }

    }

    @Override
    public void onClick(View v) {
        if (v == ivBack) {
            finish();
        }
    }
}
