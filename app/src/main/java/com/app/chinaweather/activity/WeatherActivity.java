package com.app.chinaweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.chinaweather.R;
import com.app.chinaweather.util.HttpCallbackListener;
import com.app.chinaweather.util.HttpUtil;
import com.app.chinaweather.util.Utility;

/**
 * Created by 14501_000 on 2016/5/29.
 */
public class WeatherActivity extends Activity implements View.OnClickListener{
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;//显示城市名
    private TextView publishText;//显示发布时间
    private TextView weatherDespText;//显示天气信息
    private TextView temp1Text;//显示气温1
    private TextView temp2Text;//显示气温2
    private TextView currentDateText;//显示当前日期
    private Button switchCity;//切换城市按钮
    private Button refreshWeather;//更新天气按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        init();
        String countyCode=getIntent().getStringExtra("county_code");//获取传递过来的县代码
        if(!TextUtils.isEmpty(countyCode)){
            //有县级代号就去查询天气
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }else{
            //没有县级代号就直接显示本地天气
            //showWeather();
            Intent intent=new Intent(this,ChooseAreaActivity.class);
            startActivity(intent);

        }
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_city:
                //Toast.makeText(WeatherActivity.this, "返回", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("同步中...");
                SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode=prefs.getString("weather_code","");
                if(!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }

    }
    private void init() {
        weatherInfoLayout= (LinearLayout) findViewById(R.id.weather_info);
        cityNameText= (TextView) findViewById(R.id.city_name);
        publishText= (TextView) findViewById(R.id.publish_text);
        weatherDespText= (TextView) findViewById(R.id.weather_desp);
        temp1Text= (TextView) findViewById(R.id.temp1);
        temp2Text= (TextView) findViewById(R.id.temp2);
        currentDateText= (TextView) findViewById(R.id.current_date);
        switchCity= (Button) findViewById(R.id.switch_city);
        refreshWeather= (Button) findViewById(R.id.refresh_weather);
    }
    /**
     * 查询县级代号所对应的天气代号
     */
    private void queryWeatherCode(String countyCode){
        String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        Log.i("info",address);
        queryFromServer(address,"countyCode");
    }
    /**
     * 查询天气代号所对应的天气
     */
    private void queryWeatherInfo(String weatherCode){
        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        Log.i("info",address);
        queryFromServer(address,"weatherCode");
    }
    /**
     * 根据传入地址及类型去查询天气代号或天气信息
     */
    private void queryFromServer(final String address, final String type){
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                if("countyCode".equals(type)){
                    if(!TextUtils.isEmpty(response)){
                        //从服务器返回的数据解析出天气代号
                        String[] array=response.split("\\|");
                        if(array!=null&&array.length==2){
                            String weatherCode=array[1];
                            Log.i("info",weatherCode);
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }else if("weatherCode".equals(type)){
                    Log.i("info",response);
                    //处理服务器返回的天气信息
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    /**
     * 从SharedPreferences文件中读取储存的天气信息，并显示到界面上
     */
    private void showWeather(){
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        temp1Text.setText(prefs.getString("temp1",""));
        temp2Text.setText(prefs.getString("temp2",""));
        weatherDespText.setText(prefs.getString("weather_desp",""));
        publishText.setText("今天"+prefs.getString("publish_time","")+"发布");
        currentDateText.setText(prefs.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }
}
