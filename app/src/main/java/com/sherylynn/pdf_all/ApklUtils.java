package com.sherylynn.pdf_all;

import android.app.Activity;
import android.graphics.Color;
import android.os.Environment;
import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.manager.DownloadManager;


public class ApklUtils {
    public static void DownloadApk(Activity activity,String ApkUrl){

        DownloadManager manager = DownloadManager.getInstance(activity);
        manager.setApkName("appupdate.apk")
                .setApkUrl(ApkUrl)
                .setDownloadPath(Environment.getExternalStorageDirectory() + "/AppUpdate")
                .setSmallIcon(R.mipmap.ic_launcher)
                .download();
    }
}
