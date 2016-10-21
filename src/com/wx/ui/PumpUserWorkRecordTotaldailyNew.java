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

import com.wx.custom.PumpUserWorkRecordTotaldailyAdapter;
import com.wx.db.Constant;
import com.wx.impl.WNPumpImpl;
import com.wx.model.DailyRecord;
import com.wx.pumptest.R;
import com.wx.utils.WNBleControl;
import com.wx.utils.WNHexChange;
import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.BleGattService;
import com.xtremeprog.sdk.ble.BleService;

public class PumpUserWorkRecordTotaldailyNew extends BaseActivity {
	private String resultData;
	private List<String> datas = new ArrayList<String>();
	private List<DailyRecord> dailyRecords = new ArrayList<DailyRecord>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pump_user_work_record_totaldaily);
		activity = PumpUserWorkRecordTotaldailyNew.this;
		back(R.string.p_days_record);
		initClick();
	}

	private void initClick() {
		Button synchroBtn = (Button) findViewById(R.id.pump_totaldaily_synchro_btn);
		synchroBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				BluetoothAdapter adpter = BluetoothAdapter.getDefaultAdapter();
				if (adpter.isEnabled() == false) {
					adpter.enable();
				} else {
					dialogUtil.showDialogLoading(true);
					WNBleControl.getInstance(
							PumpUserWorkRecordTotaldailyNew.this, mBle)
							.connect(userSP.getPumpMac());
				}
			}
		});
	}

	// 同步泵数据后执行
	private void initView() {
		WNBleControl.getInstance(this, mBle).disConnect(userSP.getPumpMac());
		dialogUtil.showDialogLoading(true);
		for (int i = 0; i < datas.size(); i++) {
			List<String> strings = WNHexChange.getPumpData(datas.get(i)
					.toString());
			DailyRecord dailyRecord = new DailyRecord();
			dailyRecord.setMonth(WNHexChange.intToString(strings.get(4)
					.toString()));
			dailyRecord.setDay(WNHexChange.intToString(strings.get(5)
					.toString()));
			String maxDose = WNHexChange.intToString(strings.get(6).toString()
					+ strings.get(7).toString());
			String maxDoseInt = WNHexChange.stringToInt(maxDose) / 10 + "."
					+ WNHexChange.stringToInt(maxDose) % 10;
			dailyRecord.setMaximumDose(maxDoseInt);
			String minDose = WNHexChange.intToString(strings.get(8).toString()
					+ strings.get(9).toString());
			String minDoseInt = WNHexChange.stringToInt(minDose) / 10 + "."
					+ WNHexChange.stringToInt(minDose) % 10;
			dailyRecord.setMinimumDose(minDoseInt);
			String averageDose = WNHexChange.intToString(strings.get(10)
					.toString() + strings.get(11).toString());
			String averageDoseInt = WNHexChange.stringToInt(averageDose) / 10
					+ "." + WNHexChange.stringToInt(averageDose) % 10;
			dailyRecord.setAverageDose(averageDoseInt);
			String totalDose = WNHexChange.intToString(strings.get(12)
					.toString() + strings.get(13).toString());
			String totalDoseInt = WNHexChange.stringToInt(totalDose) / 10 + "."
					+ WNHexChange.stringToInt(totalDose) % 10;
			dailyRecord.setTotalDose(totalDoseInt);
			dailyRecords.add(dailyRecord);
		}
		ListView totaldailyLs = (ListView) findViewById(R.id.pump_user_work_record_totaldaily_list);
		totaldailyLs.setAdapter(new PumpUserWorkRecordTotaldailyAdapter(
				PumpUserWorkRecordTotaldailyNew.this, datas));
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
				Toast.makeText(PumpUserWorkRecordTotaldailyNew.this, "断开连接",
						Toast.LENGTH_SHORT).show();
			}
			String data = intent.getStringExtra(BleService.EXTRA_VALUE);
			System.out.println(data + "***");
			if (data != null) {
				if (!TextUtils.isEmpty(resultData) && resultData.length() > 9) {
					System.out.println("返回数据***" + resultData);
					List<String> checks = WNHexChange.getPumpData(resultData);
					if (checks.get(0).toString().equals("10")) {
						datas.add(resultData);
						if (checks.get(3).toString().equals("0b")) {
							initView();
							dialogUtil.showDialogLoading(false);
						}
					}
					if (checks.get(2).toString().equals("b2")) {
					}
					if (checks.get(2).toString().equals("b3")) {
						Toast.makeText(
								PumpUserWorkRecordTotaldailyNew.this,
								getResources().getString(
										R.string.p_no_transmission),
								Toast.LENGTH_LONG).show();
					}
					if (checks.get(2).toString().equals("b4")) {
						Toast.makeText(PumpUserWorkRecordTotaldailyNew.this,
								getResources().getString(R.string.p_norecord),
								Toast.LENGTH_LONG).show();
					}

					if (checks.get(2).toString().equals("b5")) {
						Toast.makeText(
								PumpUserWorkRecordTotaldailyNew.this,
								getResources().getString(
										R.string.p_no_day_record),
								Toast.LENGTH_LONG).show();
					}

					if (checks.get(2).toString().equals("b6")) {
						Toast.makeText(
								PumpUserWorkRecordTotaldailyNew.this,
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
									PumpUserWorkRecordTotaldailyNew.this, mBle)
									.toWrite(userSP.getPumpMac(),
											Constant.PUMP_DAILY_RECORD);
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
						PumpUserWorkRecordTotaldailyNew.this, mBle)
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
