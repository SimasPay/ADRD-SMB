package com.dimo.PayByQR.QrStore.utility;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import java.io.File;

public class FileCache {
    
    private File cacheDir;
  
    public FileCache(Context context){
        //Find the dir to save cached images
        cacheDir = new File(ContextCompat.getExternalFilesDirs(context, null)[0].getPath(), "TempImages/");
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
  
    public File getFile(String url){
        String filename=String.valueOf(url.hashCode());
        File f = new File(cacheDir, filename);
        return f;
  
    }
  
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }
  
}