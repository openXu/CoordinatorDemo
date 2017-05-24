package com.openxu.coord.behavior;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.BuildConfig;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.openxu.coord.DemoApplication;
import com.openxu.coord.R;
import com.openxu.coord.helper.MathUtils;
import com.openxu.coord.helper.ViewOffsetBehavior;

import java.util.List;

/** 
 * autour: openXu
 * date: 2017/5/24 16:51 
 * className: UcNewsContentBehavior
 * version:
 * description: 可滚动的内容列表Behavior
 *
 *          由于列表内容需要随着header的移动而移动，所以，列表需要依赖haeder
 */
public class ContentBehavior extends ViewOffsetBehavior<View> {

    public ContentBehavior() {
    }

    public ContentBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**********/
//    正常来说被依赖的View会优先于依赖它的View处理，所以需要依赖的View可以在measure/layout的时候，
//    找到依赖的View并获取到它的测量/布局的信息，这里的处理就是依靠着这种关系来实现的
    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, View child, int parentWidthMeasureSpec,
                                  int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        final int childLpHeight = child.getLayoutParams().height;
        if (childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT ||
                childLpHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
            final List<View> dependencies = parent.getDependencies(child);
            if(dependencies==null || dependencies.size()<=0)
                return false;
            final LinearLayout headerLayout = (LinearLayout)dependencies.get(0);
            if (ViewCompat.getFitsSystemWindows(child)) {
                // If the set succeeded, trigger a new layout and return true
                child.requestLayout();
                return true;
            }

            if (ViewCompat.isLaidOut(headerLayout)) {
                int availableHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec);
                if (availableHeight == 0) {
                    // If the measure spec doesn't specify a size, use the current height
                    availableHeight = parent.getHeight();
                }
                FrameLayout id_header_1 = (FrameLayout)headerLayout.getChildAt(0);
                FrameLayout id_header_2 = (FrameLayout)headerLayout.getChildAt(1);
                //内容布局的高度=屏幕高度-头部的高度+第一个头部隐藏之后漏出的高度（id_header_1的高度）
                final int height = availableHeight - id_header_2.getMeasuredHeight();
//                final int height = availableHeight - headerLayout.getMeasuredHeight() +
//                        id_header_1.getMeasuredHeight();
                Log.i(TAG, "id_header_1高度："+id_header_1.getMeasuredHeight());
                Log.i(TAG, "id_header_2高度："+id_header_2.getMeasuredHeight());
                Log.i(TAG, "内容高度："+height);
                final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height,
                        childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT ?
                                View.MeasureSpec.EXACTLY : View.MeasureSpec.AT_MOST);

                // Now measure the scrolling view with the correct height
                parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);

                return true;
            }
        }

        /*if (childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT ||
                childLpHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
            // If the menu's height is set to match_parent/wrap_content then measure it
            // with the maximum visible height
            //获取child依赖的控件（header）
            final List<View> dependencies = parent.getDependencies(child);
            final View header = findFirstDependency(dependencies);
            Log.i(TAG, "头部控件："+header);
            if (header != null) {
                if (ViewCompat.getFitsSystemWindows(header) && !ViewCompat.getFitsSystemWindows(child)) {
                    // If the header is fitting system windows then we need to also,
                    // otherwise we'll get CoL's compatible measuring
                    ViewCompat.setFitsSystemWindows(child, true);
                    if (ViewCompat.getFitsSystemWindows(child)) {
                        // If the set succeeded, trigger a new layout and return true
                        child.requestLayout();
                        return true;
                    }
                }

                if (ViewCompat.isLaidOut(header)) {
                    int availableHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec);
                    if (availableHeight == 0) {
                        // If the measure spec doesn't specify a size, use the current height
                        availableHeight = parent.getHeight();
                    }
                    //内容布局的高度=屏幕高度-头部的高度+
                    final int height = availableHeight - header.getMeasuredHeight() + getScrollRange(header);
                    final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height,
                            childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT ? View.MeasureSpec.EXACTLY : View.MeasureSpec.AT_MOST);

                    // Now measure the scrolling view with the correct height
                    parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);

                    return true;
                }
            }
        }*/
        return false;
    }


    private final Rect mTempRect1 = new Rect();
    private final Rect mTempRect2 = new Rect();
    private int mVerticalLayoutGap = 0;
    private int mOverlayTop;
    @Override
    protected void layoutChild(final CoordinatorLayout parent, final View child, final int layoutDirection) {
//        final List<View> dependencies = parent.getDependencies(child);
//        final View headerLayout = findFirstDependency(dependencies);
        final List<View> dependencies = parent.getDependencies(child);
        if(dependencies==null || dependencies.size()<=0)
            return;
        final LinearLayout headerLayout = (LinearLayout)dependencies.get(0);
        if (headerLayout != null) {
            final CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            final Rect available = mTempRect1;
            //内容布局的左、上、右、下
            available.set(parent.getPaddingLeft() + lp.leftMargin,
                    headerLayout.getBottom() + lp.topMargin,
                    parent.getWidth() - parent.getPaddingRight() - lp.rightMargin,
                    parent.getHeight() + headerLayout.getBottom() - parent.getPaddingBottom() - lp.bottomMargin);

            final Rect out = mTempRect2;
            GravityCompat.apply(resolveGravity(lp.gravity), child.getMeasuredWidth(),
                    child.getMeasuredHeight(), available, out, layoutDirection);

            final int overlap = getOverlapPixelsForOffset(headerLayout);

            child.layout(out.left, out.top - overlap, out.right, out.bottom - overlap);
            mVerticalLayoutGap = out.top - headerLayout.getBottom();
        } else {
            // If we don't have a dependency, let super handle it
            super.layoutChild(parent, child, layoutDirection);
            mVerticalLayoutGap = 0;
        }
    }
    private static int resolveGravity(int gravity) {
        return gravity == Gravity.NO_GRAVITY ? GravityCompat.START | Gravity.TOP : gravity;
    }
    final int getOverlapPixelsForOffset(final View header) {
        return mOverlayTop == 0 ? 0
                : MathUtils.constrain(Math.round(getOverlapRatioForOffset(header) * mOverlayTop),
                0, mOverlayTop);

    }
    float getOverlapRatioForOffset(final View header) {
        return 1f;
    }
    /**********/

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

    private View findFirstDependency(List<View> views) {
        for (int i = 0, z = views.size(); i < z; i++) {
            View view = views.get(i);
            if (isDependOn(view))
                return view;
        }
        return null;
    }

    private int getScrollRange(View v) {
        if (isDependOn(v)) {
            return Math.max(0, v.getMeasuredHeight() - getFinalHeight());
        } else {
            return v.getMeasuredHeight();
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
