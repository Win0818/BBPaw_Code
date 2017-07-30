package com.worldchip.bbp.ect.entity;

import java.io.Serializable;

public class AlarmInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer hours;
	private Integer musutes;
	private String daysofweek;
	private Integer alarmtime;
	private Integer enabled;// 闹钟是否开启
	private String alert; // 闹钟路径
	private Integer times; // 时间间隔
	private Integer isdefault;
	private Integer interval;

	public AlarmInfo() {
	};

	public AlarmInfo(Integer id, Integer hours, Integer musutes,
			String daysofweek, Integer alarmtime, Integer enabled,
			String alert, Integer times, Integer isdefault, Integer interval) {
		super();
		this.id = id;
		this.hours = hours;
		this.musutes = musutes;
		this.daysofweek = daysofweek;
		this.alarmtime = alarmtime;
		this.enabled = enabled;
		this.alert = alert;
		this.times = times;
		this.isdefault = isdefault;
		this.interval = interval;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getHours() {
		return hours;
	}

	public void setHours(Integer hours) {
		this.hours = hours;
	}

	public Integer getMusutes() {
		return musutes;
	}

	public void setMusutes(Integer musutes) {
		this.musutes = musutes;
	}

	public String getDaysofweek() {
		return daysofweek;
	}

	public void setDaysofweek(String daysofweek) {
		this.daysofweek = daysofweek;
	}

	public Integer getAlarmtime() {
		return alarmtime;
	}

	public void setAlarmtime(Integer alarmtime) {
		this.alarmtime = alarmtime;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	public String getAlert() {
		return alert;
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

	public Integer getTimes() {
		return times;
	}

	public void setTimes(Integer times) {
		this.times = times;
	}

	public Integer getIsdefault() {
		return isdefault;
	}

	public void setIsdefault(Integer isdefault) {
		this.isdefault = isdefault;
	}

	public Integer getInterval() {
		return interval;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	@Override
	public String toString() {
		return "AlarmInfo [id=" + id + ", hours=" + hours + ", musutes="
				+ musutes + ", daysofweek=" + daysofweek + ", alarmtime="
				+ alarmtime + ", enabled=" + enabled + ", alert=" + alert
				+ ", times=" + times + ", isdefault=" + isdefault
				+ ", interval=" + interval + "]";
	}

}