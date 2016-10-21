package com.wx.model;

import java.util.List;

public class InfusionSetting {
	private String date;
	private List<InfusionSet> infusionSet;

	public String getDate() {
		return date;
	}

	public void setDate(String data) {
		this.date = data;
	}

	public List<InfusionSet> getInfusionSet() {
		return infusionSet;
	}

	public void setInfusionSet(List<InfusionSet> infusionSet) {
		this.infusionSet = infusionSet;
	}

}