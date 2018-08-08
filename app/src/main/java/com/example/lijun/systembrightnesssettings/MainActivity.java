package com.example.lijun.systembrightnesssettings;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Button mAdjBrightness;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdjBrightness = findViewById(R.id.adj_brightness);

        mAdjBrightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int state = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
                if(state == 1){
                    Log.e(TAG, "设置自动亮度的值 ");
                    Settings.Secure.putFloat(getContentResolver(), "screen_auto_brightness_adj",
                            1);
                }else{
                    Log.e(TAG, "设置手动亮度的值 ");
                    Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                            0);
                }
            }
        });

        //获取各个手机的系统设置Uri
        Uri uri = Uri.parse("content://settings");
        /**
         * 华为手机(自动调节亮度的Uri): content://settings/system/screen_auto_brightness
         * 华为手机(手动调节亮度的Uri): content://settings/system/screen_brightness
         * 原生手机(自动调节亮度的Uri): content://settings/system/screen_auto_brightness_adj
         */

//        float brightness = Settings.Secure.getFloat(getContentResolver(), "brightness_pms_marker_screen", -2);
//        Log.e(TAG, "获取自动亮度值== " + brightness);

        //获取自动亮度模式下的Uri
        Uri uriAuto = Settings.Secure.getUriFor("brightness_pms_marker_screen");
//        //获取手动亮度模式下的Uri
        Uri uriManual = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);

        getContentResolver().registerContentObserver(uri, true, mContentObserver);
        getContentResolver().registerContentObserver(uriAuto, false, mContentObserverAuto);
        getContentResolver().registerContentObserver(uriManual, false, mContentObserverManual);
    }
    private Handler mHandler = new Handler(Looper.myLooper());

    private ContentObserver mContentObserver = new ContentObserver(mHandler) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Log.e(TAG, "获取各个手机的Uri== " + uri.toString());
        }
    };

    //获取系统自动亮度模式下的亮度值
    private ContentObserver mContentObserverAuto = new ContentObserver(mHandler) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            float brightness = Settings.Secure.getFloat(getContentResolver(), "brightness_pms_marker_screen", -2);
            Log.e(TAG, "获取自动亮度值== " + brightness);
        }
    };

    //获取手动模式下的亮度值
    private ContentObserver mContentObserverManual = new ContentObserver(mHandler) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            int brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1);
            Log.e(TAG, "获取手动模式下的亮度值== " + brightness);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mContentObserver);
        getContentResolver().unregisterContentObserver(mContentObserverAuto);
        getContentResolver().unregisterContentObserver(mContentObserverManual);
    }
}
