package com.hongyeblis.androidbleblis.belhandle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.util.Log;
import com.hongyeblis.androidbleblis.belservice.BluetoothLeService;
import com.hongyeblis.androidbleblis.bleslistener.ScanBlutoothDvicesListener;
import com.hongyeblis.androidbleblis.bleslistener.BluetoothCountListener;
import com.hongyeblis.androidbleblis.bleslistener.WriteDataListener;
import com.hongyeblis.androidbleblis.bleslistener.GetServicesCountListener;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;

/**
 * 此类是整个操作的一个对象
 *
 * @author 红叶岭谷
 */
public class BLEHandle {

    private static final String TAG = "BLEHandle";

    private BluetoothManager bluetoothManager;

    private Intent mIntent = null;

    private BluetoothLeService bluetoothLeService;

    private BlutoothServiceConnection connection;

    private BluetoothGattCharacteristic mGattCharacteristic;

    private String writeUUID = "0000ff11-0000-1000-8000-00805f9b34fb";

    private String notifitionUUID = "0000ff12-0000-1000-8000-00805f9b34fb";

    private long slp = 50;

    // 指定的扫描AMC
    private String AimsMAC = null;

    private List<BluetoothDevice> mListbledev;

    private static BLEHandle instance;

    // 返回扫描的蓝牙接口
    private ScanBlutoothDvicesListener blutoothDvices;

    private BLEHandle() {
    }

    ;

    /**
     * 获取蓝牙操作的对象
     *
     * @return
     */
    public static synchronized BLEHandle getinstance() {
        if (instance == null) {
            instance = new BLEHandle();
        }
        return instance;
    }

    /**
     * 设置蓝牙的每包发送间隔
     *
     * @param times 蓝牙每包数据的发送间隔时间
     * @return
     */
    public BLEHandle setBLEitems(long times) {
        this.slp = times;
        return instance;
    }

    /**
     * 设置书写uuid
     *
     * @param uuid 蓝牙的可写的UUID
     * @return
     */
    public BLEHandle setwriteUUID(String uuid) {
        this.writeUUID = uuid;
        return instance;
    }

    /**
     * 设置通知uuid
     *
     * @param uuid 接收的UUID
     * @return
     */
    public BLEHandle setnotifitionUUID(String uuid) {
        this.notifitionUUID = uuid;
        return instance;
    }

