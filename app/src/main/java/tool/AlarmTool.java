package tool;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.xicp.cjlhappiness.clock.AlarmActivity;

import java.util.Calendar;

import db.AlarmCRUD;
import db.AlarmDataBaseHelper;

public class AlarmTool {

    //约定常量，当AddAlarmActivity结束返回时，1代表取消，2代表添加，3代表删除，4代表修改
    public static final int[] ALARM_CLOSE_CODE = new int[]{1, 2, 3, 4};
    //约定常量，当AddAlarmActivity被打开时，1代表添加，2代表修改
    public static final int[] ALARM_OPEN_CODE = new int[]{1, 2};
    //约定常量，0代表关闭,1代表开启 闹钟
    public static final int[] ALARM_OPEN_CLOSE = new int[]{0, 1};

    //计算当前时间到设定时间的剩余时间，传入目标时间（秒）以及距离目标时间的间隔天数
    public static int getSurplusTime(int target, int day) {
        int surplusTime;
        int nowTime = getNowTime();
        int oneDay = 24 * 3600;
//        if (target <= nowTime) {//前面已经做过如果target <= nowTime要减1天的逻辑，所以这里要加回去，因为以target = nowDay为临界点，<或>只是相差了2秒，并不是差了一天
//            day += 1;
//        }
        int DayToSecond = day * oneDay;
        surplusTime = target - nowTime + DayToSecond;

        return surplusTime - 60;
    }

    //获得设置时的目标时间字符串（如 12：01），传入目标时间（秒）
    public static String getTargetTimeText(int target) {
        String targetTimeText = String.format("%02d : %02d", target / 3600, target % 3600 / 60);
        return targetTimeText;
    }

    //获得当前的系统时间，精确到分,返回秒
    public static int getNowTime() {
        Calendar calendar = Calendar.getInstance();
        int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
        int nowMinute = calendar.get(Calendar.MINUTE);
//        int nowSecond = calendar.get(Calendar.SECOND);
        return nowHour * 3600 + nowMinute * 60;
    }

