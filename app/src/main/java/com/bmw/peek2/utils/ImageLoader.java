package com.bmw.peek2.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by admin on 2017/5/19.
 */

public class ImageLoader {

    private LruCache<String,Bitmap> mLruCache;

    public ImageLoader() {
        int maxSize = (int) (Runtime.getRuntime().maxMemory()/1024);
        int cacheSize = maxSize/4;
        mLruCache = new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes()*value.getHeight()/1024;
            }
        };
    }

    public synchronized void putBitmap(Bitmap bitmap,String key){
        if(key == null || bitmap == null)
            return;
        if(mLruCache.get(key) == null){
            mLruCache.put(key,bitmap);
        }
    }

    public synchronized Bitmap getBitmap(String key){
        if(key == null)
            return null;
        return mLruCache.get(key);
    }

    public synchronized void removeBitmapCache(String key){
        if(key == null)
            return;
        if(mLruCache != null){
            Bitmap bm = mLruCache.remove(key);
            if(bm!=null)
                bm.recycle();
        }
    }

    public void clearBitmapCache(){
        if(mLruCache == null || mLruCache.size() ==  0){
            return;
        }
        mLruCache.evictAll();
        mLruCache = null;
    }
}
