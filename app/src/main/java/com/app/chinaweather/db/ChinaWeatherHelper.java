package com.app.chinaweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 14501_000 on 2016/5/24.
 * 建立三张表Province,City,County分别存放省，市，县的各种数据信息
 */
public class ChinaWeatherHelper extends SQLiteOpenHelper {
    /**
     * City建表语句
     */
    public static final String CREAT_CITY="creat table city(" +
            "id integer primary key autoincrement," +
            "city_name text," +
            "city_code text," +
            "province_id integer)";
    /**
     * County 建表语句
     */
    public static final String CREAT_COUNTY="creat table province(" +
            "id integer primary key autoincrement," +
            "county_name text," +
            "county_code text," +
            "city_id integer)";
    /**
     * province 建表语句
     */
    public static final String CREAT_PROVINCE="creat table province(" +
            "id integer primary key autoincrement," +
            "province_name text," +
            "province_code text)";
    public ChinaWeatherHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建三张表
        db.execSQL(CREAT_PROVINCE);
        db.execSQL(CREAT_CITY);
        db.execSQL(CREAT_COUNTY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
