package com.wx.custom;

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

public class PumpUserWorkRecordTotaldailyAdapter extends BaseAdapter {

	private Context context;
	private List<String> datas;

	public PumpUserWorkRecordTotaldailyAdapter(Context context,
			List<String> datas) {
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
		WNViewHolder holder;
		if (convertView == null) {
			convertView = ((Activity) context).getLayoutInflater().inflate(
					R.layout.pump_user_work_record_totaldaiy_lsitem, null);
			holder = new WNViewHolder();
			holder.tv1 = (TextView) convertView
					.findViewById(R.id.pump_totaldaily_data_tv);
			holder.tv2 = (TextView) convertView
					.findViewById(R.id.pump_totaldaily_max_tv);
			holder.tv3 = (TextView) convertView
					.findViewById(R.id.pump_totaldaily_min_tv);
			holder.tv4 = (TextView) convertView
					.findViewById(R.id.pump_totaldaily_avg_tv);
			holder.tv5 = (TextView) convertView
					.findViewById(R.id.pump_totaldaily_total_tv);
			convertView.setTag(holder);
		} else {
			holder = (WNViewHolder) convertView.getTag();
		}
		for (int i = 0; i < datas.size(); i++) {
			List<String> strings = WNHexChange.getPumpData(datas.get(i)
					.toString());
			if (i == position) {
				// 日期
				holder.tv1.setText(WNHexChange.intToString(strings.get(4)
						.toString())
						+ context.getResources().getString(R.string.month)
						+ WNHexChange.intToString(strings.get(5).toString())
						+ context.getResources().getString(
								R.string.daily_record));
				// 最大量
				String max = WNHexChange.intToString(strings.get(6).toString()
						+ strings.get(7).toString());
				String maxInt = WNHexChange.stringToInt(max) / 10 + "."
						+ WNHexChange.stringToInt(max) % 10;
				holder.tv2.setText(maxInt + "ug");
				// 最小量
				String min = WNHexChange.intToString(strings.get(8).toString()
						+ strings.get(9).toString());
				String minInt = WNHexChange.stringToInt(min) / 10 + "."
						+ WNHexChange.stringToInt(min) % 10;
				holder.tv3.setText(minInt + "ug");
				// 平均量
				String avg = WNHexChange.intToString(strings.get(10).toString()
						+ strings.get(11).toString());
				String avgInt = WNHexChange.stringToInt(avg) / 10 + "."
						+ WNHexChange.stringToInt(avg) % 10;
				holder.tv4.setText(avgInt + "ug");
				// 总量
				String total = WNHexChange.intToString(strings.get(12)
						.toString() + strings.get(13).toString());
				String totalInt = WNHexChange.stringToInt(total) / 10 + "."
						+ WNHexChange.stringToInt(total) % 10;
				holder.tv5.setText(totalInt + "ug");
			}
		}

		return convertView;
	}
}