    //获得现在在一周中是第几天，1为星期一
    public static int getNowDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int nowDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;//因为星期天是1，而不是星期一是1，所以要转换
        if (nowDay == 0) {
            nowDay = 7;
        }
        return nowDay;
    }

    // /获得下一个目标时间到当前时间的时长字符串（如 1天16时30分后提醒），传入剩余时间（秒）
    public static String getNextTimeText(int surplusTime) {
        String nextTimeText;
        int day = surplusTime / 86400;
        int surplusTimeOfDay = surplusTime % 86400;
        int hour = surplusTimeOfDay / 3600;
        int minute = surplusTimeOfDay % 3600 / 60;
//        int second = surplusTimeOfDay % 3600 % 60;
        if (day > 0) {
            nextTimeText = String.format("%02d天%02d时%02d分后提醒", day, hour, minute);
        } else if (hour > 0) {
            nextTimeText = String.format("%02d时%02d分后提醒", hour, minute);
        } else if (minute > 0) {
            nextTimeText = String.format("%02d分后提醒", minute);
        } else {
            nextTimeText = "1分钟内提醒";
        }
        return nextTimeText;
    }

    //获得用户设定的时间周期内下一次时间的目标时间，周期内下一次的剩余天数
    //返回的是指定的日期和时间距 1970 年 1 月 1 日午夜(GMT 时间)之间的毫秒数
    public static long getTargetTime(int target, int someDay) {
        Calendar calendar = Calendar.getInstance();
        int targetDay = someDay;
        int targetHour = target / 3600;
        int targetMinute = target % 3600 / 60;
        int targetSecond = 0;
//        if (target <= getNowTime()) {
//            targetDay += 1;
//        }
        calendar.set(Calendar.HOUR_OF_DAY, targetHour);
        calendar.set(Calendar.MINUTE, targetMinute);
        calendar.set(Calendar.SECOND, targetSecond);
        calendar.add(Calendar.DAY_OF_MONTH, targetDay);
        return calendar.getTimeInMillis();
    }

    //返回当前日期距离参数日期的天数差（只考虑天数差不考虑target和nowTime的关系）
    public static int getOverDay(int nextDay, int target, String repeat) {
        int nowDay = getNowDayOfWeek();
        int nowTime = getNowTime();
        int overDay;

        //考虑0和8两种周期day数据
        if (nextDay == 0 || nextDay == 8) {
            if (target <= nowTime){
                return 1;
            }else {
                return 0;
            }
        }

        //如果设定时间小于等于系统时间，说明当前日期的闹钟已被执行，应该重新获得下一天的日期
        if (nowDay == nextDay){
            if (target <= nowTime){
                nextDay = getNextDay(repeat, 1);
            }else {
                return 0;
            }
        }

        //如果nextDay小于或等于nowDay的值，则说明应该计算距离下一周的天数（此处等于nowDay时，target时间大于now的情况在上面已经考虑无需再做处理）
        if (nextDay <= nowDay) {
            overDay = nextDay - nowDay + 7;
        } else {
            overDay = nextDay - nowDay;
        }

        //上面的代码只是完成了天数差的计算，但是并未考虑具体时间的影响，这里进行考虑，如周三12：00到周五11：00，差了2天，但实际考虑时间，只差了一个完整的1天以及23个小时
        //这也是getSurplusTime()和getTargetTime()方法进行了target <= nowTime要+1的原因，因为这个方法算法的问题
//        if (target <= nowTime) {
//            return overDay - 1;
//        }
        return overDay;
    }

    //获得设定的循环周期内当前日期距离目标天最近的下一个日期（日期指的是星期几），参数code代表是否重新计算下一个日期
    public static int getNextDay(String repeat, int code) {
        int nowDay = getNowDayOfWeek();
        if (code == 1) {
            nowDay += 1;
        }
        int[] dayOfWeek = new int[repeat.length()];

        for (int i = 0; i < repeat.length(); i++) {//将重复周期转换为整型数据，如[1,5,6]
            dayOfWeek[i] = Integer.parseInt(repeat.substring(i, i + 1));
        }

        for (int i = 0; i < repeat.length(); i++) {//找到距离现在日期最近的日期，如今天是2，[1,5,6]，则应该返回5
            if (nowDay <= dayOfWeek[i]) {
                return dayOfWeek[i];
            }
        }

        return dayOfWeek[0];//这里被执行显然是数组内没有一个日期大于当前日期，那么就代表这个周期已经结束，重新返回该周期的第一天
    }


    //将repeat里的数字装换成汉字形式
    public static String repeatNumChangeToString(String repeat) {
        String string = "";
        if (repeat.substring(0, 1).equals("0") || repeat.substring(0, 1).equals("8")) {
            String temp = repeat.substring(0, 1);
            if (temp.equals("0")) {
                string = "仅一次";
            } else {
                string = "每天";
            }
            return string;
        }
        for (int i = 0; i < repeat.length(); i++) {
            switch (repeat.substring(i, i + 1)) {
                case "1":
                    string += "一";
                    break;
                case "2":
                    string += "二";
                    break;
                case "3":
                    string += "三";
                    break;
                case "4":
                    string += "四";
                    break;
                case "5":
                    string += "五";
                    break;
                case "6":
                    string += "六";
                    break;
                case "7":
                    string += "日";
                    break;
            }
        }
        return string;
    }

    public static String getSurplusTimeText(int target, String repeat) {
        String text;
        int nextTime = getNextDay(repeat, 0);
        int overTime = getOverDay(nextTime, target, repeat);
        int surplusTime = getSurplusTime(target, overTime);
        text = getNextTimeText(surplusTime);
        return text;
    }

    /*
    以上为时间转换相关方法
    **********************************************************************************************
    以下为Alarm功能相关方法
    */

    public static void startAlarm(Context context, AlarmDataBaseHelper helper, int id) {
        AlarmListData data = AlarmCRUD.queryOneAlarm(helper, id);
        int nextTime = getNextDay(data.getRepeat(), 0);
        int overTime = getOverDay(nextTime, data.getTime(), data.getRepeat());
        long longTime = getTargetTime(data.getTime(), overTime);
        Intent intent = new Intent(context, AlarmActivity.class);
        intent.putExtra("dataId", id);
        PendingIntent pi = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, longTime, pi);
    }

    public static void stopAlarm(Context context, int id) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pi);
    }

}
