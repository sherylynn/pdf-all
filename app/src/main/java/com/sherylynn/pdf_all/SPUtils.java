package com.sherylynn.pdf_all;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SPUtils {
    //# activity is from content
    //public static void put(Activity activity,String name ,int value){
    public static void put(Context context,String name,int value){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = sharedPref.edit();
        editor.putInt(name,value);
        //# commit is sync method when apply is async method
        //editor.commit();
        editor.apply();
    }
    public static int get(Context context,String name,int defaultValue){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int value = sharedPref.getInt(name,defaultValue);
        return value;
    }
}
