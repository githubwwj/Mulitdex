package com.wwj.mulitdex;

import android.app.Application;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/6/5 0005.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Toast.makeText(App.this, "程序遇到错误:" + ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        });
    }
}
