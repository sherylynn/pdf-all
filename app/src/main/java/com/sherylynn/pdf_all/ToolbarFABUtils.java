package com.sherylynn.pdf_all;

import android.app.Activity;
import android.graphics.Color;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.iconics.IconicsColor;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.IconicsSize;
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial;

public class ToolbarFABUtils {

    public static void show(Activity activity, FloatingActionButton floatingActionButton, Toolbar toolbar){
        floatingActionButton.setImageDrawable(new IconicsDrawable(activity, GoogleMaterial.Icon.gmd_visibility_off)
                .color(IconicsColor.colorInt(Color.WHITE)).size(IconicsSize.dp(24)));
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
    }
    public static void hide(Activity activity,FloatingActionButton floatingActionButton, Toolbar toolbar){
        floatingActionButton.setImageDrawable(new IconicsDrawable(activity, GoogleMaterial.Icon.gmd_visibility)
                .color(IconicsColor.colorInt(Color.WHITE)).size(IconicsSize.dp(24)));
        toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
    }
}
