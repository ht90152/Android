package com.user.elderlyscan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 2016/2/16.
 */
public class ScanActivity extends AppCompatActivity{
    // 用來搜尋、管理藍芽裝置
//    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

    }
}
