package com.wx.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wx.custom.HistogramView;
import com.wx.db.Constant;
import com.wx.impl.WNPumpImpl;
import com.wx.model.WNInsulinRecord;
import com.wx.model.WNViewHolder;
import com.wx.pumptest.R;
import com.wx.utils.WNBleControl;
import com.wx.utils.WNHexChange;
import com.wx.view.WNPumpView;
import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.BleGattService;
import com.xtremeprog.sdk.ble.BleService;

public class WNInsulinWorkStatus extends BaseActivity implements WNPumpView,
		OnClickListener {

	private TextView workstatusTimeTv, baseRateTv;
	private ImageView workstatusRefreshIv;
	private String command;
	private String baserate;
	private TextView rightTv;
	private int baserateTag;
	private List<WNInsulinRecord> insulinRecords;
	private StringBuilder originalContentSB = new StringBuilder();
	//
	ArrayList<View> mViewList = new ArrayList<View>();
	LayoutInflater mLayoutInflater;
	LinearLayout mNumLayout;

	Button mPreSelectedBt;

	MyPagerAdapter mPagerAdapter;
	public static float[] progress = new float[24];// 7
	private Button btnOne, btnTwo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wn_insulin_work_status);
		registerReceiver(mBleReceiverConnect, BleService.getIntentFilter());
		activity = WNInsulinWorkStatus.this;
		baserate = getIntent().getStringExtra("baserate");
		if ("1".equals(baserate)) {
			baserateTag = 1;
		}
		if ("2".equals(baserate)) {
			baserateTag = 2;
		}
		initView();
		back(-1);
		//
		mLayoutInflater = getLayoutInflater();
		// 可以按照需求进行动态创建Layout,这里暂用静态的xml layout
		mViewList.add(mLayoutInflater.inflate(R.layout.per_pager1, null));
		mViewList.add(mLayoutInflater.inflate(R.layout.per_pager2, null));
		// mViewList.add(mLayoutInflater.inflate(R.layout.per_pager3, null));
		ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new MyPagerAdapter();
		viewPager.setAdapter(mPagerAdapter);
		//
		mNumLayout = (LinearLayout) findViewById(R.id.ll_pager_num);

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_dot_normal);
		btnOne = new Button(this);
		btnOne.setLayoutParams(new ViewGroup.LayoutParams(bitmap.getWidth(),
				bitmap.getHeight()));
		btnOne.setBackgroundResource(R.drawable.home_page_dot_select);
		mNumLayout.addView(btnOne);
		btnTwo = new Button(this);
		btnTwo.setLayoutParams(new ViewGroup.LayoutParams(bitmap.getWidth(),
				bitmap.getHeight()));
		btnTwo.setBackgroundResource(R.drawable.icon_dot_normal);
		mNumLayout.addView(btnTwo);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				if (position == 0) {
					btnOne.setBackgroundResource(R.drawable.home_page_dot_select);
					btnTwo.setBackgroundResource(R.drawable.icon_dot_normal);
				} else {
					btnOne.setBackgroundResource(R.drawable.icon_dot_normal);
					btnTwo.setBackgroundResource(R.drawable.home_page_dot_select);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mViewList.size();
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(mViewList.get(position), 0);
			return mViewList.get(position);
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(mViewList.get(position));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

	private void initView() {
		rightTv = (TextView) findViewById(R.id.title_text_right);
		rightTv.setVisibility(View.VISIBLE);
		if (baserateTag == 1) {
			rightTv.setText(getString(R.string.w_base_type) + "2");
		} else if (baserateTag == 2) {
			rightTv.setText(getString(R.string.w_base_type) + "1");
		}
		rightTv.setOnClickListener(this);
		// 时间
		workstatusTimeTv = (TextView) findViewById(R.id.wn_insulin_workstatus_time_tv);
		// 刷新
		workstatusRefreshIv = (ImageView) findViewById(R.id.wn_insulin_workstatus_refresh_iv);
		workstatusRefreshIv.setOnClickListener(this);
		// 基础率
		baseRateTv = (TextView) findViewById(R.id.wn_insulin_workstatus_baserate_tv);
		baseRateTv.setText(getString(R.string.w_base_type) + baserate);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		// 刷新
		if (id == R.id.wn_insulin_workstatus_refresh_iv) {
			dialogUtil.showDialogLoading(true);
			insulinRecords = new ArrayList<WNInsulinRecord>();
			if (baserateTag == 1) {
				command = Constant.INSULIN_BASE_ONE;
			} else if (baserateTag == 2) {
				command = Constant.INSULIN_BASE_TWO;
			}
			WNBleControl.getInstance(activity, mBle).connect(
					userSP.getPumpMac());
			return;
		}
		// 查看另一种模式详情(右上角)
		if (id == R.id.title_text_right) {
			insulinRecords = new ArrayList<WNInsulinRecord>();
			initList();
			progress = new float[24];
			if (baserateTag == 1) {
				baserateTag = 2;
				baseRateTv.setText(getString(R.string.w_base_type) + "2");
				rightTv.setText(getString(R.string.w_base_type) + "1");
			} else if (baserateTag == 2) {
				baserateTag = 1;
				baseRateTv.setText(getString(R.string.w_base_type) + "1");
				rightTv.setText(getString(R.string.w_base_type) + "2");
			}
			return;
		}
	}

	private void initList() {
		if (insulinRecords.size() > 0) {
			workstatusTimeTv.setText(insulinRecords.get(0).getTitle());
		}
		for (int i = 0; i < insulinRecords.size(); i++) {
			progress[i] = Float.parseFloat(insulinRecords.get(i).getRightStr()
					.toString());
		}
		mPagerAdapter.notifyDataSetChanged();
		ListView listView = (ListView) findViewById(R.id.wn_insulin_workstatus_listview);
		MyAdapter adapter = new MyAdapter();
		listView.setAdapter(adapter);
		HistogramView histogramView = (HistogramView) findViewById(R.id.just_histogram);
		histogramView.start(2);
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return insulinRecords.size();
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
		public View getView(int position, View converntView, ViewGroup arg2) {
			WNViewHolder viewHolder = null;
			if (converntView == null) {
				viewHolder = new WNViewHolder();
				converntView = getLayoutInflater().inflate(
						R.layout.wn_insulin_workstatus_item, null);
				viewHolder.tv1 = (TextView) converntView
						.findViewById(R.id.wn_insulin_pump_record_tv1);
				viewHolder.tv2 = (TextView) converntView
						.findViewById(R.id.wn_insulin_pump_record_tv2);
				converntView.setTag(viewHolder);
			} else {
				viewHolder = (WNViewHolder) converntView.getTag();
			}
			viewHolder.tv1.setText(insulinRecords.get(position).getLeftStr()
					+ getString(R.string.wn_m_time));
			viewHolder.tv2.setText(insulinRecords.get(position).getRightStr()
					+ "U");
			return converntView;
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
		unregisterReceiver(mBleReceiverConnect);
		WNBleControl.getInstance(this, mBle).disConnect(userSP.getPumpMac());
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void disconnectInsulin() {
		WNBleControl.getInstance(this, mBle).disConnect(userSP.getPumpMac());
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
				Toast.makeText(WNInsulinWorkStatus.this, "断开连接",
						Toast.LENGTH_SHORT).show();
				WNBleControl.getInstance(WNInsulinWorkStatus.this, mBle)
						.disConnect(userSP.getPumpMac());
			}
			String data = intent.getStringExtra(BleService.EXTRA_VALUE);
			System.out.println(data + "***");
			if (data != null) {
				if (data.length() % 2 != 0 && data.length() > 10)
					return;
				List<String> datas = WNHexChange.getPumpData(data);
				// 13 66 a3 02 01 06 06 06 0d 0d 0d 0c 0c 0c 0a 00***
				if ("01".equals(datas.get(4))) {
					for (int i = 5; i < 17; i++) {
						WNInsulinRecord insulinRecord = new WNInsulinRecord(
								WNInsulinWorkStatus.this);
						insulinRecord.setLeftStr((i - 5) + "~" + (i - 4));
						insulinRecord.setRightStr(Integer.parseInt(WNHexChange
								.intToString(datas.get(i)))
								/ 10
								+ "."
								+ Integer.parseInt(WNHexChange
										.intToString(datas.get(i))) % 10);
						insulinRecord.setTitle(getString(R.string.w_now));
						insulinRecords.add(insulinRecord);
					}
					originalContentSB.append(data);
				} else if ("02".equals(datas.get(4))) {
					for (int i = 5; i < 17; i++) {
						WNInsulinRecord insulinRecord = new WNInsulinRecord(
								WNInsulinWorkStatus.this);
						insulinRecord.setLeftStr((i + 7) + "~" + (i + 8));
						insulinRecord.setRightStr(Integer.parseInt(WNHexChange
								.intToString(datas.get(i)))
								/ 10
								+ "."
								+ Integer.parseInt(WNHexChange
										.intToString(datas.get(i))) % 10);
						insulinRecord.setTitle(getString(R.string.w_now));
						insulinRecords.add(insulinRecord);
					}
					originalContentSB.append("," + data);
					workstatusTimeTv.setText(getString(R.string.w_update_at)
							+ ":" + getString(R.string.w_now));
					initList();
					disconnectInsulin();
					dialogUtil.showDialogLoading(false);
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
							WNBleControl.getInstance(WNInsulinWorkStatus.this,
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
						WNInsulinWorkStatus.this, mBle).getGattServices(
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
		WNBleControl.getInstance(WNInsulinWorkStatus.this, mBle).connect(
				userSP.getPumpMac());
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
