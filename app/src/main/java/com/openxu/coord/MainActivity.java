package com.openxu.coord;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.openxu.coord.behavior.HeaderPagerBehavior;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements HeaderPagerBehavior.OnPagerStateListener {



    private HeaderPagerBehavior mPagerBehavior;
    private FixedLinearLayout sliding_content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPagerBehavior = (HeaderPagerBehavior) ((CoordinatorLayout.LayoutParams) findViewById(R.id.id_header_pager).getLayoutParams()).getBehavior();
        mPagerBehavior.setPagerStateListener(this);
        sliding_content = (FixedLinearLayout) findViewById(R.id.sliding_content);
        setViewPagerScrollEnable(mNewsPager,false);

    }

    @Override
    public void onPagerClosed() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onPagerClosed: ");
        }
        Snackbar.make(mNewsPager, "pager closed", Snackbar.LENGTH_SHORT).show();
        setFragmentRefreshEnabled(true);
        setViewPagerScrollEnable(mNewsPager,true);
    }



    @Override
    public void onPagerOpened() {
        Snackbar.make(mNewsPager, "pager opened", Snackbar.LENGTH_SHORT).show();
        setFragmentRefreshEnabled(false);
        setViewPagerScrollEnable(mNewsPager,false);
    }

    public void setViewPagerScrollEnable(ViewPager viewPager,boolean enable){
        if(false==(viewPager instanceof FixedLinearLayout)){
            return;
        }
        FixedViewPager fixViewPager= (FixedViewPager) viewPager;
        if(enable){
            fixViewPager.setScrollable(true);
        }else{
            fixViewPager.setScrollable(false);
        }
    }

    private void setFragmentRefreshEnabled(boolean enabled) {
        for(Fragment fragment:mFragments){
            ((TestFragment)fragment).setRefreshEnable(enabled);
        }
    }

}
