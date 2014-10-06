package com.castro.runtracker;

import java.util.Date;

public class Run {

	private Date mStartDate;
	private long mId;
	
	public Run() {
		mId = -1;
		mStartDate = new Date();
	}
	
	public Date getStartDate() {
		return mStartDate;		
	}
	
	public void setStartDate(Date startDate) {
		mStartDate = startDate;
	}
	
	public int getDurationSeconds (Long endMillis) {
		return (int) ((endMillis -mStartDate.getTime()) / 1000);	
	}
	
	public static String formatDuration (int durationSeconds) {
		int seconds = durationSeconds % 60;
		int minutes = ((durationSeconds - seconds) / 60) % 60;
		int hours = (durationSeconds - (minutes * 60) - seconds) /60;
		
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return mId;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		mId = id;
	}
	
}
