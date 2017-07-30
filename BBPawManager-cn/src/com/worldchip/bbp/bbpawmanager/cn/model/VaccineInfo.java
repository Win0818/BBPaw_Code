package com.worldchip.bbp.bbpawmanager.cn.model;

public class VaccineInfo {
	
	/** 年龄 以月为单位**/
	private String age;
	/** 年龄标题**/
	private String ageTitle;
	/** 疫苗名称**/
	private String vaccineTypeName;
	/** 疫苗种类**/
	private String vaccineType;
	/** 说明**/
	private String vaccineExplain;
	/** 接种日期**/
	private String date;
	
	private boolean checked;
	
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getVaccineType() {
		return vaccineType;
	}
	
	public String getVaccineTypeName() {
		return vaccineTypeName;
	}
	public void setVaccineTypeName(String vaccineTypeName) {
		this.vaccineTypeName = vaccineTypeName;
	}
	public void setVaccineType(String vaccineType) {
		this.vaccineType = vaccineType;
	}
	public String getVaccineExplain() {
		return vaccineExplain;
	}
	public void setVaccineExplain(String vaccineExplain) {
		this.vaccineExplain = vaccineExplain;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getAgeTitle() {
		return ageTitle;
	}
	public void setAgeTitle(String ageTitle) {
		this.ageTitle = ageTitle;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "VaccineInfo == {age = "+this.age + " ageTitle = " + this.ageTitle+ " vaccineType = "+this.vaccineType
				+" vaccineExplain = "+this.vaccineExplain +" date = "+this.date +" checked = "+checked+"}";
	}
	
	
}
