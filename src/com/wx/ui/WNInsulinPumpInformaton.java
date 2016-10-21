package com.wx.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wx.db.Constant;
import com.wx.impl.WNPumpImpl;
import com.wx.pumptest.R;
import com.wx.utils.WNBleControl;
import com.wx.utils.WNHexChange;
import com.wx.view.WNPumpView;
import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.BleGattService;
import com.xtremeprog.sdk.ble.BleService;

public class WNInsulinPumpInformaton extends BaseActivity implements
		OnClickListener, WNPumpView {
	// 中间模块
	private TextView basedModelTv, nowBaseModelTv, alreadyBaseModelTv;
	private ImageView baseRefreshIv;
	// 底部模块
	private TextView statusTimeTv, electricityTv, chargeTv, blockTv,
			inputSwitchTv;
	private LinearLayout statusRefreshLl;
	private TextView serialNumberTv;
	private TextView leftTv, rightTv;
	private ImageView leftIv, rightIv;
	private String command;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wn_insulinpump_informaton);
		activity = WNInsulinPumpInformaton.this;
		initView();
		back(R.string.w_insulin_info);
	}

	private void initView() {
		// 顶部模块（时间）
		leftTv = (TextView) findViewById(R.id.wn_insulin_pump_record_left_tv);
		rightTv = (TextView) findViewById(R.id.wn_insulin_pump_record_right_tv);
		leftIv = (ImageView) findViewById(R.id.wn_insulin_pump_record_left_iv);
		rightIv = (ImageView) findViewById(R.id.wn_insulin_pump_record_right_iv);
		leftIv.setOnClickListener(this);
		rightIv.setOnClickListener(this);
		// 中间模块（工作模式）
		basedModelTv = (TextView) findViewById(R.id.wn_insulin_info_based_model_tv);
		baseRefreshIv = (ImageView) findViewById(R.id.wn_insulin_info_based_model_refresh_iv);
		baseRefreshIv.setOnClickListener(this);
		nowBaseModelTv = (TextView) findViewById(R.id.wn_insulin_info_now_base_model_tv);
		alreadyBaseModelTv = (TextView) findViewById(R.id.wn_insulin_info_already_base_model_tv);
		findViewById(R.id.wn_insulin_info_ll3).setOnClickListener(this);
		// 底部模块（泵状态）
		statusRefreshLl = (LinearLayout) findViewById(R.id.wn_insulin_info_ll4);
		statusRefreshLl.setOnClickListener(this);
		statusTimeTv = (TextView) findViewById(R.id.wn_insulin_info_status_time_tv);
		electricityTv = (TextView) findViewById(R.id.wn_insulin_info_electricity_tv);
		chargeTv = (TextView) findViewById(R.id.wn_insulin_info_charge_tv);
		blockTv = (TextView) findViewById(R.id.wn_insulin_info_block_tv);
		inputSwitchTv = (TextView) findViewById(R.id.wn_insulin_info_input_switch_tv);
		serialNumberTv = (TextView) findViewById(R.id.wn_insulin_info_serial_number_tv);
		serialNumberTv.setText("序列号:" + userSP.getPumpName());
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		// 总量
		if (id == R.id.wn_insulin_pump_record_left_iv) {
			dialogUtil.showDialogLoading(true);
			command = Constant.INSULIN_DAILY_BASE;
			WNBleControl.getInstance(activity, mBle).connect(
					userSP.getPumpMac());
		}
		if (id == R.id.wn_insulin_pump_record_right_iv) {
			dialogUtil.showDialogLoading(true);
			command = Constant.INSULIN_DAILY_DOSE;
			WNBleControl.getInstance(activity, mBle).connect(
					userSP.getPumpMac());
		}
		// 工作模式
		if (id == R.id.wn_insulin_info_based_model_refresh_iv) {
			dialogUtil.showDialogLoading(true);
			command = Constant.INSULIN_PRESENT_BASE_MODEL;
			WNBleControl.getInstance(activity, mBle).connect(
					userSP.getPumpMac());
		}
		// 基础模式详情
		if (id == R.id.wn_insulin_info_ll3) {
			if ("1".equals(basedModelTv.getText())
					|| "2".equals(basedModelTv.getText())) {
				Intent intent = new Intent();
				intent.putExtra("baserate", basedModelTv.getText());
				intent.setClass(this, WNInsulinWorkStatus.class);
				disconnectInsulin();
				startActivity(intent);
			} else {
				Toast.makeText(this,
						getString(R.string.w_synchronization_base_type),
						Toast.LENGTH_SHORT).show();
			}
		}
		// 泵状态
		if (id == R.id.wn_insulin_info_ll4) {
			dialogUtil.showDialogLoading(true);
			command = Constant.INSULIN_PUMP_INFO;
			WNBleControl.getInstance(activity, mBle).connect(
					userSP.getPumpMac());
		}
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
				Toast.makeText(WNInsulinPumpInformaton.this, "断开连接",
						Toast.LENGTH_SHORT).show();
				WNBleControl.getInstance(WNInsulinPumpInformaton.this, mBle)
						.disConnect(userSP.getPumpMac());
			}
			String data = intent.getStringExtra(BleService.EXTRA_VALUE);
			System.out.println(data + "***");
			if (data != null) {
				if (data.length() % 2 != 0 && data.length() > 10)
					return;
				List<String> datas = WNHexChange.getPumpData(data);
				// 当日基础总量
				if (datas.size() > 6 && "a5".equals(datas.get(2))) {
					disconnectInsulin();
					dialogUtil.showDialogLoading(false);
					String dayBaseAmount = WNHexChange.intToString(datas.get(3)
							+ datas.get(4));
					String dayBaseAmountFinal = Integer.parseInt(dayBaseAmount)
							/ 100 + "." + Integer.parseInt(dayBaseAmount) % 100;
					leftTv.setText(dayBaseAmountFinal + "U");
				}
				// 日大剂量总量
				if (datas.size() > 6 && "a6".equals(datas.get(2))) {
					disconnectInsulin();
					dialogUtil.showDialogLoading(false);
					String dayLargeDose = WNHexChange.intToString(datas.get(3)
							+ datas.get(4));
					String dayLargeDoseFinal = Integer.parseInt(dayLargeDose)
							/ 100 + "." + Integer.parseInt(dayLargeDose) % 100;
					rightTv.setText(dayLargeDoseFinal + "U");
				}
				// 基础模式 08 66 a2 01 0a 00 08 ee***
				if (datas.size() > 6 && "a2".equals(datas.get(2))) {
					disconnectInsulin();
					dialogUtil.showDialogLoading(false);
					// 基础模式
					String basedModel = datas.get(3);
					if ("01".equals(basedModel)) {
						basedModelTv.setText("1");
					}
					if ("02".equals(basedModel)) {
						basedModelTv.setText("2");
					}
					// 当前基础率
					String baseRate = Integer.parseInt(WNHexChange
							.intToString(datas.get(4)))
							/ 10
							+ "."
							+ Integer.parseInt(WNHexChange.intToString(datas
									.get(4))) % 10 + "0";
					nowBaseModelTv.setText(baseRate + "U");
					// 已输注的基础率
					String baseRateOfInjection = Integer.parseInt(WNHexChange
							.intToString(datas.get(5)))
							/ 100
							+ "."
							+ Integer.parseInt(WNHexChange.intToString(datas
									.get(5))) % 100;
					alreadyBaseModelTv.setText(baseRateOfInjection + "U");
				}
				// 泵工作状态
				if (datas.size() > 8 && "a1".equals(datas.get(2))) {
					disconnectInsulin();
					dialogUtil.showDialogLoading(false);
					// 电量🔋
					String electricity = WNHexChange.intToString(datas.get(3));
					int electricityInt = Integer.parseInt(electricity);
					if (electricityInt < 5) {
						electricityTv
								.setText(getString(R.string.w_electric_over));
					} else if (electricityInt >= 5 && electricityInt < 10) {
						electricityTv
								.setText(getString(R.string.w_electric_low));
					} else {
						electricityTv.setText(electricityInt + "%");
					}
					// 药量
					String charge = WNHexChange.intToString(datas.get(4)
							+ datas.get(5));
					String chargeFinal = Integer.parseInt(charge) / 10 + "."
							+ Integer.parseInt(charge) % 10;
					chargeTv.setText(chargeFinal + "U");
					// 阻塞标志
					String blockingSign = datas.get(6);
					if ("0a".equals(blockingSign)) {
						// 阻塞
						blockTv.setText(getString(R.string.w_block));
					} else if ("0b".equals(blockingSign)) {
						// 正常
						blockTv.setText(getString(R.string.w_normal));
					}
					// 输注开关
					String inputSwitch = datas.get(7);
					if ("a5".equals(inputSwitch)) {
						// 开
						inputSwitchTv.setText(getString(R.string.w_block));
					} else if ("b5".equals(inputSwitch)) {
						// 关
						inputSwitchTv
								.setText(getString(R.string.w_infusion_off));
					}
					statusTimeTv.setText(getString(R.string.w_update_at) + ":"
							+ getString(R.string.w_now));
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
									WNInsulinPumpInformaton.this, mBle)
									.toWrite(userSP.getPumpMac(), command);
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
						WNInsulinPumpInformaton.this, mBle).getGattServices(
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

	@Override
	public void connectionDevice() {
		WNBleControl.getInstance(WNInsulinPumpInformaton.this, mBle).connect(
				userSP.getPumpMac());
	}

	private void disconnectInsulin() {
		WNBleControl.getInstance(this, mBle).disConnect(userSP.getPumpMac());
	}

	private ArrayList<ArrayList<BleGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BleGattCharacteristic>>();
	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";

	private void displayGattServices(List<BleGattService> gattServices) {
		if (gattServices == null)
			return;
		// String uuid = null;
		String unknownServiceString = getResources().getString(
				R.string.wn_unknown_service);
		String unknownCharaString = getResources().getString(
				R.string.wn_unknown_characteristic);
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
