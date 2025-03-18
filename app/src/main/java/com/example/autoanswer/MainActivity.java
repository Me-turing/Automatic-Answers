package com.example.autoanswer;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button startServiceButton;
    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startServiceButton = findViewById(R.id.startServiceButton);
        statusTextView = findViewById(R.id.statusTextView);

        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateServiceStatus();
    }

    private void updateServiceStatus() {
        boolean isServiceEnabled = AutoAnswerService.isServiceEnabled(this);
        if (isServiceEnabled) {
            statusTextView.setText("服务已启用，可以开始自动答题");
            statusTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            statusTextView.setText("服务未启用，请点击下方按钮开启无障碍服务");
            statusTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }
} 