package com.sherylynn.pdf_all;

import android.app.Activity;
import android.graphics.Color;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.iconics.IconicsColor;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.IconicsSize;
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial;

public class ToolbarFABUtils {

    public static void show(Activity activity, FloatingActionButton floatingActionButton, Toolbar toolbar){
        visibilityOffFAB(activity,floatingActionButton);
        showToolbar(toolbar);
    }
    public static void show(Activity activity, FloatingActionButton floatingActionButton, ActionBar actionBar){
        visibilityOffFAB(activity,floatingActionButton);
        showActionBar(actionBar);
    }

    public static void hide(Activity activity,FloatingActionButton floatingActionButton, Toolbar toolbar){
        visibilityFAB(activity,floatingActionButton);
        hideToolbar(toolbar);
    }
    public static void hide(Activity activity, FloatingActionButton floatingActionButton, ActionBar actionBar){
        visibilityFAB(activity,floatingActionButton);
        hideActionBar(actionBar);
    }

    public static void showToolbar(Toolbar toolbar){
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
    }
    public static void hideToolbar(Toolbar toolbar){
        toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
    }
    public static void hideActionBar(ActionBar actionBar){
        if (actionBar != null) {
            actionBar.hide();
        }
    }
    public static void showActionBar(ActionBar actionBar){
        if (actionBar != null) {
            actionBar.show();
        }
    }
    public static void visibilityOffFAB(Activity activity,FloatingActionButton floatingActionButton){
        floatingActionButton.setImageDrawable(new IconicsDrawable(activity, GoogleMaterial.Icon.gmd_visibility_off)
                .color(IconicsColor.colorInt(Color.WHITE)).size(IconicsSize.dp(24)));
    }
    public static void visibilityFAB(Activity activity,FloatingActionButton floatingActionButton){
        floatingActionButton.setImageDrawable(new IconicsDrawable(activity, GoogleMaterial.Icon.gmd_visibility)
                .color(IconicsColor.colorInt(Color.WHITE)).size(IconicsSize.dp(24)));
    }
}
