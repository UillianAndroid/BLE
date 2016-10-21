package com.wx.model;

public class PumpWorkStatus {
	private String quantityOfElectricity;
	private String dose;
	private String obstructionSign;
	private String concentration;
	private String infusionInterval;
	private String infusionDose;

	public String getQuantityOfElectricity() {
		return quantityOfElectricity;
	}

	public void setQuantityOfElectricity(String quantityOfElectricity) {
		this.quantityOfElectricity = quantityOfElectricity;
	}

	public String getDose() {
		return dose;
	}

	public void setDose(String dose) {
		this.dose = dose;
	}

	public String getObstructionSign() {
		return obstructionSign;
	}

	public void setObstructionSign(String obstructionSign) {
		this.obstructionSign = obstructionSign;
	}

	public String getConcentration() {
		return concentration;
	}

	public void setConcentration(String concentration) {
		this.concentration = concentration;
	}

	public String getInfusionInterval() {
		return infusionInterval;
	}

	public void setInfusionInterval(String infusionInterval) {
		this.infusionInterval = infusionInterval;
	}

	public String getInfusionDose() {
		return infusionDose;
	}

	public void setInfusionDose(String infusionDose) {
		this.infusionDose = infusionDose;
	}

}
