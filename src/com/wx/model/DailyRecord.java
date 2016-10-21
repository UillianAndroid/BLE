package com.wx.model;

public class DailyRecord {
	private String month;
	private String day;
	private String maximumDose;
	private String minimumDose;
	private String averageDose;
	private String totalDose;

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getMaximumDose() {
		return maximumDose;
	}

	public void setMaximumDose(String maximumDose) {
		this.maximumDose = maximumDose;
	}

	public String getMinimumDose() {
		return minimumDose;
	}

	public void setMinimumDose(String minimumDose) {
		this.minimumDose = minimumDose;
	}

	public String getAverageDose() {
		return averageDose;
	}

	public void setAverageDose(String averageDose) {
		this.averageDose = averageDose;
	}

	public String getTotalDose() {
		return totalDose;
	}

	public void setTotalDose(String totalDose) {
		this.totalDose = totalDose;
	}

}
