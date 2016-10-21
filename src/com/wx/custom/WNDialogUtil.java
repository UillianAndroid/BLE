package com.wx.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.wx.pumptest.R;

/*
 * @atyun_wx
 */
public class WNDialogUtil {
	Activity context;
	AlertDialog dialog;

	public WNDialogUtil(Activity context) {
		this.context = context;
	}

	public void showDialogLoading(boolean isShow) {
		try {
			if (dialog != null) {
				dialog.dismiss();
				dialog = null;
			}
			if (isShow) {
				LayoutInflater inflater = LayoutInflater.from(context);
				View v = inflater.inflate(R.layout.wn_dialog_loading, null);
				dialog = new AlertDialog.Builder(context).setView(
						inflater.inflate(R.layout.wn_dialog_loading, null))
						.create();
				dialog.show();
				ViewGroup.LayoutParams params = new LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT);
				dialog.getWindow().setContentView(v, params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
