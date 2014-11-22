package com.pavkoo.franklin.controls;

import java.util.ArrayList;
import java.util.List;

import com.nineoldandroids.view.ViewHelper;
import com.pavkoo.franklin.R;
import com.pavkoo.franklin.common.Comment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter {
	private LinearLayout llCommentItemBg;
	private TextView txtCommentItemNumber;
	private TextView txtCommentItemText;
	private int max = 0;
	private int mainColor;
	private final int MAXSIZE = 15;
	private IRemoveComment OnRemoveComment;

	public IRemoveComment getOnRemoveComment() {
		return OnRemoveComment;
	}

	public void setOnRemoveComment(IRemoveComment onRemoveComment) {
		OnRemoveComment = onRemoveComment;
	}

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
			int addedCount = 0;
			for (int i = 0; i < comms.size() && addedCount < MAXSIZE; i++) {
				if (!comms.get(i).isRemoved()) {
					this.comments.add(comms.get(i));
					addedCount++;
				}
			}
		}
		this.notifyDataSetChanged();
	}

	private Context context;

	public CommentAdapter(Context context, List<Comment> comms, int mainColor) {
		this.context = context;
		this.setComments(comms);
		this.mainColor = mainColor;
	}

	public CommentAdapter(Context context, int mainColor) {
		this.context = context;
		this.mainColor = mainColor;
		if (this.comments == null) {
			this.comments = new ArrayList<Comment>();
		}
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

	@SuppressLint("HandlerLeak")
	final Handler myHandle = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HideIconMsg:
				((TextView) msg.obj).setVisibility(View.GONE);
				break;
			default:
				break;
			}
		}
	};
	private final int HideIconMsg = 0;

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.cycle_history_comments_item, null);
		}
		final TextView txtCommentItemRemove;
		llCommentItemBg = (LinearLayout) convertView
				.findViewById(R.id.llCommentItemBg);
		llCommentItemBg.setBackgroundColor(mainColor);
		txtCommentItemNumber = (TextView) convertView
				.findViewById(R.id.txtCommentItemNumber);
		txtCommentItemText = (TextView) convertView
				.findViewById(R.id.txtCommentItemText);
		txtCommentItemRemove = (TextView) convertView
				.findViewById(R.id.txtCommentItemRemove);
		txtCommentItemNumber.setText(String.valueOf(comments.get(position)
				.getCount()));
		txtCommentItemText.setText(comments.get(position).getContent());

		txtCommentItemText.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Animation anim = AnimationUtils.loadAnimation(context,
						android.R.anim.fade_in);
				txtCommentItemRemove.setAnimation(anim);
				txtCommentItemRemove.setVisibility(View.VISIBLE);
				anim.start();
				Message msg = new Message();
				msg.obj = txtCommentItemRemove;
				msg.what = HideIconMsg;
				myHandle.sendMessageDelayed(msg, 4000);
				return false;
			}
		});
		txtCommentItemRemove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CommentAdapter.this.OnRemoveComment != null) {
					CommentAdapter.this.OnRemoveComment
							.onRemoveComment(position);
				}
				txtCommentItemRemove.setVisibility(View.GONE);
			}
		});

		float scale = comments.get(position).getCount() / (float) max;
		ViewHelper.setScaleX(llCommentItemBg, scale);
		// who can write worse code than this
		if (comments.get(position).isRemoved()) {
			return null;
		}
		return convertView;
	}

}
