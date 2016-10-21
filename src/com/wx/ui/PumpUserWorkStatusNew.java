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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wx.db.Constant;
import com.wx.impl.WNPumpImpl;
import com.wx.model.PumpDataObj;
import com.wx.model.PumpWorkStatus;
import com.wx.model.WNViewHolder;
import com.wx.pumptest.R;
import com.wx.utils.WNBleControl;
import com.wx.utils.WNHexChange;
import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.BleGattService;
import com.xtremeprog.sdk.ble.BleService;

public class PumpUserWorkStatusNew extends BaseActivity {
	private String resultData;
	private String timePost, electricPost, residualDosePost,
			residualDosePostTemp, blockPost, concentrationPost,
			infusionIntervalPost, infusionDosePost;
	private List<PumpDataObj> pumpDataObjs = new ArrayList<PumpDataObj>();
	private List<PumpWorkStatus> pumpWorkStatus = new ArrayList<PumpWorkStatus>();
	private List<String> strings = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pump_user_work_status);
		activity = PumpUserWorkStatusNew.this;
		back(R.string.p_pump_work_state);
		initClick();
	}

	private void initClick() {

		// 同步泵数据
		Button synchroBtn = (Button) findViewById(R.id.pump_work_status_synchro_btn);
		synchroBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				pumpDataObjs.clear();
				pumpWorkStatus.clear();
				BluetoothAdapter adpter = BluetoothAdapter.getDefaultAdapter();
				if (adpter.isEnabled() == false) {
					adpter.enable();
				} else {
					dialogUtil.showDialogLoading(true);
					WNBleControl.getInstance(PumpUserWorkStatusNew.this, mBle)
							.connect(userSP.getPumpMac());
				}
			}
		});
	}

	private void initList() {
		ListView dataList = (ListView) findViewById(R.id.pump_work_status_list);
		MyListAdapter adapter = new MyListAdapter();
		dataList.setAdapter(adapter);
	}

	class MyListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return strings.size();
		}

		@Override
		public Object getItem(int arg0) {

			return null;
		}

		@Override
		public long getItemId(int arg0) {

			return 0;
		}

		@Override
		public View getView(int position, View convernView, ViewGroup arg2) {

			WNViewHolder holder = null;
			if (convernView == null) {
				holder = new WNViewHolder();
				convernView = getLayoutInflater().inflate(
						R.layout.pump_user_work_status_item, null);
				// 时间
				holder.tv1 = (TextView) convernView
						.findViewById(R.id.pump_work_status_data_tv);
				// 电量
				holder.tv2 = (TextView) convernView
						.findViewById(R.id.pump_work_status_electric_tv);
				// 剩余药量
				holder.tv3 = (TextView) convernView
						.findViewById(R.id.pump_work_status_residual_dose_tv);
				// 阻塞
				holder.tv4 = (TextView) convernView
						.findViewById(R.id.pump_work_status_block_tv);
				// 浓度
				holder.tv5 = (TextView) convernView
						.findViewById(R.id.pump_work_status_concentration_tv);
				// 输注间隔
				holder.tv6 = (TextView) convernView
						.findViewById(R.id.pump_work_status_infusion_interval_tv);
				// 输注剂量
				holder.tv7 = (TextView) convernView
						.findViewById(R.id.pump_work_status_infusion_dose_tv);
				convernView.setTag(holder);
			} else {
				holder = (WNViewHolder) convernView.getTag();
			}
			electricPost = WNHexChange.intToString(strings.get(3).toString());
			residualDosePostTemp = WNHexChange.intToString(strings.get(4)
					.toString() + strings.get(5).toString());
			int residualDosePostInt = WNHexChange
					.stringToInt(residualDosePostTemp);
			residualDosePost = residualDosePostInt / 10 + "."
					+ residualDosePostInt % 10;
			if (strings.get(6).toString().equals("0a")) {
				blockPost = getResources().getString(R.string.block);
			} else if (strings.get(6).toString().equals("0b")
					|| strings.get(6).toString().equals("00")) {
				blockPost = getResources().getString(R.string.p_normal);
			}
			concentrationPost = WNHexChange.intToString(strings.get(7)
					.toString() + strings.get(8).toString());
			infusionIntervalPost = WNHexChange.intToString(strings.get(9)
					.toString());
			infusionDosePost = WNHexChange.intToString(strings.get(10)
					.toString());
			holder.tv1.setText(timePost);
			holder.tv2.setText(electricPost + "%");
			holder.tv3.setText(residualDosePost + "μg");
			holder.tv4.setText(blockPost);
			holder.tv5.setText(concentrationPost + "μg/ml");
			holder.tv6.setText(infusionIntervalPost + "min");
			holder.tv7.setText(infusionDosePost + "μg");
			return convernView;
		}
	}

	// 得到泵数据
	private void initView() {
		dialogUtil.showDialogLoading(true);
		strings = WNHexChange.getPumpData(resultData);
		electricPost = WNHexChange.intToString(strings.get(3).toString());
		residualDosePostTemp = WNHexChange.intToString(strings.get(4)
				.toString() + strings.get(5).toString());
		int residualDosePostInt = WNHexChange.stringToInt(residualDosePostTemp);
		residualDosePost = residualDosePostInt / 10 + "." + residualDosePostInt
				% 10;
		if (strings.get(6).toString().equals("0a")) {
			blockPost = getResources().getString(R.string.block);
		} else if (strings.get(6).toString().equals("0b")
				|| strings.get(6).toString().equals("00")) {
			blockPost = getResources().getString(R.string.p_normal);
		}
		concentrationPost = WNHexChange.intToString(strings.get(7).toString()
				+ strings.get(8).toString());
		infusionIntervalPost = WNHexChange.intToString(strings.get(9)
				.toString());
		infusionDosePost = WNHexChange.intToString(strings.get(10).toString());
		initList();
	}

	/*
	 * @wx操作蓝牙部分
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
			}
			String data = intent.getStringExtra(BleService.EXTRA_VALUE);
			System.out.println(data + "***");
			if (data != null) {
				if (!TextUtils.isEmpty(resultData) && resultData.length() > 9) {
					System.out.println("返回数据***" + resultData);
					List<String> checks = WNHexChange.getPumpData(resultData);
					if (checks.get(0).toString().equals("0d")) {
						initView();
						dialogUtil.showDialogLoading(false);
					}
					if (checks.get(2).toString().equals("b2")) {
					}
					if (resultData.substring(6, 8).equals("b3")) {
						Toast.makeText(
								PumpUserWorkStatusNew.this,
								getResources().getString(
										R.string.p_no_transmission),
								Toast.LENGTH_LONG).show();
					}
					if (resultData.substring(6, 8).equals("b4")) {
						Toast.makeText(PumpUserWorkStatusNew.this,
								getResources().getString(R.string.p_norecord),
								Toast.LENGTH_LONG).show();
					}

					if (resultData.substring(6, 8).equals("b5")) {
						Toast.makeText(
								PumpUserWorkStatusNew.this,
								getResources().getString(
										R.string.p_no_day_record),
								Toast.LENGTH_LONG).show();
					}

					if (resultData.substring(6, 8).equals("b6")) {
						Toast.makeText(
								PumpUserWorkStatusNew.this,
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
									PumpUserWorkStatusNew.this, mBle).toWrite(
									userSP.getPumpMac(),
									Constant.PUMP_WORK_STATUS);
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
						PumpUserWorkStatusNew.this, mBle).getGattServices(
						userSP.getPumpMac()));
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
