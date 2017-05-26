package db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import tool.AlarmListData;

//该类执行数据库的增删查改操作
public class AlarmCRUD {

    //查询所有数据
    public static List<AlarmListData> queryAlarm(AlarmDataBaseHelper helper, List<AlarmListData> list){
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("AlarmTable" ,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int time = cursor.getInt(cursor.getColumnIndex("time"));
                String repeat = cursor.getString(cursor.getColumnIndex("repeat"));
                int vibrate = cursor.getInt(cursor.getColumnIndex("vibrate"));
                int ring = cursor.getInt(cursor.getColumnIndex("ring"));
                int state = cursor.getInt(cursor.getColumnIndex("state"));
                AlarmListData data = new AlarmListData(id, time, repeat, vibrate, ring, state);
                list.add(data);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return sort(list);
    }

    //查询某一项
    public static AlarmListData queryOneAlarm(AlarmDataBaseHelper helper , int dataId){
        AlarmListData data = null;
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("AlarmTable", null, "id =" + dataId , null, null, null, null);
        if (cursor.moveToFirst()){
            do {
                int time = cursor.getInt(cursor.getColumnIndex("time"));
                String repeat = cursor.getString(cursor.getColumnIndex("repeat"));
                int vibrate = cursor.getInt(cursor.getColumnIndex("vibrate"));
                int ring = cursor.getInt(cursor.getColumnIndex("ring"));
                int state = cursor.getInt(cursor.getColumnIndex("state"));
                data = new AlarmListData(dataId, time, repeat, vibrate, ring, state);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return data;
    }

    //增加
    public static int createAlarm(AlarmDataBaseHelper helper, int time, String repeat, int vibrate,
                                  int ring, int state){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("time" , time);
        values.put("repeat" ,repeat);
        values.put("vibrate" , vibrate);
        values.put("ring" , ring);
        values.put("state" , state);
        int id = (int)db.insert("AlarmTable" , null , values);
        return id;
    }

    //删除
    public static void deleteAlarm(AlarmDataBaseHelper helper , int id){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("AlarmTable" , "id ="+id , null);
    }

    //更新
    public static void updateAlarm(AlarmDataBaseHelper helper, int id, int time,
                                   String repeat, int vibrate, int ring, int state){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
            values.put("time", time);
            values.put("repeat", repeat);
            values.put("vibrate", vibrate);
            values.put("ring", ring);
        values.put("state", state);
        db.update("AlarmTable", values, "id ="+ id, null);
    }

    //更新
    public static void updateAlarm(AlarmDataBaseHelper helper, int id, int state){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("state", state);
        db.update("AlarmTable", values, "id ="+ id, null);
    }

    //排序算法
    public static List<AlarmListData> sort (List<AlarmListData> list){
        List<AlarmListData> temp1 = new ArrayList<>();
        List<AlarmListData> temp2 = new ArrayList<>();

        for (int i = 0 ; i < list.size() - 1; i++){//冒泡排序
            for (int j = i + 1 ; j < list.size() ; j++){
                if (list.get(i).getTime() > list.get(j).getTime()){
                    AlarmListData data = list.get(j);
                    list.add(j, list.get(i));
                    list.remove(j + 1);
                    list.add(i, data);
                    list.remove(i + 1);
                }
            }
        }
        for (int i = 0 ; i < list.size() ; i++){//将筛选出的目标时间最小的数据进一步筛选其状态并分类
            if (list.get(i).getState() == 1){
                temp1.add(list.get(i));
            }else{
                temp2.add(list.get(i));
            }
        }
        list.clear();
        list.addAll(temp1);
        list.addAll(temp2);
        return list;
    }

}
