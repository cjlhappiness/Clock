package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//该类执行数据库的创建，数据库的初始化
public class AlarmDataBaseHelper extends SQLiteOpenHelper{

    //数据表结构：
    //          id（int）        自增，标志每条数据，以及保证删改操作准确找到对应数据(主键)
    //          time（int）      用户设定的时间，以00：00开始计算的长度，单位s
    //          repeat（String） 重复，约定1-7为对应天数，0为单次不重复，8为每天重复
    //          vibrate（int）   是否震动，0为off，1为on
    //          ring（int）      是否铃声，0为off，1为on
    //          state（int）     状态标志该闹钟当前是否执行，0为off，1为on
    public static final String CREATE_ALARM_DATA = "create table AlarmTable (" +
            "id integer primary key autoincrement ," +
            "time integer," +
            "repeat text," +
            "vibrate integer," +
            "ring integer," +
            "state integer)";

    public AlarmDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_ALARM_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
