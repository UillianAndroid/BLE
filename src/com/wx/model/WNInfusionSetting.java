package com.wx.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WNInfusionSetting {
	private String date;
	private List<WNInfusionSet> infusionSet;

	public String getDate() {
		return date;
	}

	public void setDate(String data) {
		this.date = data;
	}

	public List<WNInfusionSet> getInfusionSet() {
		return infusionSet;
	}

	public void setInfusionSet(List<WNInfusionSet> infusionSet) {
		this.infusionSet = infusionSet;
	}

	// 查看最近一次输注设置
	public static List<WNInfusionSetting> getLastInfusionsetting(String result) {
		List<WNInfusionSetting> infusionSettings = new ArrayList<WNInfusionSetting>();
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (jsonObject.has("data")) {
				JSONObject dataJson = jsonObject.getJSONObject("data");
				if (dataJson.has("pump_data")) {
					WNInfusionSetting infusionSetting = new WNInfusionSetting();
					JSONObject pumpDataJson = dataJson
							.getJSONObject("pump_data");
					if (pumpDataJson.has("date")) {
						infusionSetting.setDate(pumpDataJson.getString("date"));
					}
					if (pumpDataJson.has("parsed_content")) {
						JSONObject parsedContentJson = pumpDataJson
								.getJSONObject("parsed_content");
						if (parsedContentJson.has("infusion_set")) {
							JSONArray infusionSetArray = parsedContentJson
									.getJSONArray("infusion_set");
							List<WNInfusionSet> infusionSets = new ArrayList<WNInfusionSet>();
							for (int j = 0; j < infusionSetArray.length(); j++) {
								JSONObject infusionSetJson = (JSONObject) infusionSetArray
										.get(j);
								WNInfusionSet infusionSet = new WNInfusionSet();
								if (infusionSetJson.has("dose")) {
									infusionSet.setDose(infusionSetJson
											.getString("dose"));
								}
								if (infusionSetJson.has("time")) {
									infusionSet.setTime(infusionSetJson
											.getString("time"));
								}
								infusionSets.add(infusionSet);
							}
							infusionSetting.setInfusionSet(infusionSets);
						}
						infusionSettings.add(infusionSetting);
					}
				}
			}
			return infusionSettings;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 查看历史输注设置
	public static List<WNInfusionSetting> getHistoryInfusionsetting(
			String result) {
		List<WNInfusionSetting> infusionSettings = new ArrayList<WNInfusionSetting>();
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (jsonObject.has("data")) {
				JSONObject dataJson = jsonObject.getJSONObject("data");
				if (dataJson.has("pump_data")) {
					JSONArray pumpDataArray = dataJson
							.getJSONArray("pump_data");
					for (int i = 0; i < pumpDataArray.length(); i++) {
						WNInfusionSetting infusionSetting = new WNInfusionSetting();
						JSONObject pumpDataJson = pumpDataArray
								.getJSONObject(i);
						if (pumpDataJson.has("date")) {
							infusionSetting.setDate(pumpDataJson
									.getString("date"));
						}
						if (pumpDataJson.has("parsed_content")) {
							JSONObject parsedContentJson = pumpDataJson
									.getJSONObject("parsed_content");
							if (parsedContentJson.has("infusion_set")) {
								JSONArray infusionSetArray = parsedContentJson
										.getJSONArray("infusion_set");
								List<WNInfusionSet> infusionSets = new ArrayList<WNInfusionSet>();
								for (int j = 0; j < infusionSetArray.length(); j++) {
									JSONObject infusionSetJson = (JSONObject) infusionSetArray
											.get(j);
									WNInfusionSet infusionSet = new WNInfusionSet();
									if (infusionSetJson.has("dose")) {
										infusionSet.setDose(infusionSetJson
												.getString("dose"));
									}
									if (infusionSetJson.has("time")) {
										infusionSet.setTime(infusionSetJson
												.getString("time"));
									}
									infusionSets.add(infusionSet);
								}
								infusionSetting.setInfusionSet(infusionSets);
							}
						}
						infusionSettings.add(infusionSetting);
					}
				}
			}
			return infusionSettings;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}