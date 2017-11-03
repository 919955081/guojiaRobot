package com.guojia.robot.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.guojia.robot.R;
import com.guojia.robot.adapter.MainViewPagerAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private ViewPager mViewPager;
    private MainViewPagerAdapter mVpAdater;

    //ui
    private RelativeLayout mUserTitleLayout,mSearchLayout;  /**用户栏  搜索栏  */
    private ImageView mQRCSearchImg,mVoiceSearchImg,mInditor_one,mInditor_two;        /**扫描搜索  语音搜索   指示器*/
    private LinearLayout mSujectLayout;         /** 学科*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initData() {
        mVpAdater = new MainViewPagerAdapter(this);
    }



    private void initView() {
        mViewPager = findViewById(R.id.main_item_vp);
        ViewPagerScroller scroller = new ViewPagerScroller(MainActivity.this);


        mUserTitleLayout = findViewById(R.id.main_user_title_layout);
        mSearchLayout = findViewById(R.id.main_search_layout);
        mQRCSearchImg = findViewById(R.id.main_search_qr_iv);
        mVoiceSearchImg = findViewById(R.id.main_search_voice);
        mSujectLayout = findViewById(R.id.main_subject_layout);
        mInditor_one = findViewById(R.id.main_indicator_iv1);
        mInditor_two = findViewById(R.id.main_indicator_iv2);

        mInditor_one.setTag(1);
        mUserTitleLayout.setOnClickListener(this);
        mSearchLayout.setOnClickListener(this);
        mQRCSearchImg.setOnClickListener(this);
        mVoiceSearchImg.setOnClickListener(this);
        mSujectLayout.setOnClickListener(this);

        mViewPager.setAdapter(mVpAdater);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.e(TAG, "onPageScrolled: "+position+positionOffset+positionOffsetPixels );
                if ((int)mInditor_one.getTag()==1){
                    mInditor_one.setBackgroundResource(R.mipmap.main_indicator_normal);
                    mInditor_two.setBackgroundResource(R.mipmap.main_indicator_choice);
                    mInditor_one.setTag(2);
                }else {
                    mInditor_one.setBackgroundResource(R.mipmap.main_indicator_choice);
                    mInditor_two.setBackgroundResource(R.mipmap.main_indicator_normal);
                    mInditor_one.setTag(1);
                }

            }


            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        scroller.setScrollDuration(500);
        scroller.initViewPagerScroll(mViewPager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_user_title_layout:
                Intent intent = new Intent(this,PersonalActivity.class);
                startActivity(intent);
                break;
            case R.id.main_search_layout:
                startActivity(new Intent(this,SearchActivity.class));
                break;
            case R.id.main_search_qr_iv:
                startActivity(new Intent(this,QRSearchActivity.class));
                break;
            case R.id.main_search_voice:
                startActivity(new Intent(this,VoiceSearchActivity.class));
                break;
            case R.id.main_subject_layout:
                break;
        }
    }

    class ViewPagerScroller extends Scroller {
        private int mScrollDuration = 2000;             // 滑动速度

        /**
         * 设置速度速度
         * @param duration
         */
        public void setScrollDuration(int duration){
            this.mScrollDuration = duration;
        }

        public ViewPagerScroller(Context context) {
            super(context);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }



        public void initViewPagerScroll(ViewPager viewPager) {
            try {
                Field mScroller = ViewPager.class.getDeclaredField("mScroller");
                mScroller.setAccessible(true);
                mScroller.set(viewPager, this);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
