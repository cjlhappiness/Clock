package Fragment;

/*秒表*/

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.example.cjlhappiness.clock.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChronographFragment extends Fragment implements View.OnClickListener{

    private TextView chronographShow;
    private Button buttonStart;
    private Button buttonStop;
    private ListView chronographList;
    private SimpleAdapter listAdapter;
    private List<Map<String ,String>> list = new ArrayList<>();
    private long mStart;
    private long mTime;
    private int listSize;
    private boolean isRun;
    private boolean isPause;
    private static final int TIMER_CODE = 0x11;
    private View view;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chronographShow.setText(timeChange(mStart));
            handler.sendEmptyMessageDelayed(TIMER_CODE ,100);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null){
            view = getActivity().getLayoutInflater().inflate(R.layout.chronograph_fragment ,null);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null){
            parent.removeView(view);
        }

        chronographShow = (TextView) view.findViewById(R.id.chronograph_show);
        buttonStart = (Button) view.findViewById(R.id.chronograph_start);
        buttonStop = (Button) view.findViewById(R.id.chronograph_stop);
        chronographList = (ListView) view.findViewById(R.id.chronograph_list);
        initChronograph();
        return view;
    }

    private void initChronograph() {
        if (mTime != 0){
            chronographShow.setText(timeChange(mStart));
        }
        if (isRun){
            buttonStart.setText("计次");
            buttonStop.setVisibility(View.VISIBLE);
        }
        if (isPause){
            buttonStart.setText("继续");
            buttonStop.setText("复位");
            buttonStop.setVisibility(View.VISIBLE);
        }
        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        listAdapter = new SimpleAdapter(getActivity() ,list ,R.layout.chronograph_list_layout , new String[]{"number" ,"time"} ,
        new int[]{R.id.chronograph_list_title ,R.id.chronograph_list_content});
        chronographList.setAdapter(listAdapter);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.chronograph_start://开始 或 计次
                if (!isRun){            //开始
                    startTimer();
                    buttonStart.setText("计次");
                    if (buttonStop.getVisibility() == View.VISIBLE){
                        buttonStop.setText("暂停");
                        isPause = false;
                    }
                    buttonStop.setVisibility(View.VISIBLE);
                    isRun = true;
                }else{                 //计次
                    Map map = new HashMap();
                    map.put("number",String.format("%02d",++listSize));
                    map.put("time" ,timeChange(mStart));
                    list.add(0 ,map);
                    listAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.chronograph_stop://暂停 或 复位
                if (!isPause){         //暂停
                    stopTimer();
                    buttonStart.setText("继续");
                    buttonStop.setText("复位");
                    isPause = true;
                    isRun = false;
                }else{                //复位
                    resetTimer();
                    buttonStop.setVisibility(View.GONE);
                    chronographShow.setText("00:00");
                    buttonStart.setText("开始");
                    buttonStop.setText("暂停");
                    listAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private void startTimer(){
        mStart = System.currentTimeMillis() - mTime;
        handler.sendEmptyMessage(TIMER_CODE);
    }

    private void stopTimer(){
        mTime +=System.currentTimeMillis() - mStart;
        handler.removeMessages(TIMER_CODE);
    }

    private void resetTimer(){
        stopTimer();
        list.clear();
        mStart = 0;
        mTime = 0;
        listSize = 0;
        isRun = false;
        isPause = false;
    }

    private String timeChange(long mStart){
        //这里判断，如果isPause=true说明已经暂停，因此要直接使用mTime的值即可
        long nowTime;
        if (isPause){
            nowTime = mTime;
        }else {
            nowTime = System.currentTimeMillis() - mStart;
        }
//        long h = nowTime / (60 * 60 * 1000);//计算小时
        long m = nowTime / (60 * 1000);//计算分
        long s = nowTime / 1000 % 60;//计算秒
        String string = String.format("%02d:%02d",m ,s);
        return string;
    }


}
