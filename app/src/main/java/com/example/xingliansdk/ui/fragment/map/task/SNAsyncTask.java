package com.example.xingliansdk.ui.fragment.map.task;

/**
 * 功能:异步任务  不堵塞UI
 */

public class SNAsyncTask {

    public static void execute(SNVTaskCallBack vtask) {
        if (vtask == null) return;
        vtask.exec();
    }

}
