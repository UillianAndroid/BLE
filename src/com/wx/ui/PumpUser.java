package com.wx.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wx.pumptest.R;

public class PumpUser extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pump_user);
		back(R.string.p_my_pump_data);
	}

	// 用户信息
	public void clickPumpUserInformation(View view) {
		Intent intent = new Intent(this, PumpUserInformationNew.class);
		startActivity(intent);
	}

	// 泵工作记录
	public void clickPumpWorkrecord(View view) {
		Intent intent = new Intent(this, PumpUserWorkRecord.class);
		startActivity(intent);

	}

	// 泵工作状态
	public void clickPumpWorkStatus(View view) {
		Intent intent = new Intent(this, PumpUserWorkStatusNew.class);
		startActivity(intent);
	}

}
