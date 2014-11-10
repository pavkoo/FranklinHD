package com.pavkoo.franklin.controls;

import java.util.ArrayList;
import java.util.List;

import com.pavkoo.franklin.R;
import com.pavkoo.franklin.common.Comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter {
	private LinearLayout llCommentItemBg;
	private TextView txtCommentItemNumber;
	private TextView txtCommentItemText;
	private int max = 0;
	private int mainColor;

	private List<Comment> comments;

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comms) {
		if (this.comments == null) {
			this.comments = new ArrayList<Comment>();
		}
		comments.clear();
		if (null != comms) {
			for (int i = 0; i < comms.size(); i++) {
				if (!comms.get(i).isRemoved()) {
					this.comments.add(comms.get(i));
				}
			}
		}
		updateMax();
	}

	private Context context;

	public CommentAdapter(Context context, List<Comment> comms, int mainColor) {
		this.context = context;
		this.setComments(comms);
		this.mainColor = mainColor;
	}

	private void updateMax() {
		for (int i = 0; i < comments.size(); i++) {
			if (max < comments.get(i).getCount()) {
				max = comments.get(i).getCount();
			}
		}
	}

	@Override
	public void notifyDataSetChanged() {
		updateMax();
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return comments.size();
	}

	@Override
	public Object getItem(int position) {
		return comments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.cycle_history_comments_item, null);
		}
		llCommentItemBg = (LinearLayout) convertView
				.findViewById(R.id.llCommentItemBg);
		llCommentItemBg.setBackgroundColor(mainColor);
		txtCommentItemNumber = (TextView) convertView
				.findViewById(R.id.txtCommentItemNumber);
		txtCommentItemText = (TextView) convertView
				.findViewById(R.id.txtCommentItemText);
		txtCommentItemNumber.setText(String.valueOf(comments.get(position)
				.getCount()));
		txtCommentItemText.setText(comments.get(position).getContent());
		float scale = comments.get(position).getCount() / (float) max;
		llCommentItemBg.setScaleX(scale);
		// who can write worse code than this
		if (comments.get(position).isRemoved()) {
			return null;
		}
		return convertView;
	}

}
