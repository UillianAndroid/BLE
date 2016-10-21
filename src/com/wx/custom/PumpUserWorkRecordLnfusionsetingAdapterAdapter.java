package com.wx.custom;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wx.model.InfusionSetting;
import com.wx.model.WNViewHolder;
import com.wx.pumptest.R;

public class PumpUserWorkRecordLnfusionsetingAdapterAdapter extends BaseAdapter {
	private Context context;
	private int index;
	private List<InfusionSetting> infusionSettings = new ArrayList<InfusionSetting>();

	public PumpUserWorkRecordLnfusionsetingAdapterAdapter(Context context,
			int index, List<InfusionSetting> infusionSettings) {
		this.context = context;
		this.index = index;
		this.infusionSettings = infusionSettings;
	}

	@Override
	public int getCount() {
		return infusionSettings.get(index + 1).getInfusionSet().size();
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
	public View getView(int position, View convertView, ViewGroup arg2) {
		WNViewHolder vh;
		if (convertView == null) {
			convertView = ((Activity) context).getLayoutInflater().inflate(
					R.layout.pump_infusion_setting_item_list, null);
			vh = new WNViewHolder();
			vh.tv1 = (TextView) convertView
					.findViewById(R.id.pump_infusion_setting_item_list_time);
			vh.tv2 = (TextView) convertView
					.findViewById(R.id.pump_infusion_setting_item_list_dose);
			convertView.setTag(vh);
		} else {
			vh = (WNViewHolder) convertView.getTag();
		}
		vh.tv1.setText(infusionSettings.get(index + 1).getInfusionSet()
				.get(position).getTime());
		vh.tv2.setText(infusionSettings.get(index + 1).getInfusionSet()
				.get(position).getDose());
		return convertView;
	}
}
