package com.example.autoanswer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LogActivity extends AppCompatActivity {
    private TextView logTextView;
    private BroadcastReceiver logReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        logTextView = findViewById(R.id.logTextView);

        // 注册广播接收器
        logReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String log = intent.getStringExtra("log");
                if (log != null) {
                    updateLog(log);
                }
            }
        };
        registerReceiver(logReceiver, new IntentFilter("com.example.autoanswer.LOG_UPDATE"));
    }

    public void updateLog(String log) {
        logTextView.append(log + "\n");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消注册广播接收器
        unregisterReceiver(logReceiver);
    }
} 