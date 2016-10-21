package com.wx.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.wx.pumptest.R;
import com.wx.ui.WNUtils;
import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.BleGattService;
import com.xtremeprog.sdk.ble.IBle;

/*
 * @wx
 * 控制泵设备蓝牙操作
 */
public class WNBleControl {
	public IBle mBle;
	public Handler mHandler = new Handler();
	public final long SCAN_PERIOD = 10000;
	private Activity activity;
	private static WNBleControl bleControl;
	public static BleGattCharacteristic mCharacteristic = null;
	public final static String UUID_NOTIFY = "0000ffe1-0000-1000-8000-00805f9b34fb";
	public final static String UUID_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb";
	public final String LIST_NAME = "NAME";
	public final static String LIST_UUID = "UUID";
	private ArrayList<ArrayList<BleGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BleGattCharacteristic>>();

	public WNBleControl(Activity activity, IBle mBle) {
		this.mBle = mBle;
		this.activity = activity;
	}

	public static WNBleControl getInstance(Activity activity, IBle mBle) {

		if (bleControl == null) {
			bleControl = new WNBleControl(activity, mBle);
		}
		return bleControl;
	}

	// 设置蓝牙可见性，500表示可见时间（单位：秒），当值大于300时默认为300
	private void openBluetooth() {
		Intent discoverIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
				500);
		activity.startActivity(discoverIntent);
	}

	// 连接
	public void connect(String deviceAddress) {
		mBle.requestConnect(deviceAddress);
	}

	// 断开连接
	public void disConnect(String deviceAddress) {
		if (mBle != null) {
			mBle.disconnect(deviceAddress);
		}
	}

	// 开启通知
	public boolean startNotify(Activity activity, String deviceAddress) {
		boolean isStart = false;
		try {
			// if (mCharacteristic == null) {
			mCharacteristic = mBle.getService(deviceAddress,
					UUID.fromString(UUID_SERVICE)).getCharacteristic(
					UUID.fromString(UUID_NOTIFY));
			// }
		} catch (Exception e) {
			disConnect(deviceAddress);
			e.printStackTrace();
			return isStart;
		}
		if (!isStart) {
			mBle.requestCharacteristicNotification(deviceAddress,
					mCharacteristic);
			isStart = true;
		} else {
			mBle.requestCharacteristicNotification(deviceAddress,
					mCharacteristic);
			isStart = true;
		}
		return isStart;
	}

	// 写入指令
	public void toWrite(String deviceAddress, String code) {
		try {
			// if (mCharacteristic == null) {
			mCharacteristic = mBle.getService(deviceAddress,
					UUID.fromString(UUID_SERVICE)).getCharacteristic(
					UUID.fromString(UUID_NOTIFY));
			// }
			byte[] data = hex2byte(code);
			mCharacteristic.setValue(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mBle.requestWriteCharacteristic(deviceAddress, mCharacteristic, "");
	}

	public static byte[] hex2byte(String hex) {
		String digital = "0123456789ABCDEF";
		char[] hex2char = hex.toCharArray();
		byte[] bytes = new byte[hex.length() / 2];
		int temp;
		for (int i = 0; i < bytes.length; i++) {
			temp = digital.indexOf(hex2char[2 * i]) * 16;
			temp += digital.indexOf(hex2char[2 * i + 1]);
			bytes[i] = (byte) (temp & 0xff);
		}
		return bytes;
	}

	public List<BleGattService> getGattServices(String deviceAddress) {
		List<BleGattService> gattServices = mBle.getServices(deviceAddress);
		return gattServices;
	}

	public void displayGattServices(String deviceAddress) {
		List<BleGattService> gattServices = mBle.getServices(deviceAddress);
		if (gattServices == null)
			return;
		String uuid = null;
		String unknownServiceString = activity.getResources().getString(
				R.string.wn_unknown_service);
		String unknownCharaString = activity.getResources().getString(
				R.string.wn_unknown_characteristic);
		ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
		ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
		mGattCharacteristics = new ArrayList<ArrayList<BleGattCharacteristic>>();

		// Loops through available GATT Services.
		for (BleGattService gattService : gattServices) {
			HashMap<String, String> currentServiceData = new HashMap<String, String>();
			uuid = gattService.getUuid().toString().toUpperCase();

			currentServiceData.put(LIST_NAME, WNUtils.BLE_SERVICES
					.containsKey(uuid) ? WNUtils.BLE_SERVICES.get(uuid)
					: unknownServiceString);
			currentServiceData.put(LIST_UUID, uuid);
			gattServiceData.add(currentServiceData);

			ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
			List<BleGattCharacteristic> gattCharacteristics = gattService
					.getCharacteristics();
			ArrayList<BleGattCharacteristic> charas = new ArrayList<BleGattCharacteristic>();

			// Loops through available Characteristics.
			for (BleGattCharacteristic gattCharacteristic : gattCharacteristics) {
				charas.add(gattCharacteristic);
				HashMap<String, String> currentCharaData = new HashMap<String, String>();
				uuid = gattCharacteristic.getUuid().toString().toUpperCase();
				currentCharaData
						.put(LIST_NAME,
								WNUtils.BLE_CHARACTERISTICS.containsKey(uuid) ? WNUtils.BLE_CHARACTERISTICS
										.get(uuid) : unknownCharaString);
				currentCharaData.put(LIST_UUID, uuid);
				gattCharacteristicGroupData.add(currentCharaData);
			}
			mGattCharacteristics.add(charas);
			gattCharacteristicData.add(gattCharacteristicGroupData);
		}
	}
}
