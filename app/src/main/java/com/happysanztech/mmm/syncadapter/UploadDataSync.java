package com.happysanztech.mmm.syncadapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;

public class UploadDataSync extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static UploadDataSyncAdapter uploadDataSyncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("UploadDataSync", "onCreate");
        synchronized (sSyncAdapterLock) {
            if (uploadDataSyncAdapter == null) {
                uploadDataSyncAdapter = new UploadDataSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("MyServiceSync", "onBind");
        return uploadDataSyncAdapter.getSyncAdapterBinder();
    }
}
