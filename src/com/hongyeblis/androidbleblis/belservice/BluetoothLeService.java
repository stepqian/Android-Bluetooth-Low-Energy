package com.hongyeblis.androidbleblis.belservice;

import java.util.List;
import java.util.UUID;

import com.hongyeblis.androidbleblis.bleslistener.BluetoothCountListener;
import com.hongyeblis.androidbleblis.bleslistener.WriteDataListener;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class BluetoothLeService extends Service{
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	private BluetoothGatt mBluetoothGatt;
	private BluetoothCountListener mBLECount;
	private WriteDataListener mBLEWriteData;
	private boolean handle = false;
	public  UUID NOTIFICATION = null;

	/**
	 * 蓝牙数据回调接口
	 */
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				if(mBluetoothGatt!=null)
				{
					mBluetoothGatt.discoverServices();
				}
				if(mBLECount != null){
					mBLECount.onConnected();
				}
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				if(mBLECount != null && !handle && mBluetoothGatt != null){
					mBLECount.onChangeStateDisconnected();
				}
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				if(mBLECount !=null){
					mBLECount.onServicesDiscovered(getSupportedGattServices());
				}
			}
		}

		/**
		 * 读到数据
		 */
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			// 读取到值，在这里读数据
			if (status == BluetoothGatt.GATT_SUCCESS) {
				System.out.println("BLE============03====");
			}
		}

		/**
		 * 一旦消息发变化就会调用该方法
		 */
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			if(mBLEWriteData !=null && NOTIFICATION.equals(characteristic.getUuid())){
				mBLEWriteData.notificationData(characteristic.getValue());
			}
		}
	};

	public class LocalBinder extends Binder {
		public BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		close();
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	/**
	 * 初始化蓝牙
	 * @return
	 */
	private boolean initialize() {
		BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		if (mBluetoothManager == null) {
			return false;
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			System.out.println("BLE====mBluetoothAdapter==NULL=====");
			return false;
		}
		return true;
	}

	/**
	 * 链接蓝牙设备
	 * @param address
	 * @param mCount
	 * @return
	 */
	public void connect(final String address,BluetoothCountListener mCount) {
		this.mBLECount = mCount;
		handle = false;
		// Previously connected device. Try to reconnect.
		if (address.equals(mBluetoothDeviceAddress)) {
			disconnect();
		}

		if (mBluetoothAdapter == null || address == null) {
			initialize();
			if(mBluetoothAdapter == null){
				if(mBLECount != null){
					mBLECount.onConnectionFailed();
					return;
				}
			};
		}

		final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		if (device == null) {
			if(mBLECount != null){
				mBLECount.onConnectionFailed();
			}
			return;
		}

		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);// 连接

		mBluetoothDeviceAddress = address;

	}


	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		}
		if(mBluetoothGatt!=null)mBluetoothGatt.disconnect();
		handle = true;
		mBLEWriteData = null;
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		if (mBluetoothGatt != null) {
			mBluetoothGatt.close();
			handle = true;
			mBLEWriteData = null;
		}
		mBluetoothGatt = null;
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 *
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			System.out.println("BLE======06==============");
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	/**
	 * writeData
	 *
	 * @param characteristic
	 */
	public void writeCharacteristic(BluetoothGattCharacteristic characteristic,WriteDataListener mWriteData) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		}
		this.mBLEWriteData = mWriteData;
		mBluetoothGatt.writeCharacteristic(characteristic);
	}

	/**
	 * Enables or disables notification on a give characteristic.
	 *
	 * @param characteristic
	 *            Characteristic to act on.
	 * @param enabled
	 *            If true, enable notification. False otherwise.
	 */
	public boolean setCharacteristicNotification(
			BluetoothGattCharacteristic characteristic, boolean enabled) {
		this.NOTIFICATION = characteristic.getUuid();
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return false;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
		return true;
	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This
	 * should be invoked only after {@code BluetoothGatt#discoverServices()}
	 * completes successfully.
	 * @return A {@code List} of supported services.
	 */
	private List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null)
			return null;
		return mBluetoothGatt.getServices();
	}
}
