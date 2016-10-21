package com.wx.model;

import android.app.Activity;

public class WNInsulinRecord {
	public String title;
	public String leftStr;
	public String rightStr;
	private static Activity context;

	public WNInsulinRecord() {
	}

	@SuppressWarnings("static-access")
	public WNInsulinRecord(Activity context) {
		this.context = context;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLeftStr() {
		return leftStr;
	}

	public void setLeftStr(String leftStr) {
		this.leftStr = leftStr;
	}

	public String getRightStr() {
		return rightStr;
	}

	public void setRightStr(String rightStr) {
		this.rightStr = rightStr;
	}
}
