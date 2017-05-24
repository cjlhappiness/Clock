package com.example.cjlhappiness.clock;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.view.KeyEvent;
import Fragment.CountdownFragment;
import Fragment.AlarmFragment;
import Fragment.ChronographFragment;

public class MainActivity extends FragmentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTab();
    }

    private void initTab() {//添加3个tab
        FragmentTabHost tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(this,getSupportFragmentManager() ,android.R.id.tabcontent);
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("闹钟") , AlarmFragment.class , null);
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("秒表") , ChronographFragment.class , null);
        tabHost.addTab(tabHost.newTabSpec("tab4").setIndicator("倒计时") , CountdownFragment.class , null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//返回键事件 程序不退出而是转入后台
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
