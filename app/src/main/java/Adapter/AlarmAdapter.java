package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.example.cjlhappiness.clock.R;
import java.util.List;
import db.AlarmDataBaseHelper;
import db.AlarmCRUD;
import tool.AlarmListData;
import tool.AlarmTool;

public class AlarmAdapter extends ArrayAdapter<AlarmListData>{

    private Context context;
    private int resourceId;
    private List<AlarmListData> list;
    private AlarmDataBaseHelper helper;

    public AlarmAdapter(Context context, int resourceId, List<AlarmListData> list) {
        super(context, resourceId, list);
        this.context = context;
        this.resourceId = resourceId;
        this.list = list;
        helper = new AlarmDataBaseHelper(context , "Alarm.db" , null , 1);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view;
        ViewHolder holder;
        boolean state;
        final AlarmListData data = list.get(position);
        if (convertView == null){
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(resourceId , null);
            holder.alarm_list_time = (TextView) view.findViewById(R.id.alarm_list_time);
            holder.alarm_list_surplus = (TextView) view.findViewById(R.id.alarm_list_surplus);
            holder.alarm_list_state = (Switch) view.findViewById(R.id.alarm_list_switch);
            view.setTag(holder);
        }else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        if (data.getState() == AlarmTool.ALARM_OPEN_CLOSE[1]){
            state = true;
        }else{
            state = false;
        }
        final boolean tempState = state;
        holder.alarm_list_time.setText(AlarmTool.getTargetTimeText(data.getTime()));
        if (state){
            holder.alarm_list_surplus.setVisibility(View.VISIBLE);
            holder.alarm_list_surplus.setText(AlarmTool.getSurplusTimeText(data.getTime(), data.getRepeat()));
        }else {
            holder.alarm_list_surplus.setVisibility(View.INVISIBLE);
        }
        holder.alarm_list_state.setChecked(state);
        holder.alarm_list_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newState;
                if (tempState){
                    newState = AlarmTool.ALARM_OPEN_CLOSE[0];
                }else{
                    newState = AlarmTool.ALARM_OPEN_CLOSE[1];
                }
                AlarmCRUD.updateAlarm(helper, data.getId(), AlarmCRUD.UPDATE_CODE[1], 0, "", 0, 0, newState);
            }
        });
//        holder.alarm_list_state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                int state;
//                if (isChecked){
//                    state = AlarmTool.ALARM_OPEN_COLSE[1];
//                }else{
//                    state = AlarmTool.ALARM_OPEN_COLSE[0];
//                }
//
//                AlarmCRUD.updateAlarm(helper, data.getId(), AlarmCRUD.UPDATE_CODE[1], 0, "", 0, state);
//            }
//        });
        return view;
    }

    class ViewHolder{
        TextView alarm_list_time;
        TextView alarm_list_surplus;
        Switch alarm_list_state;
    }

}
