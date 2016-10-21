package com.wx.custom;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wx.model.WNViewHolder;
import com.wx.pumptest.R;
import com.wx.utils.WNHexChange;

public class PumpUserWorkRecordLnfusionAdapter extends BaseAdapter {
	private Context context;
	private List<String> datas = new ArrayList<String>();

	public PumpUserWorkRecordLnfusionAdapter(Context context, List<String> datas) {
		this.context = context;
		this.datas = datas;
	}

	@Override
	public int getCount() {
		return datas.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		WNViewHolder vh;
		if (convertView == null) {
			convertView = ((Activity) context).getLayoutInflater().inflate(
					R.layout.pump_user_work_record_infusion_lsitem, null);
			vh = new WNViewHolder();
			vh.tv1 = (TextView) convertView
					.findViewById(R.id.pump_record_infusion_record_time);
			vh.tv2 = (TextView) convertView
					.findViewById(R.id.pump_record_infusion_record_dose);
			convertView.setTag(vh);
		} else {
			vh = (WNViewHolder) convertView.getTag();
		}
		for (int i = 0; i < datas.size(); i++) {
			List<String> strings = WNHexChange.getPumpData(datas.get(i)
					.toString());
			if (i == position) {
				String minuteTemp = WNHexChange.intToString(strings.get(5)
						.toString());
				if (minuteTemp.length() != 2) {
					minuteTemp = "0" + minuteTemp;
				}
				vh.tv1.setText(WNHexChange.intToString(strings.get(4).toString())
						+ ":" + minuteTemp);
				vh.tv2.setText(WNHexChange.intToString(strings.get(6).toString()
						+ strings.get(7).toString())
						+ "μg");
			}
		}
		return convertView;
	}
}
