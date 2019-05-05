package com.sherylynn.pdf_all;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.UriUtils;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;


import java.io.IOException;
import com.alibaba.fastjson.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    private Uri uri;
    private String uriString;
    private static int LastPage =1 ;
    private static int CurrentPage =0 ;
    private String TAG="MainActivity";
    private static final int UPDATE_PAGES_TIME=29000;
    private UpdateTask updateTask=null;
    private String filePath;
    private String origin;
    private String username;
    private String password;
    private String fileName = "test.pdf";
    private PDFView pdfView;
    private String DocId=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.verifyStoragePermissions(this);
        Intent intent = getIntent();
        if (intent!=null && intent.ACTION_VIEW.equals(intent.getAction())){
            uri = intent.getData();
            uriString = Uri.decode(uri.getEncodedPath());
            //set Last pdf uri
            SPUtils.put(this,"LastPDFUriString",uri.toString());
            //LogUtils.d("test");
            fileName = uriString.substring(uriString.lastIndexOf("/")+1,uriString.length());
            Log.v("pdf-all-file", "有文件："+uriString);
            Log.v("pdf-all-file", "文件名："+fileName);
            setContentView(R.layout.activity_fullscreen);
            if(SPUtils.get(this,"url","blank")=="blank"){
                DialogUtils.signin(this);
            }
            origin=SPUtils.get(this,"url","black");
            username=SPUtils.get(this,"username","black");
            //for test
            syncPage();
            //loadPdf(1);
        }else {
            Log.v("pdf-all-file", "无文件或action不对应"+"test.pdf");
            setContentView(R.layout.activity_fullscreen);
            DocId=PDFUtils.DocId(this,fileName);
            Log.v("pdf-all-file", "默认文件ID："+DocId);
            String defaultLastPDFUriString = "self";
            // init false
            // because dialog is not immediate
            // init false will be aways false
            // so i configure it in dialog
            //SPUtils.putSync(activity,"reopenClick",false);
            String LastPDFUriString =SPUtils.get(this,"LastPDFUriString",defaultLastPDFUriString);
            if(LastPDFUriString!=defaultLastPDFUriString){
                DialogUtils.reopen(this);
            }

            //if not signed show sign in dialog
            if(SPUtils.get(this,"url","blank")=="blank"){
                DialogUtils.signin(this);
            }
            origin=SPUtils.get(this,"url","black");
            username=SPUtils.get(this,"username","black");
            if(SPUtils.get(this,"reopenClick",false)!=false){
                pdfView = (PDFView) findViewById(R.id.pdfView);
                pdfView
                        .fromUri(Uri.parse(LastPDFUriString))
                        .defaultPage(SPUtils.get(this,fileName,0))
                        .onPageChange(new OnPageChangeListener() {
                            @Override
                            public void onPageChanged(int page, int pageCount) {
                                LastPage=page;
                                SPUtils.put(getApplicationContext(),fileName,LastPage);
                            }
                        })
                        .load();
            }else{
                pdfView = (PDFView) findViewById(R.id.pdfView);
                pdfView
                        .fromAsset(fileName)
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
    private void syncPage(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                DocId=PDFUtils.DocId(UriUtils.uri2File(uri));
                Log.v("pdf-all-file", "文件ID："+DocId);
            }
        }).start();
        getLastPages(DocId);
        DialogUtils.create_test_dialog(this);
    }
    private void loadPdf(int lastPage){
        pdfView = (PDFView) findViewById(R.id.pdfView);
        pdfView
                .fromUri(uri)
                //.defaultPage(SPUtils.get(this,fileName,0))
                .defaultPage(lastPage)
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        CurrentPage=page;
                        SPUtils.put(getApplicationContext(),fileName,CurrentPage);
                    }
                })
                .load();
        //导致了闪退
        //startUpdateTask();
    }
    private void closeUpdateTask(){
        try {
            if(updateTask!=null) {
                updateTask.cancel(true);
                updateTask = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void startUpdateTask(){
        try {
            if (uriString != null && updateTask == null && LastPage!=-1) {
                updateTask = new UpdateTask();
                updateTask.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public class UpdateTask extends AsyncTask<String, Object, Integer> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Integer doInBackground(String... strings) {

            while (true){
                if(isCancelled()) return null;
                if(LastPage!=CurrentPage) {
                    LastPage=CurrentPage;
                    updatePages(LastPage);
                }
                try{
                    Thread.sleep(UPDATE_PAGES_TIME);
                    publishProgress();
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        @Override
        protected void onPostExecute(Integer result) {

        }
        private void updatePages(int pageNum){
            String url=origin + "/update_progress?username=" + username + "&identifier=" + DocId + "&page_num=" + pageNum;
            Log.d(TAG, "updatePages: url:"+url);
            HttpUtils.sendOkHttpRequest(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure: Filed");

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseText = response.body().string();
                    Log.d(TAG, "onResponse: "+responseText);

                }
            });
        }

    }
    private void getLastPages(String DocId){
        final String url=origin + "/get_latest_progress?username=" + username + "&identifier=" + DocId ;
        Log.d(TAG, "getLastPages: url:"+url);
        HttpUtils.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d(TAG, "onFailure get: Filed");
                CurrentPage=LastPage=0;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadPdf(LastPage);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                String page_num=null;
                try{
                    JSONObject jsonObject=JSONObject.parseObject(responseText);
                    page_num=jsonObject.getString("page_num");
                    LastPage=Integer.valueOf(page_num);
                    CurrentPage=LastPage;

                }catch (Exception e){
                    e.printStackTrace();
                }
                LogUtils.d("from server"+responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.d(LastPage);
                        loadPdf(LastPage);
                    }
                });
            }

        });
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        //#onDestroy don't listen on kill
        //#maybe should try services
        //SPUtils.put(this,fileName,LastPage);
    }
}
