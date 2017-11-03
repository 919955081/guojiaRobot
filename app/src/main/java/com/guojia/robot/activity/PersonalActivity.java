package com.guojia.robot.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guojia.robot.R;
import com.guojia.robot.fragment.PersonBaseFragment;
import com.guojia.robot.fragment.PersonSettingFragment;

public class PersonalActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout mPersonBaseLayout,mPersonSetLayout;
    private FragmentManager fm;
    private Fragment mPersonSetFragment,mPersonBaseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_layout);
        initData();
        if (savedInstanceState == null){
            mPersonSetFragment = new PersonSettingFragment();
            fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.person_right_layout,mPersonSetFragment).commit();
        }
        initView();
    }

    /**
     * 隐藏fragment
     * @param fragment
     * @param ft
     */
    private void hideFragment(Fragment fragment, FragmentTransaction ft) {
        if (fragment != null) {
            ft.hide(fragment);
        }
    }
    private void initData() {
    }

    private void initView() {
        mTop_tv = (TextView) findViewById(R.id.mode_bar_tv);
        mTop_tv.setText(getString(R.string.person_bar_name));

        mPersonBaseLayout = findViewById(R.id.base_data_layout);
        mPersonSetLayout = findViewById(R.id.setting_data_layout);
        mBackImg = findViewById(R.id.back);

        mBackImg.setOnClickListener(this);
        mPersonBaseLayout.setOnClickListener(this);
        mPersonSetLayout.setOnClickListener(this);
        findViewById(R.id.person_login_out_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = fm.beginTransaction();
        switch (v.getId()) {
            case R.id.base_data_layout:
                hideFragment(mPersonSetFragment,transaction);
                if (mPersonBaseFragment == null){
                    mPersonBaseFragment = new PersonBaseFragment();
                    transaction.add(R.id.person_right_layout, mPersonBaseFragment);
                }else {
                    transaction.show(mPersonBaseFragment);
                }
                mPersonBaseLayout.setBackgroundResource(R.color.person_item_onlick_color);
                mPersonSetLayout.setBackgroundResource(R.color.person_main_color);
                break;
            case R.id.setting_data_layout:
                hideFragment(mPersonBaseFragment,transaction);
                if (mPersonSetFragment == null){
                    mPersonSetFragment = new PersonBaseFragment();
                    transaction.add(R.id.person_right_layout, mPersonSetFragment);
                }else {
                    transaction.show(mPersonSetFragment);
                }
                mPersonBaseLayout.setBackgroundResource(R.color.person_main_color);
                mPersonSetLayout.setBackgroundResource(R.color.person_item_onlick_color);
                break;
            case R.id.person_login_out_btn:
                finish();
                break;
            case R.id.back:
                finish();
                break;
        }
        transaction.commit();
    }
}
