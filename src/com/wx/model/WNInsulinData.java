package com.wx.model;


public class WNInsulinData {
	public WNInsulinBaseTotal baseTotal;
	public WNInsulinHighDose highDose;
	public WNInsulinPumpInfo pumpInfo;
	public WNInsulinPumpJob pumpJob;


	public WNInsulinBaseTotal getBaseTotal() {
		return baseTotal;
	}

	public void setBaseTotal(WNInsulinBaseTotal baseTotal) {
		this.baseTotal = baseTotal;
	}

	public WNInsulinHighDose getHighDose() {
		return highDose;
	}

	public void setHighDose(WNInsulinHighDose highDose) {
		this.highDose = highDose;
	}

	public WNInsulinPumpInfo getPumpInfo() {
		return pumpInfo;
	}

	public void setPumpInfo(WNInsulinPumpInfo pumpInfo) {
		this.pumpInfo = pumpInfo;
	}

	public WNInsulinPumpJob getPumpJob() {
		return pumpJob;
	}

	public void setPumpJob(WNInsulinPumpJob pumpJob) {
		this.pumpJob = pumpJob;
	}
}
