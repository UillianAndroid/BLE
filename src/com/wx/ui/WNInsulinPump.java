package com.wx.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.wx.pumptest.R;

public class WNInsulinPump extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wn_insulin_pump);
		back(R.string.w_insulin_pump);
		initView();
	}

	private void initView() {
		findViewById(R.id.wn_insulin_pump_information_rl).setOnClickListener(
				this);
		findViewById(R.id.wn_insulin_pump_workrecord_rl).setOnClickListener(
				this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.wn_insulin_pump_information_rl)
			startActivity(WNInsulinPumpInformaton.class);

		if (id == R.id.wn_insulin_pump_workrecord_rl)
			startActivity(WNInsulinPumpWorkRecord.class);
	}

	private <T> void startActivity(Class<T> cls) {
		startActivity(new Intent(this, cls));
	}
}
