package com.app.chinaweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.app.chinaweather.db.ChinaWeatherDb;
import com.app.chinaweather.model.City;
import com.app.chinaweather.model.County;
import com.app.chinaweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 14501_000 on 2016/5/25.
 *
 */
public class Utility {
    /**
     *解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvinceResponse(ChinaWeatherDb chinaWeatherDb,String response){
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces=response.split(",");//将数据串中数据以逗号分割成各部分加入数组中
            if(allProvinces!=null&&allProvinces.length>0){
                for(String p:allProvinces){
                    String[] array=p.split("\\|");
                    Province province=new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    chinaWeatherDb.saveProvince(province);//将省份数据保存在Province数据表中
                }
                return true;
            }
        }
        return false;
    }
    /**
     *解析和处理服务器返回的市级数据
     */
    public synchronized static boolean handleCityResponse(ChinaWeatherDb chinaWeatherDb,String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            String[] allCities=response.split(",");//将数据串中数据以逗号分割成各部分加入数组中
            if(allCities!=null&&allCities.length>0){
                for(String c:allCities){
                    String[] array=c.split("\\|");
                    City city=new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    chinaWeatherDb.saveCity(city);//将市级数据保存在City数据表中
                }
                return true;
            }
        }
        return false;
    }
    /**
     *解析和处理服务器返回的县级数据
     */
    public synchronized static boolean handleCountryResponse(ChinaWeatherDb chinaWeatherDb,String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            String[] allCountrys=response.split(",");//将数据串中数据以逗号分割成各部分加入数组中
            if(allCountrys!=null&&allCountrys.length>0){
                for(String p:allCountrys){
                    String[] array=p.split("\\|");
                    County county=new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    chinaWeatherDb.saveCounty(county);//将县级数据保存在County数据表中
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的数据，并将解析出的数据储存到本地
     */
    public static void handleWeatherResponse(Context context, String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
            String cityName=weatherInfo.getString("city");
            String weatherCode=weatherInfo.getString("cityid");
            String temp1=weatherInfo.getString("temp1");
            String temp2=weatherInfo.getString("temp2");
            String weatherDesp=weatherInfo.getString("weather");
            String publishTime=weatherInfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *将解析出的天气信息存储到SharedPreferences文件中
     */
    private static void saveWeatherInfo(Context context, String cityName,
 String weatherCode, String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("publish_time",publishTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }
}
