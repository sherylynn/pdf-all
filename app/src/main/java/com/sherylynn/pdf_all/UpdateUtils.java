package com.sherylynn.pdf_all;
import android.app.Activity;
import android.util.Log;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;

public class UpdateUtils {
  //public static
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
    AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(activity)
            .setUpdateFrom(UpdateFrom.GITHUB)
            .setGitHubUserAndRepo("sherylynn","pdf-all")
            .withListener(new AppUpdaterUtils.UpdateListener() {
              @Override
              public void onSuccess(Update update, Boolean isUpdateAvailable) {
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
