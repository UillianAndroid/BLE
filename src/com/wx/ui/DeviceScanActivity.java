/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * This XPG software is supplied to you by Xtreme Programming Group, Inc.
 * ("XPG") in consideration of your agreement to the following terms, and your
 * use, installation, modification or redistribution of this XPG software
 * constitutes acceptance of these terms.� If you do not agree with these terms,
 * please do not use, install, modify or redistribute this XPG software.
 * 
 * In consideration of your agreement to abide by the following terms, and
 * subject to these terms, XPG grants you a non-exclusive license, under XPG's
 * copyrights in this original XPG software (the "XPG Software"), to use and
 * redistribute the XPG Software, in source and/or binary forms; provided that
 * if you redistribute the XPG Software, with or without modifications, you must
 * retain this notice and the following text and disclaimers in all such
 * redistributions of the XPG Software. Neither the name, trademarks, service
 * marks or logos of XPG Inc. may be used to endorse or promote products derived
 * from the XPG Software without specific prior written permission from XPG.�
 * Except as expressly stated in this notice, no other rights or licenses,
 * express or implied, are granted by XPG herein, including but not limited to
 * any patent rights that may be infringed by your derivative works or by other
 * works in which the XPG Software may be incorporated.
 * 
 * The XPG Software is provided by XPG on an "AS IS" basis.� XPG MAKES NO
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE IMPLIED
 * WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, REGARDING THE XPG SOFTWARE OR ITS USE AND OPERATION ALONE OR IN
 * COMBINATION WITH YOUR PRODUCTS.
 * 
 * IN NO EVENT SHALL XPG BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION, MODIFICATION
 * AND/OR DISTRIBUTION OF THE XPG SOFTWARE, HOWEVER CAUSED AND WHETHER UNDER
 * THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE), STRICT LIABILITY OR
 * OTHERWISE, EVEN IF XPG HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * ABOUT XPG: Established since June 2005, Xtreme Programming Group, Inc. (XPG)
 * is a digital solutions company based in the United States and China. XPG
 * integrates cutting-edge hardware designs, mobile applications, and cloud
 * computing technologies to bring innovative products to the marketplace. XPG's
 * partners and customers include global leading corporations in semiconductor,
 * home appliances, health/wellness electronics, toys and games, and automotive
 * industries. Visit www.xtremeprog.com for more information.
 * 
 * Copyright (C) 2013 Xtreme Programming Group, Inc. All Rights Reserved.
 */

