package com.pavkoo.franklin.controls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.pavkoo.franklin.R;
import com.pavkoo.franklin.common.CheckState;
import com.pavkoo.franklin.common.Comment;
import com.pavkoo.franklin.common.CommonConst;
import com.pavkoo.franklin.common.FranklinApplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class TodayDialog extends ParentDialog {

	private RelativeLayout llappTodayPopupComment;
	private LinearLayout llprogressLine;
	private RadioGroup rgpToday;
	private RadioButton gpbTodayYes;
	private RadioButton gpbTodayNo;
	private AutoCompleteTextView txtComment;
	private TextView tvTodayPopupYes;
	private TextView title;

	private FranklinApplication app;

	public void setDialogTitle(String dialogTitle) {
		title.setText(dialogTitle);
	}
	
	public void updateUIByMoral(int index){
		int mainColor =Color.parseColor(CommonConst.colors[index % CommonConst.colors.length]);
		GradientDrawable gd = (GradientDrawable) title.getBackground();
		gd.setColor(mainColor);
	}

	private boolean resultChanged = false;
	private boolean newComment = false;
	private int newCommentIndex = -1;

	public int getNewCommentIndex() {
		return newCommentIndex;
	}

	public boolean isNewComment() {
		return newComment;
	}

	private ObjectAnimator closePopAnim;
	private CheckState checkState;

	public CheckState getCheckState() {
		return checkState;
	}

	public void setCheckState(CheckState checkState) {
		this.checkState = checkState;
	}

	private Object extraObject;

	public Object getExtraObject() {
		return extraObject;
	}

	public void setExtraObject(Object extraObject) {
		this.extraObject = extraObject;
	}

	public TodayDialog(Context context, int theme) {
		super(context, theme);
		resultChanged = false;
		newComment = false;
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
		View dialogView = li.inflate(R.layout.today_control_popup, null);
		setContentView(dialogView, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		rgpToday = (RadioGroup) findViewById(R.id.rgpToday);
		gpbTodayYes = (RadioButton) findViewById(R.id.gpbTodayYes);
		gpbTodayNo = (RadioButton) findViewById(R.id.gpbTodayNo);
		llappTodayPopupComment = (RelativeLayout) findViewById(R.id.llappTodayPopupComment);
		llprogressLine = (LinearLayout) findViewById(R.id.llprogressLine);
		txtComment = (AutoCompleteTextView) findViewById(R.id.txtComment);
		txtComment.setThreshold(1);
		tvTodayPopupYes = (TextView) findViewById(R.id.tvTodayPopupYes);
		title = (TextView) findViewById(R.id.tvTodayTitle);
		app = (FranklinApplication) this.getContext().getApplicationContext();

		closePopAnim = ObjectAnimator.ofFloat(llprogressLine, "scaleX", 1, 0);
		closePopAnim.setDuration(6000);
		closePopAnim.setInterpolator(new LinearInterpolator());
		tvTodayPopupYes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				addComment();
			}
		});

		gpbTodayYes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				resultChanged = true;
				checkState = CheckState.DONE;
				TodayDialog.this.dismiss();
			}
		});

		gpbTodayNo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				resultChanged = true;
				iniAutoComplemet();
				checkState = CheckState.UNDONE;
				gpbTodayYes.setVisibility(View.GONE);
				rgpToday.requestLayout();
				llappTodayPopupComment.setVisibility(View.VISIBLE);
				tvTodayPopupYes.setVisibility(View.VISIBLE);
				closePopAnim.start();
			}
		});

		txtComment.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				closePopAnim.cancel();
				return false;
			}
		});
		txtComment.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				if (actionId == EditorInfo.IME_ACTION_DONE) {
					addComment();
				}
				return false;
			}
		});

		closePopAnim.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				llprogressLine.invalidate();
			}
		});
		
		closePopAnim.addListener(new AnimatorListener() {
			private boolean isCancel;

			@Override
			public void onAnimationStart(Animator animation) {
				isCancel = false;
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (isCancel)
					return;
				TodayDialog.this.dismiss();
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				isCancel = true;
				closePopAnim.setCurrentPlayTime(0);
			}
		});

		rgpToday.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				closePopAnim.cancel();
			}
		});
	}

	public void showState(DialogState state) {
		resultChanged = false;
		newComment = false;
		if (state == DialogState.DSNote) {
			resultChanged = true;
			iniAutoComplemet();
			checkState = CheckState.UNDONE;
			gpbTodayYes.setVisibility(View.GONE);
			llappTodayPopupComment.setVisibility(View.VISIBLE);
			closePopAnim.setCurrentPlayTime(0);
			tvTodayPopupYes.setVisibility(View.VISIBLE);
			closePopAnim.start();
			this.show();
		} else {
			gpbTodayYes.setVisibility(View.VISIBLE);
			llappTodayPopupComment.setVisibility(View.GONE);
			closePopAnim.cancel();
			closePopAnim.setCurrentPlayTime(0);
			tvTodayPopupYes.setVisibility(View.GONE);
			this.show();
		}
	}

	public boolean isResultChanged() {
		return resultChanged;
	}

	public static enum DialogState {
		DSSelection, DSNote
	};

	private void iniAutoComplemet() {
		List<String> commstrs = new ArrayList<String>();
		for (int i = 0; i < app.getComments().size(); i++) {
			commstrs.add(app.getComments().get(i).getContent());
		}
		MyAdapter<String> adapter = new MyAdapter<String>(this.getContext(), android.R.layout.simple_dropdown_item_1line, commstrs);
		txtComment.setAdapter(adapter);
		txtComment.setText("");
	}

	private void addComment() {
		resultChanged = true;
		newComment = false;
		checkState = CheckState.UNDONE;
		String comment = txtComment.getText().toString();
		if (comment.equals("") || comment == null) {
			TodayDialog.this.dismiss();
			return;
		}
		Comment comObj = new Comment();
		comObj.setContent(comment);
		boolean find = false;
		List<Comment> comments = app.getComments();
		for (int i = 0; i < comments.size(); i++) {
			if (comments.get(i).equals(comObj) && !comments.get(i).isRemoved()) {
				comments.get(i).setCount(comments.get(i).getCount() + 1);
				newCommentIndex = i;
				find = true;
				break;
			}
		}
		if (!find) {
			comments.add(comObj);
			newCommentIndex = comments.size() - 1;
		}
		app.saveComments(comments);
		newComment = true;
		TodayDialog.this.dismiss();
		return;
	}

	@SuppressLint("DefaultLocale")
	private class MyAdapter<T> extends ArrayAdapter<T> implements Filterable {
		private MyFilter mFilter;
		private List<T> mOriginalValues;
		private List<T> mObjects;

		public MyAdapter(Context context, int textViewResourceId, List<T> objects) {
			super(context, textViewResourceId, objects);
			mObjects = objects;
			mFilter = new MyFilter();
		}

		@Override
		public Filter getFilter() {
			return mFilter;
		}

		@SuppressLint("DefaultLocale")
		private class MyFilter extends Filter {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				if (mOriginalValues == null) {
					mOriginalValues = new ArrayList<T>(mObjects);
				}
				int count = mOriginalValues.size();
				ArrayList<T> values = new ArrayList<T>();
				String content = "";
				if (constraint != null) {
					content = constraint.toString();
				}
				for (int i = 0; i < count; i++) {
					T value = mOriginalValues.get(i);
					String valueText = value.toString();
					if (!valueText.equals("") && !content.equals("") && valueText.indexOf(content) >= 0) {
						values.add(value);
					}
				}
				results.values = values;
				results.count = values.size();
				return results;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (results != null && results.count > 0) {
					// 有过滤结果，显示自动完成列表
					MyAdapter.this.clear(); // 清空旧列表
					MyAdapter.this.addAll((Collection<? extends T>) results.values);
					notifyDataSetChanged();
				} else {
					// 无过滤结果，关闭列表
					notifyDataSetInvalidated();
				}
			}
		}
	}
}
