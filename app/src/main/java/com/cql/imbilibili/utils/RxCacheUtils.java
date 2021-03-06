package com.cql.imbilibili.utils;

import com.lh.cachelibrary.RxCache;
import com.lh.cachelibrary.convert.GsonDiskConverter;

/**
 * Created by home on 2017/1/24.
 * Cache单例
 */

public class RxCacheUtils {

    private static volatile RxCache mCache;

    public static RxCache getInstance(){
        if (mCache == null){
            synchronized (RxCacheUtils.class) {
                if (mCache == null) {
                    mCache = new RxCache.Builder()
                            .setDiskCachePath(StorageUtils.getAppCachePath() + "/rxcache")
                            .setDiskConverter(new GsonDiskConverter())
                            .build();
                }
            }
        }
        return mCache;
    }
}
