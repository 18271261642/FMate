package com.example.xingliansdk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import static com.example.xingliansdk.utils.ExcelUtil.logPath;

public class WriteLogIntentService extends IntentService {
    private static final String TAG = "WriteLogIntentService";

    public WriteLogIntentService() {
        super("WriteLogIntentService");
    }

    protected void onHandleIntent(Intent intent) {
        BufferedWriter out = null;

        try {
            if (Environment.getExternalStorageState().equals("mounted")) {
                String msg = intent.getStringExtra("msg");
                String day = (new SimpleDateFormat("yyyyMMdd")).format(new Date());
                String fileName = day + ".txt";
                File f = new File(logPath);
                if (!f.exists()) {
                    f.mkdirs();
                }

                File file = new File(f, fileName);
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));
                String time = (new SimpleDateFormat("HH:mm:ss SSSS")).format(System.currentTimeMillis());
                out.write(time + ":" + msg + "\r\n");
                return;
            }

            Log.w("WriteLogIntentService", "sdcard unmounted,skip dump exception");
        } catch (Exception var18) {
            Log.e("WriteLogIntentService", var18.getMessage());
            return;
        } finally {
            try {
                out.close();
            } catch (Exception var17) {
                var17.printStackTrace();
            }

        }

    }

    public void onCreate() {
        super.onCreate();
        Log.d("WriteLogIntentService", "onCreate");
    }

    public void onDestroy() {
        Log.d("WriteLogIntentService", "onDestroy");
        super.onDestroy();
    }
}