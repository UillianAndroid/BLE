package com.wx.model;

import java.util.List;

public class PumpDataObj {
	private String date;
	private String category;
	private List<DailyRecord> dailyRecords;
	private PumpWorkStatus pumpWorkStatus;
	private List<AlarmRecord> alarmRecords;
	private List<InfusionRecord> infusionRecords;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<DailyRecord> getDailyRecords() {
		return dailyRecords;
	}

	public void setDailyRecords(List<DailyRecord> dailyRecords) {
		this.dailyRecords = dailyRecords;
	}

	public PumpWorkStatus getPumpWorkStatus() {
		return pumpWorkStatus;
	}

	public void setPumpWorkStatus(PumpWorkStatus pumpWorkStatus) {
		this.pumpWorkStatus = pumpWorkStatus;
	}

	public List<AlarmRecord> getAlarmRecords() {
		return alarmRecords;
	}

	public void setAlarmRecords(List<AlarmRecord> alarmRecords) {
		this.alarmRecords = alarmRecords;
	}

	public List<InfusionRecord> getInfusionRecords() {
		return infusionRecords;
	}

	public void setInfusionRecords(List<InfusionRecord> infusionRecords) {
		this.infusionRecords = infusionRecords;
	}

}
