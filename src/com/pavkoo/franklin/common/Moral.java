package com.pavkoo.franklin.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Moral implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Moral() {
		stateList = new ArrayList<CheckState>();
		commentIndex = new ArrayList<Integer>();
		finished = false;
		currentDayInCycle = 0;
		version = 1;
	};

	private String title;
	private String titleDes;
	private String titleMotto;
	private boolean finished;
	private List<Integer> commentIndex;
	private List<CheckState> stateList; // 始终保持最新一周的记录
	private int cycle;
	private int currentDayInCycle;
	private int version;
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCurrentDayInCycle() {
		return currentDayInCycle;
	}

	private void clearTodayComment() {
		commentIndex.set(commentIndex.size() - 1, -1);
	}

	public void clearHistoryComment() {
		for (int i = 0; i < commentIndex.size(); i++) {
			commentIndex.set(i, -1);
		}
	}

	public void setCurrentDayInCycle(int currentDayInCycle) {
		this.currentDayInCycle = currentDayInCycle;
	}

	private Date startDate;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	private Date endDate;

	public boolean isFinished() {
		return finished;
	}

	public boolean isDoing() {
		return currentDayInCycle != 0;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	// -1 means have no comment
	public List<Integer> getComments() {
		return commentIndex;
	}

	public String getTitleMotto() {
		return titleMotto;
	}

	public void setTitleMotto(String titleMotto) {
		this.titleMotto = titleMotto;
	}

	public CheckState getTodaySelected() {
		return stateList.get(stateList.size() - 1);
	}

	public CheckState getHistorySelected(int pos) {
		int passCycle = stateList.size() / this.cycle;
		int reminder = stateList.size() % this.cycle;
		if (reminder == 0) {
			passCycle -= 1;
		}
		pos = passCycle * this.cycle + pos - 1;
		if ((pos < 0) || (pos > stateList.size() - 1))
			return CheckState.UNKNOW;
		return stateList.get(pos);
	}

	public void setHistorySelected(int pos, CheckState cs) {
		setHistorySelected(pos, cs, -1);
	}

	public void setHistorySelected(int pos, CheckState cs, int commIndex) {
		int passCycle = stateList.size() / this.cycle;
		int reminder = stateList.size() % this.cycle;
		if (reminder == 0) {
			passCycle -= 1;
		}
		pos = passCycle * this.cycle + pos - 1;
		if ((pos < 0) || (pos > stateList.size() - 1))
			return;
		stateList.set(pos, cs);
		if (cs == CheckState.UNDONE) {
			commentIndex.set(pos, commIndex);
		} else {
			commentIndex.set(pos, -1);
		}
	}

	public int getCommentIndex(int pos) {
		int passCycle = stateList.size() / this.cycle;
		int reminder = stateList.size() % this.cycle;
		if (reminder == 0) {
			passCycle -= 1;
		}
		pos = passCycle * this.cycle + pos - 1;
		if ((pos < 0) || (pos > stateList.size() - 1))
			return -1;
		return commentIndex.get(pos);
	}

	public void setTodaySelected(CheckState todaySelected) {
		@SuppressWarnings("unused")
		CheckState today = stateList.get(stateList.size() - 1);
		today = todaySelected;
		if (todaySelected == CheckState.UNDONE) {
			clearTodayComment();
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleDes() {
		return titleDes;
	}

	public void setTitleDes(String titleDes) {
		this.titleDes = titleDes;
	}

	public List<CheckState> getStateList() {
		return stateList;
	}

	public int getCycle() {
		return cycle;
	}

	public void setCycle(int cycle) {
		this.cycle = cycle;
	}

	public int getCheckedCount() {
		int total = 0;
		for (int i = 0; i < stateList.size(); i++) {
			if (stateList.get(i) != CheckState.UNKNOW)
				total++;
		}
		return total;
	}

	public int getTotalCheckCount() {
		return stateList.size();
	}

	public int getTotalCheckedSize() {
		int total = 0;
		for (int i = 0; i < stateList.size(); i++) {
			if (stateList.get(i) == CheckState.DONE)
				total++;
		}
		return total;
	}

	public int getCycleCount() {
		return (int) Math.ceil((float) stateList.size() / this.cycle);
	}

	public float getCheckRate(int cycleNo) {
		if (cycleNo < 0 || cycleNo > getCycleCount())
			return 0;
		int beginIndex = cycleNo * cycle;
		int endIndex = (cycleNo + 1) * cycle - 1;
		if (beginIndex >= stateList.size()) {
			beginIndex = stateList.size() - 1;
		}
		if (endIndex >= stateList.size()) {
			endIndex = stateList.size() - 1;
		}
		int total = 0;
		for (int i = beginIndex; i <= endIndex; i++) {
			if (stateList.get(i) == CheckState.DONE)
				total++;
		}
		return (float) total / cycle;
	}

	public void reSet() {
		stateList.clear();
		commentIndex.clear();
		finished = false;
		currentDayInCycle = 0;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}
