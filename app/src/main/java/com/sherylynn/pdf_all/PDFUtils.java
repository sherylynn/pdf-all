package com.sherylynn.pdf_all;

import android.content.Context;
import android.content.res.AssetManager;


import com.blankj.utilcode.util.LogUtils;

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
                res =m2.group(1);
                //LogUtils.v("not find 文件ID");
            }
            //res="find中强行设置"+string_context;
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
