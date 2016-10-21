package com.wx.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.wx.custom.WNDialogUtil;
import com.wx.db.UserSharePreference;
import com.wx.pumptest.R;
import com.xtremeprog.sdk.ble.IBle;

public class BaseActivity extends Activity {
	public UserSharePreference userSP;
	public Activity activity;
	public static IBle mBle;
	public WNDialogUtil dialogUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userSP = UserSharePreference.getInstance(this);
		dialogUtil = new WNDialogUtil(this);
		BleApplication app = (BleApplication) getApplication();
		mBle = app.getIBle();
	}

	public void back(int id) {
		TextView titleIv = (TextView) findViewById(R.id.title_text_center);
		if (id != -1) {
			titleIv.setText(getResources().getString(id));
		}
		ImageView backIv = (ImageView) findViewById(R.id.title_image_left);
		backIv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
}
