package com.wx.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserSharePreference {
	private static UserSharePreference userSharePreference = null;
	private SharedPreferences preferences;

	public static UserSharePreference getInstance(Context context) {
		if (userSharePreference == null) {
			userSharePreference = new UserSharePreference(context);
		}
		return userSharePreference;
	}

	public UserSharePreference(Context context) {
		preferences = context
				.getSharedPreferences("pump", Context.MODE_PRIVATE);
	}

	// 设置泵序列号
	public void setPumpName(String value) {
		Editor editor = preferences.edit();
		editor.putString("pump_name", value);
		editor.commit();
	}

	// 得到泵序列号
	public String getPumpName() {
		return preferences.getString("pump_name", null);
	}

	// 设置泵MAC地址
	public void setPumpMac(String value) {
		Editor editor = preferences.edit();
		editor.putString("pump_mac", value);
		editor.commit();
	}

	// 得到泵MAC地址
	public String getPumpMac() {
		return preferences.getString("pump_mac", null);
	}
}
