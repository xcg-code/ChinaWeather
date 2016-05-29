package com.app.chinaweather.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.chinaweather.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by 14501_000 on 2016/5/26.
 */
public class MainActivity extends Activity {
    private Button bt;
    private TextView textView;
    StringBuilder response;
    String weatherCode;
    Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0x123){
                textView.setText(msg.obj.toString());
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        bt= (Button) findViewById(R.id.get_info);
        textView= (TextView) findViewById(R.id.show);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpURLConnection connection=null;
                        try {
                            URL url=new URL(" http://www.weather.com.cn/data/list3/city040104.xml");
                            connection=(HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            //connection.setRequestProperty("apikey",  "f14d742ae9a295c4cb69cd4c8edb02eb");
                            connection.connect();
                            connection.setReadTimeout(8000);
                            connection.setReadTimeout(8000);
                            InputStream inputStream= connection.getInputStream();
                            BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                           response=new StringBuilder();
                            String line;
                            while((line=reader.readLine())!=null){
                                response.append(line);
                            }
                            if(response==null){
                                Toast.makeText(MainActivity.this,"neirongweikong",Toast.LENGTH_SHORT).show();
                            }
                            String res=response.toString();
                            String[] array=res.split("\\|");
                            if(array!=null&&array.length==2){
                               weatherCode=array[1];
                            }
                            Message message=new Message();
                            message.what=0x123;
                            message.obj=weatherCode;
                            handler.sendMessage(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });
    }
}
