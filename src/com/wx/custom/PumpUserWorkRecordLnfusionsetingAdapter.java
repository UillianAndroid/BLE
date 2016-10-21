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

public class PumpUserWorkRecordLnfusionsetingAdapter extends BaseAdapter {

	private Context context;
	private List<InfusionSetting> infusionSettings = new ArrayList<InfusionSetting>();

	public PumpUserWorkRecordLnfusionsetingAdapter(Context context,
			List<InfusionSetting> infusionSettings) {
		this.context = context;
		this.infusionSettings = infusionSettings;
	}

	@Override
	public int getCount() {
		return infusionSettings.size() - 1;
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
					R.layout.pump_user_work_record_infusionseting_lsitem, null);
			vh = new WNViewHolder();
			vh.tv1 = (TextView) convertView
					.findViewById(R.id.pump_user_work_record_infusionseting_istiem_time);
			vh.lv = (CustomListview) convertView
					.findViewById(R.id.pump_user_work_record_infusionseting_istiem_ls);
			convertView.setTag(vh);
		} else {
			vh = (WNViewHolder) convertView.getTag();
		}
		vh.tv1.setText(infusionSettings.get(position + 1).getDate());
		PumpUserWorkRecordLnfusionsetingAdapterAdapter adapter = new PumpUserWorkRecordLnfusionsetingAdapterAdapter(
				context, position, infusionSettings);
		vh.lv.setAdapter(adapter);
		return convertView;
	}
}