package com.wx.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wx.pumptest.R;
import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends BaseActivity implements OnClickListener {
	private LeDeviceListAdapter mLeDeviceListAdapter;
	private boolean mScanning;
	private Handler mHandler;
	private IBle mBle;

	private static final int REQUEST_ENABLE_BT = 1;
	// Stops scanning after 10 seconds.
	private static final long SCAN_PERIOD = 10000;
	private TextView rightTitleTv;
	private ProgressBar progressBar;
	private ListView listView;

	private final BroadcastReceiver mBleReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BleService.BLE_NOT_SUPPORTED.equals(action)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(DeviceScanActivity.this,
								"Ble not support", Toast.LENGTH_SHORT).show();
						finish();
					}
				});
			} else if (BleService.BLE_DEVICE_FOUND.equals(action)) {
				// device found
				Bundle extras = intent.getExtras();
				if (extras != null) {
					final BluetoothDevice device = extras
							.getParcelable(BleService.EXTRA_DEVICE);
					byte[] scanRecord = extras
							.getByteArray(BleService.EXTRA_SCAN_RECORD);
					// if (bytesToHexString(scanRecord).substring(4, 8).equals(
					// "696E")) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mLeDeviceListAdapter.addDevice(device);
							mLeDeviceListAdapter.notifyDataSetChanged();
						}
					});
					// }
				}
			} else if (BleService.BLE_NO_BT_ADAPTER.equals(action)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(DeviceScanActivity.this,
								"No bluetooth adapter", Toast.LENGTH_SHORT)
								.show();
						finish();
					}
				});
			} else if (BleService.EXTRA_SCAN_RECORD.equals(action)) {

			}
		}
	};

	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.devicescan_layout);
		mHandler = new Handler();
		initView();
	}

	private void initView() {
		rightTitleTv = (TextView) findViewById(R.id.title_text_right);
		progressBar = (ProgressBar) findViewById(R.id.title_progressbar);
		rightTitleTv.setText("停止");
		rightTitleTv.setVisibility(View.VISIBLE);
		rightTitleTv.setOnClickListener(this);
		progressBar.setVisibility(View.VISIBLE);
		listView = (ListView) findViewById(R.id.devicescan_listview);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_text_right:
			if ("搜索".equals(rightTitleTv.getText())) {
				rightTitleTv.setText("停止");
				progressBar.setVisibility(View.VISIBLE);
				mLeDeviceListAdapter.clear();
				scanLeDevice(true);
			} else {
				rightTitleTv.setText("搜索");
				progressBar.setVisibility(View.GONE);
				scanLeDevice(false);
			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mBleReceiver, BleService.getIntentFilter());

		// Ensures Bluetooth is enabled on the device. If Bluetooth is not
		// currently enabled,
		// fire an intent to display a dialog asking the user to grant
		// permission to enable it.
		if (mBle != null && !mBle.adapterEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

		// Initializes list view adapter.
		mLeDeviceListAdapter = new LeDeviceListAdapter();
		listView.setAdapter(mLeDeviceListAdapter);
		scanLeDevice(true);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				final BluetoothDevice device = mLeDeviceListAdapter
						.getDevice(position);
				if (device == null)
					return;
				Intent intent = null;
				if (device.getName().contains("inI")) {
					System.out.println("胰岛素泵");
					intent = new Intent(DeviceScanActivity.this,
							WNInsulinPump.class);
					System.out.println(device.getAddress() + "***设置泵MAC地址");
					userSP.setPumpName(device.getName());
					userSP.setPumpMac(device.getAddress());
					startActivity(intent);
				} else if (device.getName().contains("inZ")) {
					System.out.println("注射泵");
					intent = new Intent(DeviceScanActivity.this, PumpUser.class);
					System.out.println(device.getAddress() + "***设置泵MAC地址");
					userSP.setPumpName(device.getName());
					userSP.setPumpMac(device.getAddress());
					startActivity(intent);
				}
				if (mBle != null) {
					mBle.stopScan();
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mBleReceiver);
		scanLeDevice(false);
		mLeDeviceListAdapter.clear();
	}

	private void scanLeDevice(final boolean enable) {
		BleApplication app = (BleApplication) getApplication();
		mBle = app.getIBle();
		if (mBle == null) {
			return;
		}
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					if (mBle != null) {
						mBle.stopScan();
						progressBar.setVisibility(View.GONE);
						rightTitleTv.setText("搜索");
					}
					invalidateOptionsMenu();
				}
			}, SCAN_PERIOD);

			mScanning = true;
			if (mBle != null) {
				mBle.startScan();
			}
		} else {
			mScanning = false;
			if (mBle != null) {
				mBle.stopScan();
				progressBar.setVisibility(View.GONE);
				rightTitleTv.setText("搜索");
			}
		}
		invalidateOptionsMenu();
	}

	// Adapter for holding devices found through scanning.
	private class LeDeviceListAdapter extends BaseAdapter {
		private ArrayList<BluetoothDevice> mLeDevices;
		private LayoutInflater mInflator;

		public LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflator = DeviceScanActivity.this.getLayoutInflater();
		}

		public void addDevice(BluetoothDevice device) {
			if (!mLeDevices.contains(device)) {
				mLeDevices.add(device);
			}
		}

		public BluetoothDevice getDevice(int position) {
			return mLeDevices.get(position);
		}

		public void clear() {
			mLeDevices.clear();
		}

		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int i) {
			return mLeDevices.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder;
			// General ListView optimization code.
			if (view == null) {
				view = mInflator.inflate(R.layout.listitem_device, null);
				viewHolder = new ViewHolder();
				viewHolder.deviceAddress = (TextView) view
						.findViewById(R.id.device_address);
				viewHolder.deviceName = (TextView) view
						.findViewById(R.id.device_name);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			BluetoothDevice device = mLeDevices.get(i);
			final String deviceName = device.getName();
			if (deviceName != null && deviceName.length() > 0)
				viewHolder.deviceName.setText(deviceName);
			else
				viewHolder.deviceName.setText(R.string.unknown_device);
			viewHolder.deviceAddress.setText(device.getAddress());
			return view;
		}
	}

	static class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
	}
}