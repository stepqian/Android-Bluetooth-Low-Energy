package com.hongyeblis.androidbleblis.bleslistener;

public interface GetServicesCountListener {

	/**
	 * 手机过去蓝牙设备服务项目成功
	 */
	void getServiceSuccess();

	/**
	 * 手机过去蓝牙设备服务项目失败
	 */
	void getServiceFailure();

	/**
	 * 异常中断
	 */
	void countDisconnectException();
}
