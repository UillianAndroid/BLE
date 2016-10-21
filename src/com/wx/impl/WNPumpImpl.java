package com.wx.impl;

import android.app.Activity;

import com.wx.db.UserSharePreference;
import com.wx.utils.WNBleControl;
import com.xtremeprog.sdk.ble.IBle;

/*
 * 此类为泵蓝牙功能中用到的公共方法
 */
public class WNPumpImpl {

	// 开启notify
	public static boolean startNotify(Activity activity, final IBle mBle,
			UserSharePreference userSP) {
		return WNBleControl.getInstance(activity, mBle).startNotify(activity,
				userSP.getPumpMac());
	}
}
