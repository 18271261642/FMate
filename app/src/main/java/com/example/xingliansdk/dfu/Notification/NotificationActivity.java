//package com.example.xingliansdk.dfu.Notification;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//
//import com.example.xingliansdk.R;
//import com.shon.connector.utils.TLog;
//
//public class NotificationActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        TLog.Companion.error("NotificationActivity onCreate");
//        if (isTaskRoot()) {
//            // Start the app before finishing
////            final Intent intent = new Intent(this, UpdateZipActivity.class);
////            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////            intent.putExtras(getIntent().getExtras()); // copy all extras
////            startActivity(intent);
//        }
//
//        // Now finish, which will drop you to the activity at which you were at the top of the task stack
//        finish();
//    }
//}