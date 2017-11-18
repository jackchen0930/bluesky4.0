package com.tongyong.ct.bluesky;

import android.app.Application;

/**
 * Created by Chentao on 15/12/22.
 */
public class BlueSkyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /*AlibabaSDK.asyncInit(this, new InitResultCallback() {

            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "初始化成功", Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onFailure(int code, String message) {
                Toast.makeText(MainActivity.this, "初始化异常", Toast.LENGTH_SHORT)
                        .show();
            }

        });*/

    }
}
