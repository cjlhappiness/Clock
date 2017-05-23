package com.example.cjlhappiness.clock;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import db.AlarmDataBaseHelper;
import tool.AlarmTool;

public class AlarmServer extends Service {

    private boolean unKill;//如果为false，说明server进程被kill

    private AlarmDataBaseHelper helper;

    public static final String[] OBJECT = new String[]{"Activity", "Broadcast"};

    public AlarmServer() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("test----", "onStartCommand: ");
        String object = intent.getExtras().getString("OBJECT");
        if (object == OBJECT[0]){
            int resultCode = intent.getExtras().getInt("CODE");
            int id = intent.getExtras().getInt("id");
            if (resultCode == AlarmTool.ALARM_CLOSE_CODE[4]){//修改
                AlarmTool.stopAlarm(this, id);
                AlarmTool.startAlarm(this, helper, id);
            }else if (resultCode == AlarmTool.ALARM_CLOSE_CODE[3]){//删除
                AlarmTool.stopAlarm(this, id);
            }else if (resultCode == AlarmTool.ALARM_CLOSE_CODE[2]){//添加
                AlarmTool.startAlarm(this, helper, id);
            }else if (object == OBJECT[0] && !unKill){//AlarmTool.ALARM_CLOSE_CODE[1]取消
                AlarmTool.stopAllAlarm(this, helper);
                AlarmTool.startAllAlarm(this, helper);
                unKill = true;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("test", "onCreate: ");
        initAlarmSql();
    }

    private void initAlarmSql() {//初始化数据库
        helper = new AlarmDataBaseHelper(this , "Alarm.db" , null , 1);
        helper.getWritableDatabase();
    }
}
