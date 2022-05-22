package com.shon.connector.call.write.bigdataclass.ppg1;

import java.util.List;

public interface OnPPG1CacheRecordListener {

    void backPPGCacheByteArray(List<byte[]> timeList);

    void backPPGCacheLongArray(List<Long> longList);

    void backPPGCacheArray(List<byte[]> timeList,List<Long> longList);
}
