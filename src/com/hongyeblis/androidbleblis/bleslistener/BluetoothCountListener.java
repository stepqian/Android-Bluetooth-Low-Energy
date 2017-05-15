package com.hongyeblis.androidbleblis.bleslistener;

import java.util.List;

import android.bluetooth.BluetoothGattService;

public interface BluetoothCountListener {

	/**
	 * 蓝牙链接中断
	 */
	void onChangeStateDisconnected();

	/**
	 * 回调时调用远程服务的列表,特性和描述符的远程设备更新,即发现了新的服务
	 * @param gattServices
	 */
	void onServicesDiscovered(List<BluetoothGattService> gattServices);

	/**
	 * 蓝牙连接成功
	 */
	void onConnected();

	/**
	 * 蓝牙连接失败
	 */
	void onConnectionFailed();
 }