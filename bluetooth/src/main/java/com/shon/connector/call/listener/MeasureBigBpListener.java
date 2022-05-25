package com.shon.connector.call.listener;

import java.util.List;

public interface MeasureBigBpListener {

    void measureStatus(int status,String measureDeviceTime);

    void measureBpResult(List<Integer> bpValue,String timeStr);
}
