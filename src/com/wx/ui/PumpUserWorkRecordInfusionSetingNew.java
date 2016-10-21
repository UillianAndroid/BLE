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

import com.wx.custom.PumpUserWorkRecordLnfusionsetingAdapter;
import com.wx.db.Constant;
import com.wx.impl.WNPumpImpl;
import com.wx.model.InfusionSet;
import com.wx.model.InfusionSetting;
import com.wx.pumptest.R;
import com.wx.utils.WNBleControl;
import com.wx.utils.WNHexChange;
import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.BleGattService;
import com.xtremeprog.sdk.ble.BleService;

public class PumpUserWorkRecordInfusionSetingNew extends BaseActivity {
	private String[] time = { "0:00", "0:15", "0:30", "0:45	", "1:00", "1:15",
			"1:30", "1:45", "2:00", "2:15", "2:30", "2:45", "3:00", "3:15",
			"3:30", "3:45", "4:00", "4:15", "4:30", "4:45", "5:00", "5:15",
			"5:30", "5:45", "6:00", "6:15", "6:30", "6:45", "7:00", "7:15",
			"7:30", "7:45", "8:00", "8:15", "8:30", "8:45", "9:00", "9:15",
			"9:30", "9:45", "10:00", "10:15", "10:30", "10:45", "11:00",
			"11:15", "11:30", "11:45", "12:00", "12:15", "12:30", "12:45",
			"13:00", "13:15", "13:30", "13:45", "14:00", "14:15", "14:30	",
			"14:45", "15:00", "15:15", "15:30", "15:45", "16:00", "16:15",
			"16:30", "16:45", "17:00", "17:15", "17:30", "17:45", "18:00",
			"18:15", "18:30", "18:45", "19:00", "19:15", "19:30", "19:45",
			"20:00", "20:15", "20:30", "20:45", "21:00", "21:15", "21:30",
			"21:45", "22:00", "22:15", "22:30", "22:45", "23:00", "23:15",
			"23:30", "23:45" };
	private List<String> postTimes = new ArrayList<String>();
	private List<String> postInfusions = new ArrayList<String>();
	private List<InfusionSetting> infusionSettings = new ArrayList<InfusionSetting>();
	private InfusionSetting infusionSetting = new InfusionSetting();
	private List<String> datas = new ArrayList<String>();
	private String resultData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pump_user_work_record_infusionseting);
		activity = PumpUserWorkRecordInfusionSetingNew.this;
		back(R.string.p_trans_set);
		initClick();
	}

	private void initClick() {
		// 同步
		Button synchroBtn = (Button) findViewById(R.id.pump_infusion_setting_synchro_btn);
		synchroBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				BluetoothAdapter adpter = BluetoothAdapter.getDefaultAdapter();
				if (adpter.isEnabled() == false) {
					adpter.enable();
				} else {
					WNBleControl.getInstance(
							PumpUserWorkRecordInfusionSetingNew.this, mBle)
							.connect(userSP.getPumpMac());
				}
			}
		});
	}

	private void initView() {
		List<String> times = new ArrayList<String>();
		for (int i = 0; i < time.length; i++) {
			times.add(time[i]);
		}
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < datas.size(); i++) {
			if (datas.get(i).toString().length() != 15) {
				stringBuffer.append(datas.get(i).toString());
			}
		}
		String result = new String(stringBuffer);
		List<String> resultList = WNHexChange.getPumpData(result);
		List<String> resultFinalList = new ArrayList<String>();
		for (int i = 0; i < resultList.size(); i++) {
			if (i % 18 != 0 && i % 18 != 1 && i % 18 != 2 && i % 18 != 3
					&& i % 18 != 16 && i % 18 != 17) {
				resultFinalList.add(WNHexChange.intToString(resultList.get(i)));
			}
		}
		System.out.println(resultFinalList.size() + "___wx长度___"
				+ resultList.size());
		postInfusions.clear();
		postTimes.clear();

		for (int i = 0; i < resultFinalList.size(); i++) {
			if (!("0").equals(resultFinalList.get(i).toString())) {
				postInfusions.add(resultFinalList.get(i));
				postTimes.add(times.get(i));
			}
		}
		if (96 == resultFinalList.size()) {
			WNBleControl.getInstance(this, mBle)
					.disConnect(userSP.getPumpMac());
			dialogUtil.showDialogLoading(true);
			infusionSettings.clear();
			infusionSetting.setDate(" ");
			infusionSettings.add(infusionSetting);
			List<InfusionSet> infusionSets = new ArrayList<InfusionSet>();
			for (int i = 0; i < postInfusions.size(); i++) {
				InfusionSet infusionSet = new InfusionSet();
				infusionSet.setDose(postInfusions.get(i));
				infusionSet.setTime(postTimes.get(i));
				infusionSets.add(infusionSet);
			}
			infusionSetting.setInfusionSet(infusionSets);
			infusionSettings.add(infusionSetting);
		}
		ListView listView = (ListView) findViewById(R.id.pump_user_work_record_infusionseting_list);
		listView.setAdapter(new PumpUserWorkRecordLnfusionsetingAdapter(
				PumpUserWorkRecordInfusionSetingNew.this, infusionSettings));
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
				Toast.makeText(PumpUserWorkRecordInfusionSetingNew.this,
						"断开连接", Toast.LENGTH_SHORT).show();
			}
			String data = intent.getStringExtra(BleService.EXTRA_VALUE);
			System.out.println(data + "***");
			if (data != null) {
				if (!TextUtils.isEmpty(resultData) && resultData.length() > 9) {
					System.out.println("返回数据***" + resultData);
					List<String> checks = WNHexChange.getPumpData(resultData);
					if (data.length() > 11) {
						resultData = data;
						datas.add(resultData);
						initView();
					}
					if (checks.get(2).toString().equals("b2")) {
					}
					if (checks.get(2).toString().equals("b3")) {
						Toast.makeText(
								PumpUserWorkRecordInfusionSetingNew.this,
								getResources().getString(
										R.string.p_no_transmission),
								Toast.LENGTH_LONG).show();
					}
					if (checks.get(2).toString().equals("b4")) {
						Toast.makeText(
								PumpUserWorkRecordInfusionSetingNew.this,
								getResources().getString(R.string.p_norecord),
								Toast.LENGTH_LONG).show();
					}

					if (checks.get(2).toString().equals("b5")) {
						Toast.makeText(
								PumpUserWorkRecordInfusionSetingNew.this,
								getResources().getString(
										R.string.p_no_day_record),
								Toast.LENGTH_LONG).show();
					}

					if (checks.get(2).toString().equals("b6")) {
						Toast.makeText(
								PumpUserWorkRecordInfusionSetingNew.this,
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
									PumpUserWorkRecordInfusionSetingNew.this,
									mBle).toWrite(userSP.getPumpMac(),
									Constant.PUMP_INFUSION_SETTING);
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
						PumpUserWorkRecordInfusionSetingNew.this, mBle)
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
