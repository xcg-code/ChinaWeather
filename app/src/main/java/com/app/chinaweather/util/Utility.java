package com.app.chinaweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.app.chinaweather.db.ChinaWeatherDb;
import com.app.chinaweather.model.City;
import com.app.chinaweather.model.County;
import com.app.chinaweather.model.Province;

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
}
