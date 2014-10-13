package com.pavkoo.franklin.controls;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ScrollingTextView extends TextView {
	
	public ScrollingTextView(Context context) {
		super(context);
	}

	public ScrollingTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ScrollingTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    LayoutParams params = (LayoutParams) getLayoutParams();
        params.width = w;
        params.height = h;
        params.weight = 0 ;
        setLayoutParams(params);
    }
}
