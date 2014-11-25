package com.pavkoo.franklin.common;

import java.util.Date;

public class SignRecords {
	private int moarlIndex;
	private int commentIndex;
	private CheckState cs;
	private Date inputDate;
	
	public SignRecords(){
		moarlIndex = -1;
		commentIndex = -1;
		cs = CheckState.UNKNOW;
		inputDate = new Date();
	}
	
	
	public int getMoarlIndex() {
		return moarlIndex;
	}

	public void setMoarlIndex(int moarlIndex) {
		this.moarlIndex = moarlIndex;
	}

	public int getCommentIndex() {
		return commentIndex;
	}

	public void setCommentIndex(int commentIndex) {
		this.commentIndex = commentIndex;
	}

	public CheckState getCs() {
		return cs;
	}

	public void setCs(CheckState cs) {
		this.cs = cs;
	}

	public Date getInputDate() {
		return inputDate;
	}

	public void setInputDate(Date inputDate) {
		this.inputDate = inputDate;
	}
}
