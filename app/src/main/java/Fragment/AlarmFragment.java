package Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import com.xicp.cjlhappiness.clock.AddAlarmActivity;
import com.xicp.cjlhappiness.clock.R;
import java.util.ArrayList;
import java.util.List;
import Adapter.AlarmAdapter;
import db.AlarmCRUD;
import db.AlarmDataBaseHelper;
import tool.AlarmListData;
import tool.AlarmTool;
import tool.mCallback;

public class AlarmFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemClickListener, mCallback {

    private ListView alarmList;
    private ImageButton buttonAdd;
    private AlarmAdapter listAdapter;
    private List<AlarmListData> listData;
    private AlarmDataBaseHelper helper;
    private View view;

    private boolean isStartRefresh;
    private int refreshNum;

    private static final int TIMER_CODE = 0x10;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == TIMER_CODE){
                listAdapter.notifyDataSetChanged();
                refreshNum ++;
                if (refreshNum >= 10){
                    refreshList();
                    refreshNum = 0;
                    return;
                }
                if (!isStartRefresh) return;
                sendEmptyMessageDelayed(TIMER_CODE, 60 * 1000);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {//初始化View

        if (view == null){
            view = getActivity().getLayoutInflater().inflate(R.layout.alarm_fragment,null);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null){
            parent.removeView(view);
        }

        buttonAdd = (ImageButton) view.findViewById(R.id.add_alarm);
        alarmList = (ListView) view.findViewById(R.id.alarm_list);
        initAlarm();
        initAlarmSql();
        listUpdate();

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        listUpdate();
        if (!isStartRefresh){
            refreshList();
            isStartRefresh = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isStartRefresh = false;
    }

    private void initAlarm() {//初始化list，adapter，设置监听器
        listData = new ArrayList<>();
        buttonAdd.setOnClickListener(this);
        alarmList.setOnItemClickListener(this);
        listAdapter = new AlarmAdapter(getActivity(), R.layout.alarm_list_layout, listData, this);
        alarmList.setAdapter(listAdapter);
    }

    private void initAlarmSql() {//初始化数据库
        helper = new AlarmDataBaseHelper(getActivity() , "Alarm.db" , null , 1);
        helper.getWritableDatabase();

    }

    private void listUpdate(){//刷新、获取listView显示内容
        listData.clear();
        listData = AlarmCRUD.queryAlarm(helper, listData);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int id = data.getIntExtra("id", -1);
        if (resultCode == AlarmTool.ALARM_CLOSE_CODE[3]){//修改
            AlarmTool.stopAlarm(getActivity(), id);
            AlarmTool.startAlarm(getActivity(), helper, id);
        }else if (resultCode == AlarmTool.ALARM_CLOSE_CODE[2]){//删除
            AlarmTool.stopAlarm(getActivity(), id);
        }else if (resultCode == AlarmTool.ALARM_CLOSE_CODE[1]){//添加
            AlarmTool.startAlarm(getActivity(), helper, id);
        }else{
            //取消不作处理
        }
        listUpdate();
    }

    @Override
    public void onClick(View view) {//添加按钮的事件
        startAddAlarmActivity(AlarmTool.ALARM_OPEN_CODE[0], -1);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {//list列表点击事件
        startAddAlarmActivity(AlarmTool.ALARM_OPEN_CODE[1] , listData.get(i).getId());
    }


    //actionFlag为0标志add，为1标志revise
    //通过list点击打开AddAlarmActivity，表示修改，需要获得原有的数据，这里直接传入数据项对应的id
    private void startAddAlarmActivity(int actionFlag , int dataId){
        Intent intent = new Intent(getActivity() , AddAlarmActivity.class);
        intent.putExtra("actionFlag" ,actionFlag);
        if (actionFlag == AlarmTool.ALARM_OPEN_CODE[1]) {
            intent.putExtra("oldAlarm", dataId);
        }
        startActivityForResult(intent , 0x10);
    }

    //计算当前时间到整点分钟的毫秒数并通过handler定时刷新数据
    private void refreshList(){
        long delayed =61 * 1000 - System.currentTimeMillis() % (1000 * 60);
        handler.sendEmptyMessageDelayed(TIMER_CODE, delayed);
    }

    @Override
    public void switchChange(int id, int position, boolean state) {
        int newState;
        if (state) {
            newState = AlarmTool.ALARM_OPEN_CLOSE[0];
        } else {
            newState = AlarmTool.ALARM_OPEN_CLOSE[1];
        }
        AlarmCRUD.updateAlarm(helper, id, newState);
        (listData.get(position)).setState(newState);
        listData = AlarmCRUD.sort(listData);
        listAdapter.notifyDataSetChanged();
    }
}
