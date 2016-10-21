package com.wx.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.wx.pumptest.R;

public class WNInsulinPumpWorkRecord extends BaseActivity implements
		OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wn_insulinpump_workrecord);
		initView();
		back(R.string.wn_p_pump_work);
	}

	private void initView() {
		findViewById(R.id.wn_insulin_pump_record_ll1).setOnClickListener(this);
		findViewById(R.id.wn_insulin_pump_record_ll2).setOnClickListener(this);
		findViewById(R.id.wn_insulin_pump_record_ll3).setOnClickListener(this);
		findViewById(R.id.wn_insulin_pump_record_ll4).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.wn_insulin_pump_record_ll1)
			startActivity(WNInsulinPumpRecord.TOTALDAILY);
		if (id == R.id.wn_insulin_pump_record_ll2)
			startActivity(WNInsulinPumpRecord.LARGEDOSE);
		if (id == R.id.wn_insulin_pump_record_ll3)
			startActivity(WNInsulinPumpRecord.BASICS);
		if (id == R.id.wn_insulin_pump_record_ll4)
			startActivity(WNInsulinPumpRecord.ALARM);
	}

	private void startActivity(String tag) {
		Intent intent = new Intent();
		intent.setClass(this, WNInsulinPumpRecord.class);
		intent.putExtra(WNInsulinPumpRecord.RECORD_TAG, tag);
		startActivity(intent);
	}
}
