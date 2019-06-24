package com.sherylynn.pdf_all;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.blankj.utilcode.util.LogUtils;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import java.io.IOException;
import com.alibaba.fastjson.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


//icon
import com.mikepenz.iconics.context.*;
import com.mikepenz.iconics.IconicsColor;
import com.mikepenz.iconics.IconicsSize;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial;

import com.google.android.material.floatingactionbutton.*;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {
    private Uri uri;
    private String uriString;
    private static int LastPage =1 ;
    private static int CurrentPage =0 ;
    private int PageCount;
    private String TAG="MainActivity";
    private static final int UPDATE_PAGES_TIME=9000;
    private UpdateTask updateTask=null;
    private String filePath;
    private String origin;
    private String username;
    private String password;
    private String fileName = "test.pdf";
    private String showName = "";
    private PDFView pdfView;


    //private static String DocId=null;
    private static String DocId=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.verifyStoragePermissions(this);
        Intent intent = getIntent();
        //直接打开
        if (intent!=null && intent.ACTION_VIEW.equals(intent.getAction())){
            uri = intent.getData();
            uriString = Uri.decode(uri.getEncodedPath());
            //set Last pdf uri
            SPUtils.put(this,"LastPDFUriString",uri.toString());
            //LogUtils.d("test");
            fileName = uriString.substring(uriString.lastIndexOf("/")+1,uriString.length());
            Log.v("pdf-all-file", "有文件："+uriString);
            Log.v("pdf-all-file", "文件名："+fileName);
            setContentView(R.layout.activity_main);
            if(SPUtils.get(this,"url","blank")=="blank"){
                DialogUtils.signin(this);
            }
            origin=SPUtils.get(this,"url","black");
            username=SPUtils.get(this,"username","black");
            //for test
            pdfView = (PDFView) findViewById(R.id.pdfView);
            pdfView
                    .fromUri(uri)
                    .defaultPage(SPUtils.get(this,fileName,0))
                    .onPageChange(new OnPageChangeListener() {
                        @Override
                        public void onPageChanged(int page, int pageCount) {
                            CurrentPage=page;
                            PageCount=pageCount;
                            //需要注意页面
                            //到底存currentpage还是lastpage
                            SPUtils.put(getApplicationContext(),fileName,LastPage);
                            //Toast.makeText(getApplicationContext(), page + " / " + pageCount, Toast.LENGTH_SHORT).show();
                            if(fileName.length()>=12){
                                showName = fileName.substring(0,12)+"... ";
                            }else{
                                showName = fileName;
                            }
                            setTitle(String.format("%s %s /%s",showName,page,pageCount));
                            //setSubtitle
                        }
                    })
                    .load();
            //pdfView.jumpTo(11);
            syncPage(this);
            //loadPdf(1);
        }else {
            Log.v("pdf-all-file", "无文件或action不对应"+"test.pdf");
            setContentView(R.layout.activity_main);
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
                                setTitle(String.format("%s %s /%s",fileName,page,pageCount));
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
                                //Toast.makeText(getApplicationContext(), page + " / " + pageCount, Toast.LENGTH_SHORT).show();

                                setTitle(String.format("%s %s /%s",fileName,page,pageCount));
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.toolbar_search_view);
        setSupportActionBar(toolbar);

        FloatingActionButton mainFab = (FloatingActionButton) findViewById(R.id.fab);
        mainFab.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_visibility)
                .color(IconicsColor.colorInt(Color.WHITE)).size(IconicsSize.dp(24)));

        //UpdateUtils.CheckUpdateGithub(this);
        UpdateUtils.CheckUpdateGithubBackground(this);
        //SPUtils.put(this,"test",1);
        //Log.v("SPUtils-test",SPUtils.get(this,"test",0)+"");
        //pdfView.fromAsset("test.pdf").load();//打开在assets文件夹里面的资源
        //pdfView.fromBytes().load();//本地打开
        //pdfView.fromFile(filePath).load();//网络下载打开，（）放字节数组
    }
    private void syncPage(Activity activity){
        new Thread(new Runnable(){
            @Override
            public void run(){
                //DocId=PDFUtils.DocId(UriUtils.uri2File(uri));
                //DocId=PDFUtils.GetDocID(uri,activity);
                //DocId=PDFUtils.DocId(uri,activity);
                DocId=PDFUtils.DocId(fileName);
                //File Uri2File = UriUtils.uri2File(uri);
                //Log.v("file-test","fuck"+Uri2File.getPath());
                //Log.v("上一层获取的uri是否有误？","fuck"+);
                Log.v("上一层获取的uri是否有误？","fuckAll"+uri.toString());
                //Log.v("uri无误，uriutils是否有错？","fuckAgain"+UriUtils.uri2File(uri).getPath());
                Log.v("pdf-all-file", "文件ID："+DocId);
                getLastPages(DocId);
            }
        }).start();
        //这里有一个阻塞，可能影响加载速度，因为androidpdf插件的加载是开头就指定了defaultpage的，没法后期调用
        //getLastPages(DocId);
        DialogUtils.create_test_dialog(this);
    }
    private void loadPdf(int lastPage){
        pdfView = (PDFView) findViewById(R.id.pdfView);
        pdfView.jumpTo(lastPage,true);
        startUpdateTask();
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
                LogUtils.v("执行了推送");
                //Toast.makeText(this,"开始推送",Toast.LENGTH_SHORT).show();
                if(LastPage!=CurrentPage && CurrentPage !=0) { // 这里lastpage可以是0.意思是服务器上进度为0
                    LastPage=CurrentPage;
                    //本地记录
                    SPUtils.put(getApplicationContext(),fileName,LastPage);
                    //推送进度
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
            LogUtils.v("推送地址"+url);
            LogUtils.v("推送id"+DocId);
            HttpUtils.sendOkHttpRequest(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtils.v("推送进度失败"+e);

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseText = response.body().string();
                    LogUtils.v("推送进度回馈"+responseText);
                }
            });
        }

    }
    private void getLastPages(String DocId){
        LogUtils.v("getLastPages中使用的文件ID"+DocId);
        final String url=origin + "/get_latest_progress?username=" + username + "&identifier=" + DocId ;
        Log.d(TAG, "getLastPages: url:"+url);
        HttpUtils.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                LogUtils.v("文件同步错误："+e);
                /*同步出错就不跳转进度了
                CurrentPage=LastPage=0;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadPdf(LastPage);
                    }
                });
                */
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
                LogUtils.v("from server"+responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.v(LastPage);
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
    private SearchView mSearchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search_view, menu);
        initSearch(menu);
        return true;
    }

    /**
     * 初始化搜索框
     * @param menu
     */
    private void initSearch(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.menu_search_view);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                int inputPage = Integer.parseInt(s);
                if(inputPage<=PageCount){
                    loadPdf(inputPage);
                }
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });
    }

    /*
    //FAB
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }
    */

}