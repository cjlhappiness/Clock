package com.xicp.cjlhappiness.clock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import Fragment.AlarmFragment;
import db.AlarmCRUD;
import db.AlarmDataBaseHelper;
import tool.AlarmListData;
import tool.AlarmTool;
import tool.mSpinner;
import static tool.AlarmTool.*;

public class AddAlarmActivity extends AppCompatActivity
        implements TimePicker.OnTimeChangedListener , View.OnClickListener ,
        AdapterView.OnItemSelectedListener , CompoundButton.OnCheckedChangeListener{

    private TimePicker timePicker;
    private TextView showPickTime;
    private mSpinner chooseSpinner;
    private Switch chooseVibrate;
    private Switch chooseRing;
    private Button cancel, confirm;
    private int alarmId;
    private int targetTime;
    private int closeCode;
    private int isAlarmAddOrDel;
    private AlarmListData oldAlarmData;
    private AlarmDataBaseHelper helper;

    private boolean loadSelect;

    private String repeat  = "0";  //默认单次
    private int    vibrate = 1;     //默认开启震动
    private int    ring    = 1;     //默认开启铃声


    //初始化各个控件，创建数据库，设置监听器
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        chooseSpinner = (mSpinner) findViewById(R.id.chooseSpinner);
        chooseVibrate = (Switch) findViewById(R.id.chooseVibrate);
        chooseRing = (Switch) findViewById(R.id.chooseRing);
        showPickTime = (TextView) findViewById(R.id.showPickTime);
        cancel = (Button) findViewById(R.id.cancel);
        confirm = (Button) findViewById(R.id.confirm);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setOnTimeChangedListener(this);
        chooseSpinner.setOnItemSelectedListener(this);
        chooseVibrate.setOnCheckedChangeListener(this);
        chooseRing.setOnCheckedChangeListener(this);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);
        helper = new AlarmDataBaseHelper(this, "Alarm.db" , null , 1);
        addOrRevise(getIntent());//判断按钮显示的文字及按钮操作

        loadSelect = true;
    }

    //根据界面被打开的方式，改变按钮文字，spinner列表，switch开关，text文本剩余时间的显示
    private void addOrRevise(Intent intent) {
        targetTime = getNowTime();
        changeToTargetString(targetTime, repeat);//将目标时间转化为显示的字符串

        isAlarmAddOrDel = intent.getIntExtra("actionFlag" , AlarmTool.ALARM_OPEN_CODE[0]);//添加或修改

        if (isAlarmAddOrDel == AlarmTool.ALARM_OPEN_CODE[1]){//通过list点击进来
            alarmId = intent.getIntExtra("oldAlarm", -1);//获得要操作的闹钟id
            oldAlarmData = AlarmCRUD.queryOneAlarm(helper, alarmId);
            repeat = oldAlarmData.getRepeat();
            changeButtonState();//通过list点击进入，改变按钮显示的文字，switch开关状态（默认开）
            selectInterval();//判断用户选择的周期（一次 或 重复）并设置相应的spinner选择项
        }
    }

    //通过list点击进入，改变按钮显示的文字，switch震动开关状态（默认（1）开）
    private void changeButtonState(){
        cancel.setText("删除");
        confirm.setText("保存");
        if (oldAlarmData.getVibrate() == 0) {
            chooseVibrate.setChecked(false);
        }
        if (oldAlarmData.getRing() == 0) {
            chooseRing.setChecked(false);
        }
    }

    //时间选择器change回调方法，获得用户选择的时间
    @Override
    public void onTimeChanged(TimePicker timePicker, int i, int i1) {
        targetTime = i * 3600 + i1 * 60;
        changeToTargetString(targetTime, repeat);
    }

    //将下一次闹钟时间转化为显示的字符串
    private void changeToTargetString(int targetTime, String repeat){
        showPickTime.setText(getSurplusTimeText(targetTime, repeat));
    }

    //button按钮回调方法
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cancel:
                cancelButton(isAlarmAddOrDel);
                break;
            case R.id.confirm:
                confirmButton(isAlarmAddOrDel);
                break;
        }
    }

    private void cancelButton(int operateCode){//取消，删除按钮
        if (operateCode ==  ALARM_OPEN_CODE[1]){//通过list点击进来
            AlarmCRUD.deleteAlarm(helper , oldAlarmData.getId());
            closeCode = AlarmTool.ALARM_CLOSE_CODE[2];
        }else {
            closeCode = AlarmTool.ALARM_CLOSE_CODE[0];
        }
        returnResult();
    }

    private void confirmButton(int operateCode){//添加，修改按钮
        if (targetTime == 0){targetTime = getNowTime();}
        if (operateCode == ALARM_OPEN_CODE[1]){//通过添加按钮点击进来
            AlarmCRUD.updateAlarm(helper, alarmId, targetTime, repeat, vibrate, ring, ALARM_OPEN_CLOSE[1]);
            closeCode = AlarmTool.ALARM_CLOSE_CODE[3];
        }else {
            alarmId = AlarmCRUD.createAlarm(helper, targetTime, repeat, vibrate, ring, ALARM_OPEN_CLOSE[1]);
            closeCode = AlarmTool.ALARM_CLOSE_CODE[1];
        }
        returnResult();
    }

    //将操作结果返回AlarmFragment中，以更新显示数据
    private void returnResult(){
        Intent resultIntent = new Intent(this , AlarmFragment.class);
        resultIntent.putExtra("id", alarmId);
        setResult(closeCode, resultIntent);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {//Spinner周期选择器回调方法
        repetitionResult(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {//Spinner周期选择器回调方法

    }

    private void repetitionResult(int position){
        switch (position){
            case 0:
                repeat = "0";//单次
                break;
            case 1:
                repeat = "8";//每天
                break;
            case 2:
                repeat = "12345";//周一至五
                break;
            case 3:
                divRepetitionResult();//自定义重复
                break;
        }
        changeToTargetString(targetTime ,repeat);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {//Switch开关回调方法
        switch (compoundButton.getId()){
            case R.id.chooseVibrate:
                if (b){
                    vibrate = 1;//开
                }else{
                    vibrate = 0;
                }
                break;
            case R.id.chooseRing:
                if (b){
                    ring = 1;//开
                }else{
                    ring = 0;
                }
                break;
        }
    }

    private void divRepetitionResult(){
        if (loadSelect){
            loadSelect = false;
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.div_repetition , null);
        int[] checkDay = new int[]{R.id.check_monday , R.id.check_tuesday , R.id.check_wednesday ,
        R.id.check_thursday , R.id.check_friday , R.id.check_saturday , R.id.check_sunday};
        final CheckBox[] checkItem = new CheckBox[7];
        for (int i = 0 ; i < 7 ; i++){
            checkItem[i] = (CheckBox) view.findViewById(checkDay[i]);
        }
        AlertDialog.Builder  dialog= new AlertDialog.Builder(this);
        dialog
                .setView(view)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectInterval();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        repeat = "";
                        for (int x = 0 ; x < 7 ; x++){
                            if (checkItem[x].isChecked()){
                                repeat += x + 1;
                            }
                        }
                        if (repeat.equals("")){
                            repeat = "0";
                        }
                        selectInterval();
                    }
                });
        dialog.show();
    }

    //判断用户选择的周期（一次 或 重复）并设置相应的spinner选择项
    private void selectInterval() {
        if (repeat.equals("0") ){
            chooseSpinner.setSelection(0);
        }else if (repeat.equals("1234567") || repeat.equals("8") ){
            chooseSpinner.setSelection(1);
        }else if (repeat.equals("12345")){
            chooseSpinner.setSelection(2);
        }else{
            chooseSpinner.setSelection(3);
        }
        changeToTargetString(targetTime, repeat);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            closeCode = AlarmTool.ALARM_CLOSE_CODE[0];
            returnResult();
        }
        return true;
    }
}
