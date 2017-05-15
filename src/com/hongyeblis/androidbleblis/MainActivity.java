package com.hongyeblis.androidbleblis;

import android.app.ActivityManager;
import com.hongyeblis.androidbleblis.belhandle.BLEHandle;
import com.hongyeblis.androidbleblis.bleslistener.ScanBlutoothDvicesListener;
import com.hongyeblis.androidbleblis.bleslistener.CountDownTimeListener;
import com.hongyeblis.androidbleblis.bleslistener.WriteDataListener;
import com.hongyeblis.androidbleblis.bleslistener.GetServicesCountListener;
import com.hongyeblis.androidbleblis.timeTask.TimeTask;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.util.List;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button btnEnabled = (Button) findViewById(R.id.btnEnabled);

		Button btnScan = (Button) findViewById(R.id.btnScan);
		
		Button btnStopScan = (Button) findViewById(R.id.btnStopScan);

		Button btnConnect = (Button) findViewById(R.id.btnConnect);

		Button btndisconnect = (Button) findViewById(R.id.btnDisConnect);//
		
		Button btnBinding = (Button) findViewById(R.id.btnBinding);
		
		Button unBinding = (Button) findViewById(R.id.btnUnBinding);
		
		Button btnWeite = (Button) findViewById(R.id.btnWeite);

		Button selectService = (Button) findViewById(R.id.selectServices);

		btnEnabled.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					// TODO Auto-generated method stub
					BLEHandle handle =BLEHandle.getinstance();
					boolean enabled = handle.bleEnabled(MainActivity.this);
					System.out.println("蓝牙是否打开=="+enabled);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		btnScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final BLEHandle handle = BLEHandle.getinstance();
				handle.scanBlutoothDevices(MainActivity.this, dvicesCallBack,null);
				TimeTask.startCount(123456, 8000, new CountDownTimeListener() {
					@Override
					public void countTimeEnd(int tag) {
						// TODO Auto-generated method stub
						System.out.println("============countTimeEnd时间到===="+tag);
						handle.stopLeScan(MainActivity.this);
					}
				});
			}
		});

		btnStopScan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			BLEHandle.getinstance().stopLeScan(MainActivity.this);	
			}
		});
		btnConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BLEHandle.getinstance().stopLeScan(MainActivity.this);
				BLEHandle.getinstance().countBlutooth("20:91:48:B3:BA:3B",new GetServicesCountListener() {
					
					@Override
					public void getServiceSuccess() {
						// TODO Auto-generated method stub
						System.out.println("=========链接成功");
					}
					
					@Override
					public void getServiceFailure() {
						// TODO Auto-generated method stub
						System.out.println("=========链接失败");
						
					}

					@Override
					public void countDisconnectException() {
						System.out.println("=========链接中断");
					}
				});
			}
		});

		btndisconnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BLEHandle.getinstance().disConnect();
			}
		});
		
		btnBinding.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean services = BLEHandle.getinstance().BindingServices(MainActivity.this);
				System.out.println("BindingServices====="+services);
			}
		});
		
		unBinding.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BLEHandle.getinstance().unBindingServices(MainActivity.this);
			}
		});
		
		btnWeite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					// TODO Auto-generated method stub
					byte[] bytes = "AA00FB00010000".getBytes();
					BLEHandle.getinstance().writeCharacteristic(bytes,new WriteDataListener() {
						
						@Override
						public void notificationData(byte[] data) {
							// TODO Auto-generated method stub
							System.out.println("收到的数据=="+ByteTransform.byte2HexStr(data));
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		selectService.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BLEHandle handle = BLEHandle.getinstance();
				List<ActivityManager.RunningServiceInfo> serviceList = handle.getServiceList(MainActivity.this,100);
				System.out.println("活动的servides=="+serviceList);
				System.out.println("活动的servides=="+serviceList.size());
				for (ActivityManager.RunningServiceInfo serviceInfo: serviceList){
					if ("com.hongyeblis.androidbleblis.belservice.BluetoothLeService".equals(serviceInfo.service.getClassName())){
						System.out.println("com.hongyeblis.androidbleblis.belservice.BluetoothLeService=========================");
					}
				}
			}
		});
	}

	private ScanBlutoothDvicesListener dvicesCallBack = new ScanBlutoothDvicesListener() {

		@Override
		public void BlutoothDevicesListener(BluetoothDevice mDevice, int rssi, byte[] record) {
			System.out.println("扫描到的设备"+mDevice.getAddress());
		}
	};
}
