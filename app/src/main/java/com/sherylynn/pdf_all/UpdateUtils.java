package com.sherylynn.pdf_all;
import android.app.Activity;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;

public class UpdateUtils {
  //
  public static void CheckUpdateGithub(Activity activity){
    new AppUpdater(activity)
            //.setDisplay(Display.NOTIFICATION)
            //.setDisplay(Display.SNACKBAR)
            .setDisplay(Display.DIALOG)
      .setUpdateFrom(UpdateFrom.GITHUB)
      .setGitHubUserAndRepo("sherylynn","pdf-all")
      .start();
  }
  public static void CheckUpdateGithubBackground(Activity activity){
      String user="sherylynn";
      String repo="pdf-all";
    AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(activity)
            .setUpdateFrom(UpdateFrom.GITHUB)
            .setGitHubUserAndRepo(user,repo)
            .withListener(new AppUpdaterUtils.UpdateListener() {
              @Override
              public void onSuccess(Update update, Boolean isUpdateAvailable) {
                  String ApkUrl="https://github.com/"+user+"/"+repo+"/releases/download/v0.0.3.1/app-release.apk";
                  LogUtils.v("最新版本"+update.getLatestVersion());
                  LogUtils.v("最新版本地址"+update.getUrlToDownload());
                  LogUtils.v("最新版本下载地址"+ApkUrl);
                  ApklUtils.DownloadApk(activity,ApkUrl);
                Log.v("Lastest Version",update.getLatestVersion());
                //Log.v("Release notes", update.getReleaseNotes()); //github no this method
                //Log.v("URL", update.getUrlToDownload()+"");

                Log.v("Is update available?", Boolean.toString(isUpdateAvailable));
              }

              @Override
              public void onFailed(AppUpdaterError error) {
                  Log.v("Lastest Version","Something went wrong");
              }
            });
    appUpdaterUtils.start();
  }
}
