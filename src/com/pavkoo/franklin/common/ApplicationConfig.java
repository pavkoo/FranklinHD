package com.pavkoo.franklin.common;

import java.util.Date;

public class ApplicationConfig {
	public ApplicationConfig() {
	}

	// the first time use app
	private boolean isFrist;

	private boolean isselfConfiged;
	private boolean isDefaultSaved; // �Ƿ��Ѿ�������Ĭ������
	private boolean isProjectStarted;
	private Date firstUse; // ʵ�ʼƻ�������ʼ��ʱ��
	private Date lastUse;
	private int historyCount; // ��ʷ��¼�������
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getHistoryCount() {
		return historyCount;
	}

	public void setHistoryCount(int historyCount) {
		this.historyCount = historyCount;
	}

	public boolean isFrist() {
		return isFrist;
	}

	public void setFrist(boolean isFrist) {
		this.isFrist = isFrist;
	}

	public boolean isIsselfConfiged() {
		return isselfConfiged;
	}

	public void setIsselfConfiged(boolean isselfConfiged) {
		this.isselfConfiged = isselfConfiged;
	}

	public boolean isDefaultSaved() {
		return isDefaultSaved;
	}

	public void setDefaultSaved(boolean isUseDefaultConfig) {
		this.isDefaultSaved = isUseDefaultConfig;
	}

	public boolean isProjectStarted() {
		return isProjectStarted;
	}

	public void setProjectStarted(boolean isProjectStarted) {
		this.isProjectStarted = isProjectStarted;
	}

	public Date getFirstUse() {
		return firstUse;
	}

	public void setFirstUse(Date firstUse) {
		this.firstUse = firstUse;
	}

	public Date getLastUse() {
		return lastUse;
	}

	public void setLastUse(Date lastUse) {
		this.lastUse = lastUse;
	}

	public int getUseAppCount() {
		// 1 means today
		return (int) UtilsClass.dayCount(this.getFirstUse(), new Date()) + 1;
	}
}
