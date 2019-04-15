package com.sherylynn.pdf_all;

import android.content.Context;
import android.content.res.AssetManager;


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
            while (stream.read(bs)!=-1){
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
            while (stream.read(bs)!=-1){
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
            String string_context = new String(bytes,"UTF-8");
            String string_pattern = "FlateDecode/ID\\[<([0-9A-Z]+)";

            Pattern pattern =Pattern.compile(string_pattern);

            Matcher m = pattern.matcher(string_context);
            if (m.find()){
                res =m.group(1);
            }
            //res="find中强行设置"+string_context;
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
