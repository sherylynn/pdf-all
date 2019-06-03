package com.sherylynn.pdf_all;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;


import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.UriUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PDFUtils {
    public static String DocId(Context context,String fileName){
        String res=null;
        AssetManager assetManager = context.getAssets();
        try{
            InputStream stream = assetManager.open(fileName);
            byte[] bs=new byte[1024];
            while (stream.read(bs) > 0){
                res=find(bs);
                if(res!=null)return res;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
    public static String GetDocID(Uri uri,Context context){
        if (uri==null){
           return null;
        }
        switch (uri.getScheme()){
            case "content":
                return DocId(URIUtils.Uri2File(uri,context));
            case "file":
                return DocId(UriUtils.uri2File(uri));
            default:
                return null;
        }
    }
    public static String DocId(Uri uri,Context context){
        String res = null;
        InputStream UriInputStream;
        if (uri.getAuthority() != null) {
            try {
                UriInputStream = context.getContentResolver().openInputStream(uri);
                byte[] bs=new byte[1024];
                while (UriInputStream.read(bs)>0){
                    res=find(bs);
                    if(res!=null)return res;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return res;
    }
    //android 7 don't support file://
    public static String DocId(File pdfFile){
        String res=null;
        try{
            FileInputStream stream=new FileInputStream(pdfFile);
            byte[] bs=new byte[1024];
            while (stream.read(bs)>0){
                res=find(bs);
                if(res!=null)return res;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
    private static String find(byte bytes[]){
        String res=null;
        try {
            String string_context = new String(bytes,"ASCII");
            //String string_pattern = "DocumentID>uuid:(\\w{8}(-\\w{4}){3}-\\w{12}?)<";
            String string_pattern1 = "R/ID\\[<([0-9]+)";
            String string_pattern2 = "FlateDecode/ID\\[<([0-9A-Z]+)";
            //String string_pattern2 = "FlateDecode/ID\\[<([0-9A-Z]+)><([0-9A-Z]+)";

            Pattern pattern1 =Pattern.compile(string_pattern1);
            Pattern pattern2 =Pattern.compile(string_pattern2);

            Matcher m1 = pattern1.matcher(string_context);
            Matcher m2 = pattern2.matcher(string_context);
            //LogUtils.v("待查文件ID"+string_context);
            if (m1.find()){
                LogUtils.v("find后文件ID"+m1.group(1));
                res =m1.group(1);
            }else if(m2.find()){
                LogUtils.v("find后文件ID"+m2.group(1));
                //LogUtils.v("find后文件ID"+m2.group(1)+m2.group(2));
                res =m2.group(1);
                //res =m2.group(1)+m2.group(2);
                //LogUtils.v("not find 文件ID");
            }
            //res="find中强行设置"+string_context;
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
