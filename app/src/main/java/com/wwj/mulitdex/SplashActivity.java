package com.wwj.mulitdex;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Administrator on 2018/6/5 0005.
 */

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if(checkPermission()){
            FixBugUtil.moveFileToFlashDisk(this);
            FixBugUtil.fixBug(this);
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        //判断当前Activity是否已经获得了该权限
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                || !(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                ) {
            requestSDCardPermission();
            return false;
        } else {
            return true;
        }
    }

    private static final int SDCARD_CODE = 23;

    private void requestSDCardPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, SDCARD_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SDCARD_CODE) {
            if (null != grantResults && grantResults.length > 0) {
                boolean granted = true;
                for (int g = 0; g < grantResults.length; g++) {
                    if (grantResults[g] != PackageManager.PERMISSION_GRANTED) {
                        granted = false;
                        break;
                    }
                }
                if (!granted) {
                    checkPermission();

                }else{
                    Intent intent=new Intent(this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }
}
