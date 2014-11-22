package com.pavkoo.franklin.common;

import java.io.Serializable;

public class Comment implements Comparable<Comment>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String content;
	private int count;
	private boolean removed;
	private long timestamp;

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public Comment() {
		this.count = 1;
		timestamp = System.currentTimeMillis();
		removed = false;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int compareTo(Comment another) {
		if (another == null)
			return 1;
		else {
			if (this.count == another.count) {
				if (this.getContent().length() > another.getContent().length()) {
					return 1;
				} else if (this.getContent().length() < another.getContent()
						.length()) {
					return -1;
				} else {
					return 0;
				}
			} else if (this.count > another.count) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o.getClass() == this.getClass()) {
			Comment target = (Comment) o;
			if (target.timestamp == this.timestamp) {
				return target.content.equals(this.content);
			}
		}
		return false;
	}

}
