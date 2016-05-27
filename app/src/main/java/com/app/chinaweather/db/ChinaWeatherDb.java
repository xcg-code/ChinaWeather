package com.app.chinaweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.app.chinaweather.model.City;
import com.app.chinaweather.model.County;
import com.app.chinaweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 14501_000 on 2016/5/24.
 * 封装一些常用的类，方便以后使用
 */
public class ChinaWeatherDb {
    public static final String DB_NAME="china_weather";//数据库名
    public static final int Version=1;//数据库版本
    private static ChinaWeatherDb chinaWeatherDb;
    private SQLiteDatabase db;
    /**
     * 将构造方法私有化
     * @param context
     */
    private ChinaWeatherDb(Context context) {
        ChinaWeatherHelper dbHelper=new ChinaWeatherHelper(context,DB_NAME,null,Version);
        db=dbHelper.getWritableDatabase();
    }
    /**
     * 获取ChinaWeatherDb实例
     */
    public synchronized static ChinaWeatherDb getInstance(Context context){
        if(chinaWeatherDb==null){
            chinaWeatherDb=new ChinaWeatherDb(context);
        }
        return chinaWeatherDb;
    }
    /**
     * 将Province实例存入数据库
     */
    public void saveProvince(Province province){
        if(province!=null){
            ContentValues value=new ContentValues();
            value.put("province_name",province.getProvinceName());
            value.put("province_code",province.getProvinceCode());
            db.insert("Province",null,value);
        }
    }
    /**
     * 从数据库读取省份信息
     */
    public List<Province> loadProvince(){
        List<Province> provinceList=new ArrayList<Province>();
        Cursor cursor=db.query("Province",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
           while(cursor.moveToNext()){
               Province province=new Province();
               province.setId(cursor.getInt(cursor.getColumnIndex("id")));
               province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
               province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
               provinceList.add(province);
           }
        }
        return provinceList;
    }
    /**
     * 将City实例存入数据库
     */
    public void saveCity(City city){
        if(city!=null){
            ContentValues value=new ContentValues();
            value.put("city_name",city.getCityName());
            value.put("city_code",city.getCityCode());
            value.put("province_id",city.getProvinceId());
            db.insert("City",null,value);
        }
    }
    /**
     * 从数据库读取城市信息
     */
    public List<City> loadCity(int provinceId){
        List<City> cityList=new ArrayList<City>();
        Cursor cursor=db.query("City",null,"province_id=?",new String[]{String.valueOf(provinceId)},null,null,null);
        if(cursor.moveToFirst()){
            while(cursor.moveToNext()){
                City city=new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                cityList.add(city);
            }
        }
        return cityList;
    }
    /**
     * 将County实例存入数据库
     */
    public void saveCounty(County county){
        if(county!=null){
            ContentValues value=new ContentValues();
            value.put("county_name",county.getCountyName());
            value.put("county_code",county.getCountyCode());
            value.put("city_id",county.getCityId());
            db.insert("County",null,value);
        }
    }
    /**
     * 从数据库读取城市信息
     */
    public List<County> loadCounty(int cityId){
        List<County> countyList=new ArrayList<County>();
        Cursor cursor=db.query("County",null,"city_id=?",new String[]{String.valueOf(cityId)},null,null,null);
        if(cursor.moveToFirst()){
            while(cursor.moveToNext()){
               County county=new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
                countyList.add(county);
            }
        }
        return countyList;
    }

}