    /**
     * 查看蓝牙是否开启
     *
     * @param mContext
     * @return
     */
    public boolean bleEnabled(Context mContext) {

        // Ensures Bluetooth is available on the device and it is enabled. If
        // not,
        // displays a dialog requesting user permission to enable Bluetooth.
        BluetoothAdapter mBluetoothAdapter = getBluetoothAdapter(mContext);
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            return false;
        }
        return true;
    }

    /**
     * 获取蓝牙管理器
     *
     * @return
     */
    private BluetoothAdapter getBluetoothAdapter(Context mContext) {

        // Initializes Bluetooth adapter.
        if (bluetoothManager == null) {
            bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        }
        return bluetoothManager.getAdapter();
    }

    /**
     * 判断APP是否支持低功耗蓝牙
     *
     * @param mContext
     * @return
     */
    public boolean supported(Context mContext) {

        // Use this check to determine whether BLE is supported on the device.
        // Then
        // you can selectively disable BLE-related features.
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        return true;
    }

    /**
     * 扫描周围低功耗蓝牙设备
     * @param mContext
     * @param blutoothCall 蓝牙扫描监听事件回调接口
     * @param mac 指定的蓝牙Mac地址,可以为空null,如果为null那就是扫描所有的
     */
    public void scanBlutoothDevices(final Context mContext, ScanBlutoothDvicesListener blutoothCall, String mac) {
        this.blutoothDvices = blutoothCall;
        this.AimsMAC = mac;
        if (mListbledev == null) {
            mListbledev = new ArrayList<BluetoothDevice>();
        } else {
            mListbledev.clear();
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                getBluetoothAdapter(mContext).startLeScan(mScanCallback);
            }
        }).start();

    }

    /**
     * 停止蓝牙扫描
     *
     * @param mContext
     * @return
     */

    public void stopLeScan(final Context mContext) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                getBluetoothAdapter(mContext).stopLeScan(mScanCallback);
            }
        }).start();
    }

    /**
     * 链接蓝牙设备
     *
     * @param bluetoothMAC 蓝牙链接回调接口
     * @return
     */
    public void countBlutooth(String bluetoothMAC, final GetServicesCountListener mCountCallBack) {
        if (bluetoothLeService != null) {
            bluetoothLeService.connect(bluetoothMAC, new BluetoothCountListener() {
                @Override
                public void onServicesDiscovered(List<BluetoothGattService> gattServices) {
                    // TODO Auto-generated method stub
                    for (BluetoothGattService bluetoothGattService : gattServices) {
                        List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
                        for (BluetoothGattCharacteristic bluetoothGattCharacteristic : characteristics) {
                            String uuid = bluetoothGattCharacteristic.getUuid().toString();
                            if (writeUUID.equalsIgnoreCase(uuid)) {
                                mGattCharacteristic = bluetoothGattCharacteristic;
                            } else if (notifitionUUID.equalsIgnoreCase(uuid)) {
                                bluetoothLeService.setCharacteristicNotification(bluetoothGattCharacteristic, true);
                            }
                        }
                    }

                    try {
                        Thread.sleep(slp);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mCountCallBack.getServiceSuccess();
                }

                @Override
                public void onChangeStateDisconnected() {
                    // TODO Auto-generated method stub
                    mCountCallBack.countDisconnectException();
                }

                @Override
                public void onConnected() {
                    // TODO Auto-generated method stub
                    Log.d(TAG, "onConnected : 蓝牙链接成功,等待获取服务特征值========");
                }

                @Override
                public void onConnectionFailed() {
                    // TODO Auto-generated method stub
                    mCountCallBack.getServiceFailure();
                }
            });
        }
    }

    /**
     * 断开蓝牙链接
     */
    public void disConnect() {
        if (bluetoothLeService != null) {
            bluetoothLeService.disconnect();
        }
    }

    /**
     * 蓝牙链接关闭
     */
    public void closeConnect() {
        if (bluetoothLeService != null) {
            bluetoothLeService.close();
        }
    }

    /**
     * 蓝牙扫描的回调接口
     */
    private BluetoothAdapter.LeScanCallback mScanCallback = new LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            // TODO Auto-generated method stub
            if (blutoothDvices != null && mListbledev != null && !mListbledev.contains(device)) {
                mListbledev.add(device);
                if (AimsMAC != null) {
                    if (AimsMAC.equalsIgnoreCase(device.getAddress())) {
                        blutoothDvices.BlutoothDevicesListener(device, rssi, scanRecord);
                    }
                } else {
                    blutoothDvices.BlutoothDevicesListener(device, rssi, scanRecord);
                }
            }
        }
    };

    /**
     * 绑定蓝牙Services
     *
     * @param mContext
     * @return
     */
    public boolean BindingServices(Context mContext) {
        if (mIntent == null) {
            mIntent = new Intent(mContext, BluetoothLeService.class);
            if (mContext.bindService(mIntent, new BlutoothServiceConnection(), Context.BIND_AUTO_CREATE)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 解除Service绑定
     *
     * @param mContext
     */
    public void unBindingServices(Context mContext) {
        if (connection != null)
            mContext.unbindService(connection);
        if (mIntent != null && mContext != null) mContext.getApplicationContext().stopService(mIntent);
        mIntent = null;
    }

    /**
     * 数据的写入
     * @param data 数据
     * @param mWriteData 写入的回调返回接口
     * @throws IOException
     */
    public void writeCharacteristic(final byte[] data, final WriteDataListener mWriteData) throws IOException {
        if (bluetoothLeService != null) {
            if (data != null) {
                if (data.length > 20) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            ByteArrayInputStream input = new ByteArrayInputStream(data);
                            try {
                                // TODO Auto-generated method stub
                                byte[] inpuArray = new byte[20];
                                while (input.read(inpuArray) != -1) {
                                    writeCharData(inpuArray, mWriteData);
                                    inpuArray = new byte[20];
                                    try {
                                        Thread.sleep(slp);
                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                                input.close();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (input != null) {
                                        input.close();
                                    }
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                } else {
                    writeCharData(data, mWriteData);
                }
            }
        }
    }

    private void writeCharData(byte[] inpuArray, WriteDataListener mWriteData) {

        if (mGattCharacteristic != null && bluetoothLeService != null) {
            mGattCharacteristic.setValue(inpuArray);
            bluetoothLeService.writeCharacteristic(mGattCharacteristic, mWriteData);
        } else {
            System.out.println("mGattCharacteristic=" + mGattCharacteristic);
            System.out.println("bluetoothLeService=" + bluetoothLeService);
        }
    }

    class BlutoothServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            Log.d(TAG, "onServiceDisconnected: ======解除绑定=====");
        }
    }

    /**
     * 获取现有的活动的Services列表
     *
     * @param maxNmu   最大集合数量
     * @param mContext 上下文关系
     * @return 活动的Services列表
     */
    public List<ActivityManager.RunningServiceInfo> getServiceList(Context mContext, int maxNmu) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        return manager.getRunningServices(maxNmu);
    }

    /**
     * 查看某一个services是否在活动
     *
     * @param mContext
     * @param servceName services的名称
     * @return
     */
    public boolean selectCheckToSeeActivity(Context mContext, String servceName) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(100);
        if (runningServices.size() < 0) return false;

        int size = runningServices.size();
        for (int i = 0; i < size; i++) {
            if (servceName.equals(runningServices.get(i).service.getClassName())) return true;
        }
        return false;
    }
}
