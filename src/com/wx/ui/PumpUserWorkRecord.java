package com.wx.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wx.pumptest.R;

public class PumpUserWorkRecord extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pump_user_work_record);
		back(R.string.p_pump_work);
	}

	// 输注记录
	public void clickPumpInfusionrecord(View view) {
		Intent intent = new Intent(this, PumpUserWorkRecordInfusionNew.class);
		startActivity(intent);
	}

	// 日总记录
	public void clickPumpTotaldailyrecord(View view) {
		Intent intent = new Intent(this, PumpUserWorkRecordTotaldailyNew.class);
		startActivity(intent);
	}

	// 报警记录
	public void clickPumpCallPoliceyrecord(View view) {
		Intent intent = new Intent(this, PumpUserWorkRecordCallpoliceNew.class);
		startActivity(intent);
	}

	// 输注设置
	public void clickPumpInfusionseting(View view) {
		Intent intent = new Intent(this,
				PumpUserWorkRecordInfusionSetingNew.class);
		startActivity(intent);
	}
}
