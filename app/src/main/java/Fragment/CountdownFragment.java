package Fragment;

/*倒计时*/

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.cjlhappiness.clock.R;

public class CountdownFragment extends Fragment implements View.OnClickListener ,
        Chronometer.OnChronometerTickListener ,SeekBar.OnSeekBarChangeListener {

    private TextView countdownShow;
    private TextView countdownChoose;
    private Button buttonStart;
    private Button buttonStop;
    private SeekBar countdownSeek;
    private Chronometer countdownChr;
    private boolean isStart;
    private boolean isSeek;
    private long mTime;
    private long seekTime;
    private int longTime;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null){
            view = getActivity().getLayoutInflater().inflate(R.layout.countdown_fragment ,null);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null){
            parent.removeView(view);
        }

        countdownShow = (TextView) view.findViewById(R.id.countdown_show);
        countdownChoose = (TextView) view.findViewById(R.id.countdown_choose);
        countdownSeek = (SeekBar) view.findViewById(R.id.countdown_seek);
        buttonStart = (Button) view.findViewById(R.id.countdown_start);
        buttonStop = (Button) view.findViewById(R.id.countdown_stop);
        countdownChr = (Chronometer) view.findViewById(R.id.countdown_chr);
        initCountdown();

        return view;
    }

    private void initCountdown(){
        buttonStart.setEnabled(false);
        countdownChr.setVisibility(View.INVISIBLE);
        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        countdownChr.setOnChronometerTickListener(this);
        countdownSeek.setOnSeekBarChangeListener(this);
        countdownChoose.setText("计时长为 " +countdownSeek.getProgress() + " 分钟");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.countdown_start:
                if (!isStart){
                    startTimer();
                    isStart = true;
                    buttonStart.setText("暂停");
                    buttonStop.setVisibility(View.VISIBLE);
                    countdownChr.setVisibility(View.VISIBLE);
                }else{
                    pauseTimer();
                    isStart = false;
                    buttonStart.setText("继续");
                }
                break;
            case R.id.countdown_stop:
                stopTimer();
                break;
        }
    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
        long time =(SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;
        long remainTime = seekTime - time;
        long m = remainTime / 60;
        long s = remainTime % 60;
        String showTime = String.format("%02d:%02d" ,m ,s);
        countdownSeek.setProgress((int)m);
        countdownShow.setText(showTime);
        if (time >= seekTime){
            stopTimer();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        countdownChoose.setText("计时长为 " + progress + " 分钟");
        if (!isSeek){
            seekTime = countdownSeek.getProgress() * 60;
            countdownChoose.setText("计时长为 " + progress + " 分钟");
        }else{
            countdownChoose.setText("计时长为 " + longTime + " 分钟");
        }
        if (progress == 0 && !isStart){
            buttonStart.setEnabled(false);
        }else{
            buttonStart.setEnabled(true);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void startTimer(){
        if (mTime > 0){
            countdownChr.setBase(SystemClock.elapsedRealtime() - mTime);
        }else{
            countdownChr.setBase(SystemClock.elapsedRealtime());
        }
        longTime = countdownSeek.getProgress();
        countdownChr.setFormat("已用时间：%s");
        countdownChr.start();
        isSeek = true;
        countdownSeek.setEnabled(false);
    }

    private void pauseTimer(){
        mTime = SystemClock.elapsedRealtime() - countdownChr.getBase();
        countdownChr.stop();
    }

    private void stopTimer(){
        pauseTimer();
        mTime = 0;
        longTime = 0;
        isStart = false;
        isSeek = false;
        countdownSeek.setProgress(0);
        buttonStart.setText("开始");
        countdownShow.setText("00:00");
        countdownSeek.setEnabled(true);
        buttonStop.setVisibility(View.GONE);
        countdownChr.setVisibility(View.INVISIBLE);
    }
}
