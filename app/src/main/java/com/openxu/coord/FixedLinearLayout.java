package com.openxu.coord;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class FixedLinearLayout extends LinearLayout {
    private boolean mIsScrollable=true;

    public FixedLinearLayout(Context context) {
        super(context);
    }

    public FixedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollable(boolean isScrollable) {
        this.mIsScrollable = isScrollable;
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent arg0) { if (mIsScrollable) return super.onInterceptTouchEvent(arg0); else return false; }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIsScrollable) {
            return super.onTouchEvent(ev);
        } else {
            return false;
        }

    }
}
