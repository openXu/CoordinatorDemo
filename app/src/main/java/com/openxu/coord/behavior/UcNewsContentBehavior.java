package com.openxu.coord.behavior;

import android.content.Context;
import android.support.design.BuildConfig;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.openxu.coord.DemoApplication;
import com.openxu.coord.R;

import java.util.List;

/** 
 * autour: openXu
 * date: 2017/5/24 16:51 
 * className: UcNewsContentBehavior
 * version:
 * description: 可滚动的内容列表Behavior
 *
 *          由于列表内容需要随着两个header的移动而移动，所以，列表需要依赖两个haeder
 */
public class UcNewsContentBehavior extends HeaderScrollingViewBehavior {

    public UcNewsContentBehavior() {
    }

    public UcNewsContentBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //child 是指应用behavior的View ，dependency 担任触发behavior的角色，并与child进行互动。
    //确定你是否依赖于这个View
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return isDependOn(dependency);
    }

    //当所依赖的View变动时会回调这个方法
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDependentViewChanged");
        }
        offsetChildAsNeeded(parent, child, dependency);
        return false;
    }

    private void offsetChildAsNeeded(CoordinatorLayout parent, View child, View dependency) {
        child.setTranslationY((int) (-dependency.getTranslationY() / (getHeaderOffsetRange() * 1.0f) * getScrollRange(dependency)));

    }


    protected View findFirstDependency(List<View> views) {
        for (int i = 0, z = views.size(); i < z; i++) {
            View view = views.get(i);
            if (isDependOn(view))
                return view;
        }
        return null;
    }

    @Override
    protected int getScrollRange(View v) {
        if (isDependOn(v)) {
            return Math.max(0, v.getMeasuredHeight() - getFinalHeight());
        } else {
            return super.getScrollRange(v);
        }
    }

    private int getHeaderOffsetRange() {
        return DemoApplication.getAppContext().getResources().getDimensionPixelOffset(R.dimen.uc_news_header_pager_offset);
    }

    private int getFinalHeight() {
        return DemoApplication.getAppContext().getResources().
                getDimensionPixelOffset(R.dimen.uc_news_tabs_height)
                + DemoApplication.getAppContext().getResources().
                getDimensionPixelOffset(R.dimen.uc_news_header_title_height);
    }


    private boolean isDependOn(View dependency) {
        return dependency != null && dependency.getId() == R.id.id_header_pager;
    }
}
