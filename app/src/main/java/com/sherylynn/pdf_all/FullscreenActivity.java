package com.sherylynn.pdf_all;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    private Uri uri;
    private int LastPage =1 ;
    private String TAG="MainActivity";
    private String filePath;
    private String fileName = "test";
    private PDFView pdfView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.verifyStoragePermissions(this);
        Intent intent = getIntent();
        if (intent!=null && intent.ACTION_VIEW.equals(intent.getAction())){
            uri = intent.getData();
            Log.v("pdf-all-file", "有文件"+Uri.decode(uri.getEncodedPath()));
            setContentView(R.layout.activity_fullscreen);
            pdfView = (PDFView) findViewById(R.id.pdfView);
            pdfView.fromUri(uri).load();
        }else {
            Log.v("pdf-all-file", "无文件或action不对应"+"test.pdf");
            setContentView(R.layout.activity_fullscreen);
            pdfView = (PDFView) findViewById(R.id.pdfView);
            pdfView
                    .fromAsset(fileName+".pdf")
                    .defaultPage(SPUtils.get(this,fileName,0))
                    .onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
                            LastPage=page;
                            SPUtils.put(getApplicationContext(),fileName,LastPage);
                        }
                    })
                    .load();//打开在assets文件夹里面的资源
        }
        Log.v("pdf-all-file", "最终："+"init完毕");
        // hide actionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        UpdateUtils.CheckUpdateGithub(this);
        //SPUtils.put(this,"test",1);
        //Log.v("SPUtils-test",SPUtils.get(this,"test",0)+"");
        //pdfView.fromAsset("test.pdf").load();//打开在assets文件夹里面的资源
        //pdfView.fromBytes().load();//本地打开
        //pdfView.fromFile(filePath).load();//网络下载打开，（）放字节数组
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        //#onDestroy don't listen on kill
        //#maybe should try services
        //SPUtils.put(this,fileName,LastPage);
    }
}
