package com.xicp.cjlhappiness.clock;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import db.AlarmCRUD;
import db.AlarmDataBaseHelper;
import tool.AlarmListData;
import tool.AlarmTool;
import tool.mPoint;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener{

    private AlarmDataBaseHelper helper;
    private AlarmListData data;
    private RelativeLayout relativeLayout;
    private TextView targetTime, nextTimeSurplus, repeatText, vibrateText, ringText, message;
    private Vibrator vibrator;
    private MediaPlayer player;
    private PowerManager.WakeLock wl;
    private mPoint[] point;
    private int width, height;
    private int pointCount;
    private static final int RADIUS = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity);

        relativeLayout = (RelativeLayout) findViewById(R.id.alarm_layout);

        targetTime = (TextView) findViewById(R.id.show_target_time);
        nextTimeSurplus = (TextView) findViewById(R.id.show_next_time_surplus);
        repeatText = (TextView) findViewById(R.id.show_repeat);
        vibrateText = (TextView) findViewById(R.id.show_vibrate);
        ringText = (TextView) findViewById(R.id.show_ring);
        message = (TextView) findViewById(R.id.show_message);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        player = MediaPlayer.create(this, R.raw.music);

        KeyguardManager km= (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);//解锁屏幕
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        kl.disableKeyguard();

        //保持屏幕唤醒
        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");
        wl.acquire();

        //获得屏幕宽高像素值
        WindowManager manager = getWindowManager();
        width = manager.getDefaultDisplay().getWidth();
        height = manager.getDefaultDisplay().getHeight();

        createAndAddPoint();//产生添加点

        initAlarmSql();//初始化数据库

        //获得触发闹钟的数据对象
        Intent intent = getIntent();
        int id = intent.getIntExtra("dataId", -1);
        data = AlarmCRUD.queryOneAlarm(helper, id);

        //判断该闹钟是否单次并相应显示文字
        if (AlarmTool.getNextDay(data.getRepeat(), 0) == 0){
            AlarmCRUD.updateAlarm(helper, id, AlarmTool.ALARM_OPEN_CLOSE[0]);
            nextTimeSurplus.setText("下一次：" + "已关闭不开启提醒");
        }else {
            AlarmTool.startAlarm(this, helper, id);
            nextTimeSurplus.setText("下一次：" + AlarmTool.getSurplusTimeText(data.getTime(), data.getRepeat()));
        }

        //显示该闹钟各项设定文字
        targetTime.setText(AlarmTool.getTargetTimeText(data.getTime()));
        repeatText.setText("重复周期：" + AlarmTool.repeatNumChangeToString(data.getRepeat()));
        vibrateText.setText("震动：" + (data.getVibrate() == 1 ? "开启" : "关闭"));
        ringText.setText("铃声：" + (data.getVibrate() == 1 ? "开启" : "关闭"));

        message.setText("点击频幕上的点以关闭闹钟，剩余：" + pointCount + "个");

        //判断是否开启震动
        if (data.getVibrate() == 1){
            vibrator.vibrate(new long[]{1000, 1000, 1000}, 1);
        }

        //判断是否开启铃声
        if (data.getRing() ==1){
            player.start();
        }
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {//播放完毕重新开始
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
            }
        });

    }

    //初始化数据库
    private void initAlarmSql() {
        helper = new AlarmDataBaseHelper(this , "Alarm.db" , null , 1);
        helper.getWritableDatabase();
    }

    //创建添加点
    private void createAndAddPoint(){
        pointCount = (int)(Math.random()* 9) + 1;//随机产生1-10个点
        point = new mPoint[pointCount];
        for (int i = 0 ; i < pointCount ; i++){//通过循环创建点并随机产生该点的位置
            int x = (int)(Math.random() * (width - RADIUS * 4) + RADIUS * 2);//保证产生的点会完全显示在屏幕当中
            int y = (int)(Math.random() * (height - RADIUS  * 4) + RADIUS * 2);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RADIUS * 2, RADIUS * 2);
            params.setMargins(x - RADIUS , y - RADIUS , x + RADIUS, y + RADIUS);
            point[i] = new mPoint(this, RADIUS, RADIUS, RADIUS);
            point[i].setOnClickListener(this);
            relativeLayout.addView(point[i], params);
        }
    }

    //程序决数释放资源
    @Override
    protected void onDestroy() {
        super.onDestroy();
        vibrator.cancel();
        if (data.getRing() ==1){
            player.stop();
        }
        player.release();
        wl.release();
    }

    //屏蔽所有按键的响应功能
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
//        if (keyCode == KeyEvent.KEYCODE_BACK){
//            return true;
//        }else {
//            return super.onKeyDown(keyCode, event);
//        }
    }

    //点的点击事件回调方法
    @Override
    public void onClick(View v) {
        v.setVisibility(View.GONE);
        pointCount --;
        message.setText("点击频幕上的点以关闭闹钟，剩余：" + pointCount + "个");
        if (pointCount == 0){
            finish();
        }
    }
}
