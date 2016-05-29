package com.app.chinaweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.chinaweather.R;
import com.app.chinaweather.db.ChinaWeatherDb;
import com.app.chinaweather.model.City;
import com.app.chinaweather.model.County;
import com.app.chinaweather.model.Province;
import com.app.chinaweather.util.HttpCallbackListener;
import com.app.chinaweather.util.HttpUtil;
import com.app.chinaweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 14501_000 on 2016/5/26.
 */
public class ChooseAreaActivity extends Activity {
    private static final int LEVEL_PROVINCE=0;
    private static final int LEVEL_CITY=1;
    private static final int LEVEL_COUNTY=2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ChinaWeatherDb chinaWeatherDb;
    private List<String> datalist=new ArrayList<String>();

    /**
     * 省份列表
     */
    private List<Province> provinceList;

    /**
     *市列表
     */
    private List<City> cityList;

    /**
     *县列表
     */
    private List<County> countyList;
    private Province selectedProvince;//被选中的省
    private City selectedCity;//被选中的市
    private int currentLevel;//当前选中的级别
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
//        if(prefs.getBoolean("city_selected",false)){
//            Intent intent=new Intent(this,WeatherActivity.class);
//            startActivity(intent);
//            finish();
//            return;
//        }------(书籍源代码有误删去)------
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView= (ListView) findViewById(R.id.list_view);
        titleText= (TextView) findViewById(R.id.title_text);
        chinaWeatherDb=ChinaWeatherDb.getInstance(this);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,datalist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();
               }else if(currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();
                }else if(currentLevel==LEVEL_COUNTY){
                    String countyCode=countyList.get(position).getCountyCode();
                    Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();//加载省级数据
    }
    /**
     * 查询全国所有的省，优先从数据库查询，没查到在从服务器上查询
     */
    private void queryProvinces() {
        provinceList=chinaWeatherDb.loadProvince();
        if(provinceList.size()>0){
            datalist.clear();
            adapter.notifyDataSetChanged();//刷新Item内容
            for(Province province:provinceList){
                datalist.add(province.getProvinceName());
            }
            listView.setSelection(0);
            titleText.setText("中国");
        }else{
            queryFromServer(null,"province");
        }
        currentLevel=LEVEL_PROVINCE;
       // Toast.makeText(this, "当前页面"+currentLevel, Toast.LENGTH_SHORT).show();
    }

    /**
     * 查询选中省内的所有市，优先从数据库查询，没查到在从服务器上查询
     */
    private void queryCities() {
        cityList=chinaWeatherDb.loadCity(selectedProvince.getId());//根据省份Id获取市级数据表
        if(cityList.size()>0){
            datalist.clear();
            adapter.notifyDataSetChanged();//刷新Item内容
            for(City city:cityList){
                datalist.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();//刷新Item内容
            listView.setSelection(0);//将列表移动到指定的position处
            titleText.setText(selectedProvince.getProvinceName());
        }else{
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
        currentLevel=LEVEL_CITY;
        //Toast.makeText(this, "当前页面"+currentLevel, Toast.LENGTH_SHORT).show();
    }
    /**
     * 查询选中市内的所有县，优先从数据库查询，没查到在从服务器上查询
     */
    private void queryCounties() {
        countyList=chinaWeatherDb.loadCounty(selectedCity.getId());//根据市级Id获取县级数据表
        if(countyList.size()>0){
            datalist.clear();
            adapter.notifyDataSetChanged();//刷新Item内容
            for(County county:countyList){
                datalist.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();//刷新Item内容
            listView.setSelection(0);//将列表移动到指定的position处
            titleText.setText(selectedCity.getCityName());
        }else{
            queryFromServer(selectedCity.getCityCode(),"county");
        }
        currentLevel=LEVEL_COUNTY;
        //Toast.makeText(this, "当前页面"+currentLevel, Toast.LENGTH_SHORT).show();
    }


    /**
     * 根据传入的代号和类型从服务器上获取省市县数据
     */
    private void queryFromServer( String code,final String type) {
        String address;
        if(!TextUtils.isEmpty(code)){
            address="http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else{
            address="http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;
                if("province".equals(type)){
                    result= Utility.handleProvinceResponse(chinaWeatherDb,response);
                }else if("city".equals(type)){
                    result=Utility.handleCityResponse(chinaWeatherDb,response,selectedProvince.getId());
                }else if("county".equals(type)){
                    result=Utility.handleCountryResponse(chinaWeatherDb,response,selectedCity.getId());
                }
                if(result){
                    //通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    /**
     * 捕获back键，判断返回那个界面
     */
    @Override
    public void onBackPressed() {
        if(currentLevel==LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel==LEVEL_CITY){
            queryProvinces();
        }else{
            finish();
        }
    }

}
