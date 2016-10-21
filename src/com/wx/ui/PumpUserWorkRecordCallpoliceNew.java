package com.wx.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.wx.custom.PumpUserWorkRecordCallpoliceAdapter;
import com.wx.db.Constant;
import com.wx.impl.WNPumpImpl;
import com.wx.pumptest.R;
import com.wx.utils.WNBleControl;
import com.wx.utils.WNHexChange;
import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.BleGattService;
import com.xtremeprog.sdk.ble.BleService;

public class PumpUserWorkRecordCallpoliceNew extends BaseActivity {
	private String resultData;
	private List<String> datas = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pump_user_work_record_callpolice);
		activity = PumpUserWorkRecordCallpoliceNew.this;
		back(R.string.p_alerm_record);
		initClick();
	}

	private void initClick() {
		Button synchroBtn = (Button) findViewById(R.id.pump_alarm_synchro_btn);
		synchroBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				datas.clear();
				BluetoothAdapter adpter = BluetoothAdapter.getDefaultAdapter();
				if (adpter.isEnabled() == false) {
					adpter.enable();
				} else {
					dialogUtil.showDialogLoading(true);
					WNBleControl.getInstance(
							PumpUserWorkRecordCallpoliceNew.this, mBle)
							.connect(userSP.getPumpMac());
				}
			}
		});
	}

	private void initList() {
		ListView pump_user_work_record_callpolic_list = (ListView) findViewById(R.id.pump_user_work_record_callpolic_list);
		pump_user_work_record_callpolic_list
				.setAdapter(new PumpUserWorkRecordCallpoliceAdapter(this, datas));
	}

	// 泵数据同步后
	private void initView() {
		WNBleControl.getInstance(this, mBle).disConnect(userSP.getPumpMac());
		dialogUtil.showDialogLoading(true);
		initList();
	}

	/*
	 * @wx 蓝牙操作部分
	 */
	@Override
	protected void onResume() {
		registerReceiver(mBleReceiverConnect, BleService.getIntentFilter());
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	protected void onPause() {
		WNBleControl.getInstance(this, mBle).disConnect(userSP.getPumpMac());
		unregisterReceiver(mBleReceiverConnect);
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		System.out.println("disConnect***" + userSP.getPumpMac());
		WNBleControl.getInstance(this, mBle).disConnect(userSP.getPumpMac());
		Intent intent = new Intent(this, BleService.class);
		stopService(intent);
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constant.NITIFY_FAIL:
				Toast.makeText(activity, "无法获得泵服务特性,请用连接工具检验泵是否正常",
						Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		};
	};
	private final BroadcastReceiver mBleReceiverConnect = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			// if (!extras.getString(BleService.EXTRA_ADDR).equals(
			// userSP.getPumpMAC())) {
			// return;
			// }
			if (!userSP.getPumpMac().equals(
					extras.getString(BleService.EXTRA_ADDR))) {
				return;
			}
			String action = intent.getAction();
			if (BleService.BLE_GATT_CONNECTED.equals(action)) {
				System.out.println("BLE_GATT_CONNECTED***连接");
			} else if (BleService.BLE_GATT_DISCONNECTED.equals(action)) {
				System.out.println("BLE_GATT_DISCONNECTED***断开连接");
				dialogUtil.showDialogLoading(false);
				Toast.makeText(PumpUserWorkRecordCallpoliceNew.this, "断开连接",
						Toast.LENGTH_SHORT).show();
			}
			String data = intent.getStringExtra(BleService.EXTRA_VALUE);
			System.out.println(data + "***");
			if (data != null) {
				if (!TextUtils.isEmpty(resultData) && resultData.length() > 9) {
					System.out.println("返回数据***" + resultData);
					List<String> checks = WNHexChange.getPumpData(resultData);
					if (checks.get(0).toString().equals("0b")) {
						datas.add(resultData);
						if (checks.get(3).toString().equals("0b")) {
							initView();
							dialogUtil.showDialogLoading(false);
						}
					}
					if (checks.get(2).toString().equals("b2")) {
					}
					if (resultData.substring(6, 8).equals("b3")) {
						Toast.makeText(
								PumpUserWorkRecordCallpoliceNew.this,
								getResources().getString(
										R.string.p_no_transmission),
								Toast.LENGTH_LONG).show();
					}
					if (resultData.substring(6, 8).equals("b4")) {
						Toast.makeText(PumpUserWorkRecordCallpoliceNew.this,
								getResources().getString(R.string.p_norecord),
								Toast.LENGTH_LONG).show();
					}

					if (resultData.substring(6, 8).equals("b5")) {
						Toast.makeText(
								PumpUserWorkRecordCallpoliceNew.this,
								getResources().getString(
										R.string.p_no_day_record),
								Toast.LENGTH_LONG).show();
					}

					if (resultData.substring(6, 8).equals("b6")) {
						Toast.makeText(
								PumpUserWorkRecordCallpoliceNew.this,
								getResources().getString(
										R.string.p_no_alerm_record),
								Toast.LENGTH_LONG).show();
					}
				}
			}
			String uuid = extras.getString(BleService.EXTRA_UUID);
			if (uuid != null
					&& !WNBleControl.UUID_NOTIFY.toString().equals(uuid)) {
				return;
			}
			if (BleService.BLE_CHARACTERISTIC_NOTIFICATION.equals(action)) {
				System.out.println("BLE_CHARACTERISTIC_NOTIFICATION***开启通知");
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(1000);
							WNBleControl.getInstance(
									PumpUserWorkRecordCallpoliceNew.this, mBle)
									.toWrite(userSP.getPumpMac(),
											Constant.PUMP_ALARM_RECORD);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
			} else if (BleService.BLE_CHARACTERISTIC_WRITE.equals(action)) {
				System.out.println("BLE_CHARACTERISTIC_WRITE***写入指令");
			} else if (BleService.BLE_SERVICE_DISCOVERED.equals(action)) {
				System.out.println("BLE_SERVICE_DISCOVERED***获得服务");
				System.out.println("uuid***" + uuid);
				displayGattServices(WNBleControl.getInstance(
						PumpUserWorkRecordCallpoliceNew.this, mBle)
						.getGattServices(userSP.getPumpMac()));
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(1000);
							if (WNPumpImpl.startNotify(activity, mBle, userSP) == false) {
								dialogUtil.showDialogLoading(false);
								handler.sendEmptyMessage(Constant.NITIFY_FAIL);
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		}
	};
	private ArrayList<ArrayList<BleGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BleGattCharacteristic>>();
	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";

	private void displayGattServices(List<BleGattService> gattServices) {
		if (gattServices == null)
			return;
		// String uuid = null;
		String unknownServiceString = getResources().getString(
				R.string.unknown_service);
		String unknownCharaString = getResources().getString(
				R.string.unknown_characteristic);
		ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
		ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
		mGattCharacteristics = new ArrayList<ArrayList<BleGattCharacteristic>>();

		// Loops through available GATT Services.
		for (BleGattService gattService : gattServices) {
			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			// uuid = gattService.getUuid().toString().toUpperCase();

			currentServiceData
					.put(LIST_NAME,
							WNUtils.BLE_SERVICES
									.containsKey(WNBleControl.UUID_SERVICE) ? WNUtils.BLE_SERVICES
									.get(WNBleControl.UUID_SERVICE)
									: unknownServiceString);
			currentServiceData.put(LIST_UUID, WNBleControl.UUID_SERVICE);
			gattServiceData.add(currentServiceData);

			ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
			List<BleGattCharacteristic> gattCharacteristics = gattService
					.getCharacteristics();
			ArrayList<BleGattCharacteristic> charas = new ArrayList<BleGattCharacteristic>();

			// Loops through available Characteristics.
			for (BleGattCharacteristic gattCharacteristic : gattCharacteristics) {
				charas.add(gattCharacteristic);
				HashMap<String, String> currentCharaData = new HashMap<String, String>();
				// uuid = gattCharacteristic.getUuid().toString().toUpperCase();
				currentCharaData
						.put(LIST_NAME,
								WNUtils.BLE_CHARACTERISTICS
										.containsKey(WNBleControl.UUID_NOTIFY) ? WNUtils.BLE_CHARACTERISTICS
										.get(WNBleControl.UUID_NOTIFY)
										: unknownCharaString);
				currentCharaData.put(LIST_UUID, WNBleControl.UUID_NOTIFY);
				gattCharacteristicGroupData.add(currentCharaData);
			}
			mGattCharacteristics.add(charas);
			gattCharacteristicData.add(gattCharacteristicGroupData);
		}
	}
}
