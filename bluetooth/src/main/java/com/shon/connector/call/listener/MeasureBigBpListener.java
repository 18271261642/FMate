package com.shon.connector.call.listener;

import java.util.List;

public interface MeasureBigBpListener {

    void measureStatus(int status);

    void measureBpResult(List<Integer> bpValue);
}
