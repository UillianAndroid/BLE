package com.wx.custom;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wx.model.WNInsulinRecord;
import com.wx.model.WNViewHolder;
import com.wx.pumptest.R;

public class WNInsulinPumpRecordAdapter extends BaseAdapter {
	private List<WNInsulinRecord> insulinRecords;
	private Context context;

	public WNInsulinPumpRecordAdapter(Context context,
			List<WNInsulinRecord> insulinRecords) {
		this.insulinRecords = insulinRecords;
		this.context = context;
	}

	@Override
	public int getCount() {
		return insulinRecords.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View converntView, ViewGroup arg2) {
		WNViewHolder viewHolder = null;
		if (converntView == null) {
			converntView = LayoutInflater.from(context).inflate(
					R.layout.wn_insulin_workstatus_item, null);
			viewHolder = new WNViewHolder();
			viewHolder.tv1 = (TextView) converntView
					.findViewById(R.id.wn_insulin_pump_record_tv1);
			viewHolder.tv2 = (TextView) converntView
					.findViewById(R.id.wn_insulin_pump_record_tv2);
			converntView.setTag(viewHolder);
		} else {
			viewHolder = (WNViewHolder) converntView.getTag();
		}
		viewHolder.tv1.setText(insulinRecords.get(position).getLeftStr());
		viewHolder.tv2.setText(insulinRecords.get(position).getRightStr());
		return converntView;
	}

}
