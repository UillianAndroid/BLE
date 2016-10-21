package com.wx.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.wx.pumptest.R;

public class MainActivity extends BaseActivity {
	private TextView textView;
	private Button button;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView = (TextView) findViewById(R.id.main_textview);
		button = (Button) findViewById(R.id.main_button);
		textView.setTextColor(Color.RED);
		System.out.println("当前手机 " + android.os.Build.MODEL + ","
				+ android.os.Build.VERSION.SDK + ","
				+ android.os.Build.VERSION.RELEASE);
		String api = android.os.Build.VERSION.SDK;
		textView.append("当前手机型号为:" + android.os.Build.MODEL);
		textView.append("\n当前手机android系统版本为:"
				+ android.os.Build.VERSION.RELEASE);
		if (Integer.parseInt(api) >= 18) {
			textView.append("\n该系统支持连接泵设备，请继续......");
			button.setText("继续");
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					startActivity(new Intent(MainActivity.this,
							DeviceScanActivity.class));
				}
			});
		} else {
			textView.append("\n该系统不支持连接泵设备，很抱歉......");
			button.setText("退出");
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					finish();
				}
			});
		}
	}
}
