package com.hongyeblis.androidbleblis.timeTask;

import com.hongyeblis.androidbleblis.bleslistener.CountDownTimeListener;

import android.os.CountDownTimer;
import android.util.SparseArray;

public class TimeTask extends CountDownTimer{

	CountDownTimeListener downTime = null;

	private int tag;

	private static SparseArray<TimeTask>sparseTimeTasks = new SparseArray<TimeTask>();

	public TimeTask(int tag,long millisInFuture, long countDownInterval,CountDownTimeListener downTime) {
		super(millisInFuture, countDownInterval);
		// TODO Auto-generated constructor stub
		this.tag = tag;
		this.downTime = downTime;
	}

	@Override
	public void onTick(long millisUntilFinished) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
		downTime.countTimeEnd(tag);
	}

	/**
	 * 启动计时
	 * @param tag 计时唯一标识
	 */
	public static void startCount(int tag,long millisInFuture,CountDownTimeListener downTime){
		TimeTask timeTask = new TimeTask(tag,millisInFuture, 500,downTime);
		timeTask.start();
		sparseTimeTasks.put(tag, timeTask);
	}

	/**
	 * 停止计时
	 * @param tag 计时唯一标识
	 */
	public static void stopCount(int tag){
		TimeTask timeTask = sparseTimeTasks.get(tag);
		if(timeTask!=null){
			timeTask.cancel();
			sparseTimeTasks.remove(tag);
		};
	}
}
