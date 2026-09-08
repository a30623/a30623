package com.example.renhouhappy;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

public class MainActivity extends Activity {
    private Vibrator vibrator;
    private int selectedLevel = 2;
    private boolean running = false;
    private Button lowButton, mediumButton, highButton, startStopButton;
    private TextView statusText, capabilityText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vibrator = getPhoneVibrator();
        setContentView(buildUi());
        updateLevelButtons();
        updateStatus();

        boolean hasVibrator = vibrator != null && vibrator.hasVibrator();
        boolean amplitude = hasVibrator && vibrator.hasAmplitudeControl();
        if (!hasVibrator) {
            capabilityText.setText("当前设备未检测到可用振动器");
            startStopButton.setEnabled(false);
        } else if (amplitude) {
            capabilityText.setText("设备支持振幅控制：轻 / 中 / 强使用不同振幅");
        } else {
            capabilityText.setText("设备不支持振幅控制：将用不同振动节奏模拟三档");
        }
    }

    private Vibrator getPhoneVibrator() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager manager = (VibratorManager) getSystemService(VIBRATOR_MANAGER_SERVICE);
            return manager == null ? null : manager.getDefaultVibrator();
        }
        return (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    private View buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(34), dp(24), dp(28));
        root.setBackgroundColor(Color.WHITE);
        root.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = new TextView(this);
        title.setText("人猴快乐器");
        title.setTextSize(30);
        title.setTextColor(Color.rgb(20, 20, 20));
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title, matchWrap());

        TextView subtitle = new TextView(this);
        subtitle.setText("选择强度，然后启动振动");
        subtitle.setTextSize(15);
        subtitle.setTextColor(Color.rgb(105, 105, 105));
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subtitleLp = matchWrap();
        subtitleLp.topMargin = dp(8);
        root.addView(subtitle, subtitleLp);

        Space s1 = new Space(this);
        root.addView(s1, new LinearLayout.LayoutParams(1, dp(34)));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        root.addView(row, matchWrap());

        lowButton = makeLevelButton("轻", 1);
        mediumButton = makeLevelButton("中", 2);
        highButton = makeLevelButton("强", 3);
        row.addView(lowButton, weightedButton());
        row.addView(makeGap(dp(10)));
        row.addView(mediumButton, weightedButton());
        row.addView(makeGap(dp(10)));
        row.addView(highButton, weightedButton());

        Space s2 = new Space(this);
        root.addView(s2, new LinearLayout.LayoutParams(1, dp(38)));

        statusText = new TextView(this);
        statusText.setTextSize(18);
        statusText.setTypeface(Typeface.DEFAULT_BOLD);
        statusText.setTextColor(Color.rgb(35, 35, 35));
        statusText.setGravity(Gravity.CENTER);
        root.addView(statusText, matchWrap());

        startStopButton = new Button(this);
        startStopButton.setAllCaps(false);
        startStopButton.setTextSize(20);
        startStopButton.setTypeface(Typeface.DEFAULT_BOLD);
        startStopButton.setMinHeight(dp(64));
        startStopButton.setOnClickListener(v -> {
            if (running) stopVibration(); else startVibration();
        });
        LinearLayout.LayoutParams startLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(72));
        startLp.topMargin = dp(18);
        root.addView(startStopButton, startLp);

        capabilityText = new TextView(this);
        capabilityText.setTextSize(13);
        capabilityText.setTextColor(Color.rgb(120, 120, 120));
        capabilityText.setGravity(Gravity.CENTER);
        capabilityText.setPadding(dp(10), dp(14), dp(10), 0);
        root.addView(capabilityText, matchWrap());

        TextView tip = new TextView(this);
        tip.setText("提示：持续振动会耗电并可能使手机发热，不建议长时间连续开启。");
        tip.setTextSize(12);
        tip.setTextColor(Color.rgb(145, 145, 145));
        tip.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tipLp = matchWrap();
        tipLp.topMargin = dp(18);
        root.addView(tip, tipLp);
        return root;
    }

    private Button makeLevelButton(String text, int level) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextSize(18);
        b.setMinHeight(dp(58));
        b.setOnClickListener(v -> {
            selectedLevel = level;
            updateLevelButtons();
            if (running) startVibration(); else updateStatus();
        });
        return b;
    }

    private void updateLevelButtons() {
        styleLevelButton(lowButton, selectedLevel == 1);
        styleLevelButton(mediumButton, selectedLevel == 2);
        styleLevelButton(highButton, selectedLevel == 3);
    }

    private void styleLevelButton(Button b, boolean selected) {
        if (selected) {
            b.setTextColor(Color.WHITE);
            b.setBackgroundColor(Color.rgb(30, 30, 30));
        } else {
            b.setTextColor(Color.rgb(40, 40, 40));
            b.setBackgroundColor(Color.rgb(235, 235, 235));
        }
    }

    private void updateStatus() {
        if (statusText == null || startStopButton == null) return;
        String level = selectedLevel == 1 ? "轻档" : selectedLevel == 2 ? "中档" : "强档";
        if (running) {
            statusText.setText("正在振动 · " + level);
            startStopButton.setText("停止");
            startStopButton.setTextColor(Color.WHITE);
            startStopButton.setBackgroundColor(Color.rgb(180, 45, 45));
        } else {
            statusText.setText("已选择 · " + level);
            startStopButton.setText("开始振动");
            startStopButton.setTextColor(Color.WHITE);
            startStopButton.setBackgroundColor(Color.rgb(25, 25, 25));
        }
    }

    private void startVibration() {
        if (vibrator == null || !vibrator.hasVibrator()) return;
        vibrator.cancel();
        if (vibrator.hasAmplitudeControl()) {
            int amp = selectedLevel == 1 ? 70 : selectedLevel == 2 ? 150 : 255;
            long[] timings = {0, 400, 30};
            int[] amplitudes = {0, amp, 0};
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, 1));
        } else {
            long[] pattern = selectedLevel == 1 ? new long[]{0,80,180} : selectedLevel == 2 ? new long[]{0,180,90} : new long[]{0,450,35};
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 1));
        }
        running = true;
        updateStatus();
    }

    private void stopVibration() {
        if (vibrator != null) vibrator.cancel();
        running = false;
        updateStatus();
    }

    @Override protected void onPause() { super.onPause(); stopVibration(); }
    @Override protected void onDestroy() { stopVibration(); super.onDestroy(); }

    private View makeGap(int width) { Space s = new Space(this); s.setLayoutParams(new LinearLayout.LayoutParams(width, 1)); return s; }
    private LinearLayout.LayoutParams weightedButton() { return new LinearLayout.LayoutParams(0, dp(60), 1f); }
    private LinearLayout.LayoutParams matchWrap() { return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
