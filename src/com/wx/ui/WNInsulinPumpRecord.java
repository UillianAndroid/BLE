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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wx.custom.WNInsulinPumpRecordAdapter;
import com.wx.db.Constant;
import com.wx.impl.WNPumpImpl;
import com.wx.model.DayBaseAmountRecord;
import com.wx.model.DayLargeDoseRecord;
import com.wx.model.DayTotalAmountRecord;
import com.wx.model.WNInsulinAlarmRecord;
import com.wx.model.WNInsulinRecord;
import com.wx.pumptest.R;
import com.wx.utils.WNBleControl;
import com.wx.utils.WNHexChange;
import com.wx.view.WNPumpView;
import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.BleGattService;
import com.xtremeprog.sdk.ble.BleService;

/*
 * 该cls包含
 * 
 * 日总量记录 
 * 大剂量纪录
 * 基础量纪录
 * 报警纪录
 */

public class WNInsulinPumpRecord extends BaseActivity implements WNPumpView,
		OnClickListener {
	public static final String RECORD_TAG = "record_tag";// 纪录tag
	public static final String TOTALDAILY = "total_daily";// 日总量记录
	public static final String LARGEDOSE = "large_dose";// 大剂量纪录
	public static final String BASICS = "basics";// 基础量纪录
	public static final String ALARM = "alarm";// 报警纪录
	private String recrorTag;// 判断传入的tag
	private TextView timeTv;
	private String command;
	private List<DayTotalAmountRecord> totalAmountRecords;
	private List<DayLargeDoseRecord> dayLargeDoseRecords;
	private List<DayBaseAmountRecord> baseAmountRecords;
	private List<WNInsulinAlarmRecord> insulinAlarmRecords;
	private List<WNInsulinRecord> insulinRecords = new ArrayList<WNInsulinRecord>();
	private List<String> pumpDatas;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wn_insulin_pumprecord);
		recrorTag = getIntent().getStringExtra(RECORD_TAG);
		registerReceiver(mBleReceiverConnect, BleService.getIntentFilter());
		activity = WNInsulinPumpRecord.this;
		initView();
		initTitle(recrorTag);
		initListView(insulinRecords);
	}

	private void initView() {
		// 刷新时间
		timeTv = (TextView) findViewById(R.id.wn_insulin_record_time_tv);
		// 刷新按钮
		findViewById(R.id.wn_insulin_record_refresh_iv)
				.setOnClickListener(this);
		listView = (ListView) findViewById(R.id.wn_insulin_record_listview);
	}

	private void initListView(List<WNInsulinRecord> insulinRecords) {
		adapter = new WNInsulinPumpRecordAdapter(this, insulinRecords);
		listView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.wn_insulin_record_refresh_iv) {
			dialogUtil.showDialogLoading(true);
			pumpDatas = new ArrayList<String>();
			totalAmountRecords = new ArrayList<DayTotalAmountRecord>();
			dayLargeDoseRecords = new ArrayList<DayLargeDoseRecord>();
			baseAmountRecords = new ArrayList<DayBaseAmountRecord>();
			insulinAlarmRecords = new ArrayList<WNInsulinAlarmRecord>();
			insulinRecords = new ArrayList<WNInsulinRecord>();
			WNBleControl.getInstance(activity, mBle).connect(
					userSP.getPumpMac());
		}
	}

	private void initTitle(String tag) {
		if (tag.equals(TOTALDAILY)) {
			back(R.string.w_total_record);
			command = Constant.INSULIN_TOTAL_DAILY;
		}
		if (tag.equals(LARGEDOSE)) {
			back(R.string.w_large_pump);
			command = Constant.INSULIN_LARGE_DOSE;
		}
		if (tag.equals(BASICS)) {
			back(R.string.w_base_pump);
			command = Constant.INSULIN_BASICS;
		}
		if (tag.equals(ALARM)) {
			back(R.string.w_alarm_pump);
			command = Constant.INSULIN_ALARM;
		}
	}

	/*
	 * @wx操作蓝牙部分
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		WNBleControl.getInstance(this, mBle).disConnect(userSP.getPumpMac());
		unregisterReceiver(mBleReceiverConnect);
	}

	@Override
	protected void onPause() {
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
			String action = intent.getAction();
			if (BleService.BLE_GATT_CONNECTED.equals(action)) {
				System.out.println("BLE_GATT_CONNECTED***连接");
			} else if (BleService.BLE_GATT_DISCONNECTED.equals(action)) {
				System.out.println("BLE_GATT_DISCONNECTED***断开连接");
				dialogUtil.showDialogLoading(false);
				Toast.makeText(WNInsulinPumpRecord.this, "断开连接",
						Toast.LENGTH_SHORT).show();
				WNBleControl.getInstance(WNInsulinPumpRecord.this, mBle)
						.disConnect(userSP.getPumpMac());
			}
			String data = intent.getStringExtra(BleService.EXTRA_VALUE);
			System.out.println(data + "***");
			if (data != null) {
				if (data.length() % 2 != 0 && data.length() > 10)
					return;
				List<String> datas = WNHexChange.getPumpData(data);
				if ("b2".equals(datas.get(2))) {
					WNBleControl.getInstance(WNInsulinPumpRecord.this, mBle)
							.toWrite(userSP.getPumpMac(), command);
				}
				// 日总量纪录
				if (datas.size() > 10 && "a7".equals(datas.get(2))) {
					listView.setVisibility(View.VISIBLE);
					timeTv.setText(getString(R.string.w_now));
					pumpDatas.add(data);
					DayTotalAmountRecord totalAmountRecord = new DayTotalAmountRecord();
					WNInsulinRecord insulinRecord = new WNInsulinRecord();
					totalAmountRecord.setMonth(datas.get(5));
					totalAmountRecord.setDay(datas.get(6));
					totalAmountRecord.setDayBaseAmount(Integer
							.parseInt(WNHexChange.intToString(datas.get(7)
									+ datas.get(8)))
							/ 100
							+ "."
							+ Integer.parseInt(WNHexChange.intToString(datas
									.get(7) + datas.get(8))) % 100);
					insulinRecord.setLeftStr(datas.get(5) + "/" + datas.get(6));
					insulinRecord.setRightStr(Integer.parseInt(WNHexChange
							.intToString(datas.get(7) + datas.get(8)))
							/ 100
							+ "."
							+ Integer.parseInt(WNHexChange.intToString(datas
									.get(7) + datas.get(8))) % 100);
					insulinRecords.add(insulinRecord);
					totalAmountRecords.add(totalAmountRecord);
					if (datas.get(3).equals(datas.get(4))) {
						disconnectInsulin();
						dialogUtil.showDialogLoading(false);
						initListView(insulinRecords);
					}
				}
				// 大剂量纪录
				if (datas.size() > 14 && "a8".equals(datas.get(2))) {
					listView.setVisibility(View.VISIBLE);
					timeTv.setText(getString(R.string.w_now));
					pumpDatas.add(data);
					DayLargeDoseRecord dayLargeDoseRecord = new DayLargeDoseRecord();
					WNInsulinRecord insulinRecord = new WNInsulinRecord();
					dayLargeDoseRecord.setMonth(datas.get(5));
					dayLargeDoseRecord.setDay(datas.get(6));
					dayLargeDoseRecord.setHour(datas.get(7));
					dayLargeDoseRecord.setMinute(datas.get(8));
					dayLargeDoseRecord.setTimeLong(WNHexChange
							.intToString(datas.get(10)));
					dayLargeDoseRecord.setLargeDose(Integer
							.parseInt(WNHexChange.intToString(datas.get(11)
									+ datas.get(12)))
							/ 100
							+ "."
							+ Integer.parseInt(WNHexChange.intToString(datas
									.get(11) + datas.get(12))) % 100 + "U");
					if ("01".equals(datas.get(9))) {
						dayLargeDoseRecord.setStyle("01");
						insulinRecord
								.setRightStr(dayLargeDoseRecord.getLargeDose()
										+ getString(R.string.w_routine));
					} else if ("02".equals(datas.get(9))) {
						dayLargeDoseRecord.setStyle("02");
						insulinRecord.setRightStr(dayLargeDoseRecord
								.getLargeDose()
								+ getString(R.string.w_square_wave));
					} else if ("03".equals(datas.get(9))) {
						dayLargeDoseRecord.setStyle("03");
						insulinRecord
								.setRightStr(dayLargeDoseRecord.getLargeDose()
										+ getString(R.string.w_double_wave_conventional));
					}
					insulinRecord.setLeftStr(dayLargeDoseRecord.getMonth()
							+ "/" + dayLargeDoseRecord.getDay() + " "
							+ dayLargeDoseRecord.getHour() + ":"
							+ dayLargeDoseRecord.getMinute() + "("
							+ getString(R.string.w_time_long) + ":"
							+ dayLargeDoseRecord.getTimeLong() + "min)");
					insulinRecords.add(insulinRecord);
					dayLargeDoseRecords.add(dayLargeDoseRecord);
					if (datas.get(3).equals(datas.get(4))) {
						disconnectInsulin();
						dialogUtil.showDialogLoading(false);
						initListView(insulinRecords);
					}
				}
				// 基础量纪录
				if (datas.size() > 10 && "a9".equals(datas.get(2))) {
					listView.setVisibility(View.VISIBLE);
					timeTv.setText(getString(R.string.w_now));
					pumpDatas.add(data);
					DayBaseAmountRecord baseAmountRecord = new DayBaseAmountRecord();
					WNInsulinRecord insulinRecord = new WNInsulinRecord();
					baseAmountRecord.setMonth(datas.get(5));
					baseAmountRecord.setDay(datas.get(6));

					baseAmountRecord.setDayBaseAmount(Integer
							.parseInt(WNHexChange.intToString(datas.get(7)
									+ datas.get(8)))
							/ 100
							+ "."
							+ Integer.parseInt(WNHexChange.intToString(datas
									.get(7) + datas.get(8))) % 100);
					insulinRecord.setLeftStr(baseAmountRecord.getMonth() + "/"
							+ baseAmountRecord.getDay());
					insulinRecord.setRightStr(baseAmountRecord
							.getDayBaseAmount() + "U");
					insulinRecords.add(insulinRecord);
					baseAmountRecords.add(baseAmountRecord);
					if (datas.get(3).equals(datas.get(4))) {
						disconnectInsulin();
						dialogUtil.showDialogLoading(false);
						initListView(insulinRecords);
					}
				}
				// 报警记录
				if (datas.size() > 11 && "aa".equals(datas.get(2))) {
					listView.setVisibility(View.VISIBLE);
					timeTv.setText(getString(R.string.w_now));
					pumpDatas.add(data);
					WNInsulinAlarmRecord insulinAlarmRecord = new WNInsulinAlarmRecord();
					WNInsulinRecord insulinRecord = new WNInsulinRecord();
					insulinAlarmRecord.setMonth(datas.get(5));
					insulinAlarmRecord.setDay(datas.get(6));
					insulinAlarmRecord.setHour(datas.get(7));
					insulinAlarmRecord.setMinute(datas.get(8));
					if ("01".equals(datas.get(9))) {
						insulinAlarmRecord.setStyle("01");
						insulinRecord.setRightStr(getString(R.string.w_block));
					} else if ("02".equals(datas.get(9))) {
						insulinAlarmRecord.setStyle("02");
						insulinRecord
								.setRightStr(getString(R.string.w_over_elec));
					} else if ("03".equals(datas.get(9))) {
						insulinAlarmRecord.setStyle("03");
						insulinRecord
								.setRightStr(getString(R.string.w_over_dose));
					} else if ("04".equals(datas.get(9))) {
						insulinAlarmRecord.setStyle("04");
						insulinRecord.setRightStr(getString(R.string.w_error));
					}
					insulinRecord.setLeftStr(insulinAlarmRecord.getMonth()
							+ "/" + insulinAlarmRecord.getDay() + " "
							+ insulinAlarmRecord.getHour() + ":"
							+ insulinAlarmRecord.getMinute());
					insulinRecords.add(insulinRecord);
					insulinAlarmRecords.add(insulinAlarmRecord);
					if (datas.get(3).equals(datas.get(4))) {
						disconnectInsulin();
						dialogUtil.showDialogLoading(false);
						initListView(insulinRecords);
					}
				}
			}
			if (!userSP.getPumpMac().equals(
					extras.getString(BleService.EXTRA_ADDR))) {
				return;
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
							WNBleControl.getInstance(WNInsulinPumpRecord.this,
									mBle).toWrite(userSP.getPumpMac(), command);
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
						WNInsulinPumpRecord.this, mBle).getGattServices(
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
		WNBleControl.getInstance(WNInsulinPumpRecord.this, mBle).connect(
				userSP.getPumpMac());
	}

	private void disconnectInsulin() {
		WNBleControl.getInstance(this, mBle).disConnect(userSP.getPumpMac());
	}

	private ArrayList<ArrayList<BleGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BleGattCharacteristic>>();
	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";
	private WNInsulinPumpRecordAdapter adapter;

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
