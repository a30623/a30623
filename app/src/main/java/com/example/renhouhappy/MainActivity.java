package com.example.renhouhappy;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private Vibrator vibrator;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean running = false;
    private int level = 2;
    private TextView status;
    private Button start;

    private final Runnable pulse = new Runnable() {
        @Override public void run() {
            if (!running) return;
            doOnePulse();
            handler.postDelayed(this, level == 1 ? 260 : level == 2 ? 210 : 170);
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vibrator = getVibratorCompat();
        setContentView(buildUi());
    }

    private Vibrator getVibratorCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vm = (VibratorManager) getSystemService(VIBRATOR_MANAGER_SERVICE);
            if (vm != null) return vm.getDefaultVibrator();
        }
        return (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    private View buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(34), dp(24), dp(24));
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(Color.WHITE);

        TextView title = new TextView(this);
        title.setText("人猴快乐器"); title.setTextSize(30); title.setTypeface(Typeface.DEFAULT_BOLD); title.setGravity(Gravity.CENTER);
        root.addView(title, new LinearLayout.LayoutParams(-1,-2));

        status = new TextView(this);
        status.setText("兼容模式 · 已选择中档"); status.setTextSize(16); status.setGravity(Gravity.CENTER); status.setPadding(0,dp(16),0,dp(24));
        root.addView(status, new LinearLayout.LayoutParams(-1,-2));

        LinearLayout row = new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL);
        String[] names = {"轻","中","强"};
        for (int i=0;i<3;i++) {
            final int l=i+1;
            Button b = new Button(this); b.setText(names[i]); b.setAllCaps(false);
            b.setOnClickListener(v->{ level=l; status.setText("兼容模式 · 已选择"+(l==1?"轻档":l==2?"中档":"强档")); if(running){ handler.removeCallbacks(pulse); pulse.run(); }});
            row.addView(b,new LinearLayout.LayoutParams(0,dp(58),1));
        }
        root.addView(row,new LinearLayout.LayoutParams(-1,-2));

        Button test = new Button(this); test.setText("测试马达（1秒）"); test.setAllCaps(false); test.setTextSize(18);
        test.setOnClickListener(v -> testMotor());
        LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(-1,dp(64)); tp.topMargin=dp(24); root.addView(test,tp);

        start = new Button(this); start.setText("开始持续振动"); start.setAllCaps(false); start.setTextSize(20);
        start.setOnClickListener(v->{ if(running) stop(); else begin(); });
        LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,dp(72)); sp.topMargin=dp(14); root.addView(start,sp);

        TextView note = new TextView(this);
        note.setText("如果“测试马达”也完全不震，说明当前系统/安卓兼容层没有把 Android Vibrator 调用转发给马达。\n版本 1.1 兼容模式");
        note.setTextSize(13); note.setGravity(Gravity.CENTER); note.setPadding(dp(8),dp(18),dp(8),0);
        root.addView(note,new LinearLayout.LayoutParams(-1,-2));
        return root;
    }

    private void testMotor() {
        if (vibrator == null || !vibrator.hasVibrator()) { status.setText("系统报告：未检测到振动器"); return; }
        vibrator.cancel();
        try {
            if (Build.VERSION.SDK_INT >= 33) {
                VibrationEffect e = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE);
                VibrationAttributes a = new VibrationAttributes.Builder().setUsage(VibrationAttributes.USAGE_ALARM).build();
                vibrator.vibrate(e, a);
            } else {
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
            }
            status.setText("已发送 1 秒马达测试指令");
        } catch (Throwable t) {
            try { vibrator.vibrate(1000); status.setText("已改用传统振动接口测试"); }
            catch (Throwable e) { status.setText("振动接口调用失败：" + e.getClass().getSimpleName()); }
        }
    }

    private void begin(){
        if(vibrator==null || !vibrator.hasVibrator()){status.setText("系统报告：未检测到振动器");return;}
        running=true; start.setText("停止振动"); handler.removeCallbacks(pulse); pulse.run();
    }

    private void doOnePulse(){
        long duration = level==1?90:level==2?130:170;
        int amplitude = level==1?80:level==2?170:255;
        try {
            if (Build.VERSION.SDK_INT >= 33) {
                VibrationEffect e=VibrationEffect.createOneShot(duration, vibrator.hasAmplitudeControl()?amplitude:VibrationEffect.DEFAULT_AMPLITUDE);
                VibrationAttributes a=new VibrationAttributes.Builder().setUsage(VibrationAttributes.USAGE_ALARM).build();
                vibrator.vibrate(e,a);
            } else {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, vibrator.hasAmplitudeControl()?amplitude:VibrationEffect.DEFAULT_AMPLITUDE));
            }
        } catch(Throwable t){ try{vibrator.vibrate(duration);}catch(Throwable ignored){} }
    }

    private void stop(){ running=false; handler.removeCallbacks(pulse); if(vibrator!=null)vibrator.cancel(); start.setText("开始持续振动"); }
    @Override protected void onPause(){ super.onPause(); stop(); }
    @Override protected void onDestroy(){ stop(); super.onDestroy(); }
    private int dp(int v){ return Math.round(v*getResources().getDisplayMetrics().density); }
}
