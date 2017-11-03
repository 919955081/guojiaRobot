package com.guojia.robot.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.guojia.robot.R;
import com.guojia.robot.utils.DialogUtil;
import com.guojia.robot.utils.LogUtil;
import com.guojia.robot.utils.MethodsUtil;
import com.guojia.robot.utils.WifiUtil;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BaseActivity {
    private static LogUtil log = new LogUtil("GuideActivity", LogUtil.LogLevel.V);

    private ArrayList<View> mViewList;
    private ViewPager vp_guideimg;
    private Context mContext = GuideActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_guide);

        vp_guideimg = (ViewPager) findViewById(R.id.vp_guideimg);

        mViewList = new ArrayList<View>();
        LayoutInflater lf = getLayoutInflater().from(GuideActivity.this);
        View view1 = lf.inflate(R.layout.activity_guide_vp1, null);
        View view2 = lf.inflate(R.layout.activity_guide_vp2, null);
        View view3 = lf.inflate(R.layout.activity_guide_vp3, null);
        mViewList.add(view1);
        mViewList.add(view2);
        mViewList.add(view3);

        vp_guideimg.setAdapter(new ViewPagerAdatper(mViewList));

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public class ViewPagerAdatper extends PagerAdapter {
        private ArrayList<View> mViewList ;

        public ViewPagerAdatper(ArrayList<View> mViewList ) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            if(2 == position){
                mViewList.get(position).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(GuideActivity.this, WelcomeActivity.class));
                        finish();
                    }
                });
            }

            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }
    }

}
