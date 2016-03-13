package com.user.elderlyscan;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final static int NOTIFICATION_ID = 0;
    private NotificationManager notificationManager;
    // 用來搜尋、管理藍芽裝置
    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //確認是否已註冊過裝置
        /*if (checkPlayServices())
        {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty())
            {
                registerInBackground();
            }
        }
        else
        {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }*/


        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // get the default local bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Button btnMessage = (Button)findViewById(R.id.btnMessage);
        Button btnScan = (Button)findViewById(R.id.btnScan);
        btnMessage.setOnClickListener(uploadMessage);
        btnScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                scanBT();
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        unregisterReceiver(mReceiver);
    }

    // 輸入失蹤者、聯絡人資料
    private View.OnClickListener uploadMessage = new View.OnClickListener() {
        public void onClick(View v) {
            //即時通報
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            View urgentView = inflater.inflate(R.layout.dialog_urgent, null);
            final EditText nameText = (EditText) urgentView.findViewById(R.id.nameText);
            final EditText phoneText = (EditText) urgentView.findViewById(R.id.phoneText);
            final EditText noteText = (EditText) urgentView.findViewById(R.id.noteText);

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("請輸入聯絡人資料")
                    .setView(urgentView)
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            return;
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    })
                    .show();
        }
    };

    private View.OnClickListener openBT = new View.OnClickListener() {
        public void onClick(View v) {
            /*
            notificationSend("Urgent message!", "The elderly lost!");

            // dialog of Bluetooth open or not
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle(R.string.bt_title);
            dialog.setMessage(R.string.bt_message);
            dialog.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // 當使用者點擊『ＯＫ』按鈕時要做的事情
                            // 如果裝置支援藍芽
                            if (mBluetoothAdapter != null) {
                                // 如果藍芽沒有開啟
                                if (!mBluetoothAdapter.isEnabled()) {
                                    mBluetoothAdapter.enable();
                                }
                            }
                        }
                    });
            dialog.setNegativeButton(android.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // 當使用者點擊『cancel』按鈕時要做的事情
                        }
                    });
            dialog.show();*/
        }
    };

    private void scanBT() {
        mBluetoothAdapter.enable();
        SystemClock.sleep(1000*5);

        // register the BroadcastReceiver
        registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        // 開始搜尋裝置
        mBluetoothAdapter.startDiscovery();
    }

    // create a broadcastReceiver for ACTION_FOUND and ACTION_DISCOVERY_FINISHED
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // when discovery finds a device//當收尋到裝置時
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // get the BluetoothDevice object from Intent//取得藍芽裝置這個物件
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

//                Toast.makeText(MainActivity.this, device.getAddress().toString(), Toast.LENGTH_SHORT).show();
                if(device.getAddress().equals("C4:00:00:D4:43:D9")){
//                    Toast.makeText(MainActivity.this, "Find the elderly!!", Toast.LENGTH_SHORT).show();
                    notificationSend("Find the elderly!!", "The elderly is near!!");
                }
            }
        }
    };

    // 用notification通知使用者找到失蹤者
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void notificationSend(String title, String content) {
        Intent notifyIntent = new Intent(this, ScanActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("content", content);
        notifyIntent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setTicker("You got a urgent message.")// ticker text no longer display in Android 5.0
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE) //Vibration
                .build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.close:
                //不詢問關閉藍牙
                if(mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.disable();
                    Toast.makeText(MainActivity.this, "已關閉藍芽", Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
            // noinspection SimplifiableIfStatement
            /*case R.id.action_settings:
                return true;*/
        }

        return super.onOptionsItemSelected(item);
    }
}
