package com.arogita.appclient.report;

public class ReportProfile {
	
	private String dateStr = null;
	private String patientId;
	private String reportId;
	private String saturation = null;
	private String pulse = null;
	private String temp = null;
	private String nibp = null;
	private String heartRate = null;
	private String respRate = null;
	private String stLevel = null;
	private String arrCode = null;
	private String notes = null;
	
	private String chartFileName = null;
	
	
	public ReportProfile(String dStr, String pId, String rId){
		this.dateStr = dStr;
		this.patientId = pId;
		this.reportId = rId;
	}
	
	public ReportProfile(String dStr, String sat, String pu, String temp, String bp,
			String hr, String rr, String st, String ar, String note, String cdFile, String id, String repId){
		this.dateStr = dStr;
		this.patientId = id;
		this.reportId = repId;
		this.saturation = sat;
		this.pulse = pu;
		this.temp = temp;
		this.nibp = bp;
		this.heartRate = hr;
		this.respRate = rr;
		this.stLevel = st;
		this.arrCode = ar;
		this.notes = note;
		this.chartFileName = cdFile;
	}

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String pId) {
		this.patientId = pId;
	}

	public String getSaturation() {
		return saturation;
	}

	public void setSaturation(String saturation) {
		this.saturation = saturation;
	}

	public String getPulse() {
		return pulse;
	}

	public void setPulse(String pulse) {
		this.pulse = pulse;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getNibp() {
		return nibp;
	}

	public void setNibp(String nibp) {
		this.nibp = nibp;
	}

	public String getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(String heartRate) {
		this.heartRate = heartRate;
	}

	public String getRespRate() {
		return respRate;
	}

	public void setRespRate(String respRate) {
		this.respRate = respRate;
	}

	public String getStLevel() {
		return stLevel;
	}

	public void setStLevel(String stLevel) {
		this.stLevel = stLevel;
	}

	public String getArrCode() {
		return arrCode;
	}

	public void setArrCode(String arrCode) {
		this.arrCode = arrCode;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public String toString(){
		return getDateStr();
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getChartFileName() {
		return chartFileName;
	}

	public void setChartFileName(String chartFileName) {
		this.chartFileName = chartFileName;
	}

}
