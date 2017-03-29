package com.reliance.myeventbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private Object a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        Log.e("thread",Thread.currentThread().getName());
                        EventBus.getDefault().post("测试");
                    }
                }.start();
            }
        });
        EventBus.getDefault().regist(this);

    }

    @SubscribeThread(Thread = ThreadModel.PostThead)
    void onReceiveMessage(String message) {
        String name = Thread.currentThread().getName();
        Log.e("name", "Threadname" + name);
        Log.e("onReceiveMessage", message);
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregist(this);
        super.onDestroy();

    }
}
