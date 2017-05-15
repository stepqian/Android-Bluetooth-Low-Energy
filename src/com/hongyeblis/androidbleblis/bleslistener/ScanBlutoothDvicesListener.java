package com.hongyeblis.androidbleblis.bleslistener;

import android.bluetooth.BluetoothDevice;

public interface ScanBlutoothDvicesListener {

	/**
	 * 蓝牙扫描的监听返回事件
	 * @param mDevice 返回扫描到的蓝牙标识远程设备
	 * @param rssi 由蓝牙硬件报告的远程设备的RSSI值。 如果没有RSSI值可用，则为0。
	 * @param record 远程设备提供的广告记录的内容。
	 */
	void BlutoothDevicesListener(BluetoothDevice mDevice,int rssi,byte[] record);

}
