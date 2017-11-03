package com.guojia.robot.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.guojia.robot.R;
import com.guojia.robot.utils.ToastUtil;

import java.util.List;

/**
 * Created by win7 on 2017/10/31.
 */

public class MainViewPagerAdapter extends PagerAdapter{
    private Context context;
    private int[] picIds;
    private int[] btnIds;
    private int[] TextIds;
    private Button btn;

   public MainViewPagerAdapter(Context context){
       this.context = context;
       picIds = new int[]{R.mipmap.vp_item_flag_pk,R.mipmap.vp_item_flag_hot,R.mipmap.vp_item_flag_findmyself,R.mipmap.vp_item_flag_msg};
       btnIds = new int[]{R.string.now_pk,R.string.right_now,R.string.find_myself,R.string.btn_msg};
       TextIds = new int[]{R.mipmap.vp_item_pk_text,R.mipmap.vp_item_hot_text,R.mipmap.vp_item_meself_text,R.mipmap.vp_item_msg_text};
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

//        for (int i = 0; i < Integer.MAX_VALUE; i++) {
        position %=picIds.length;

        View view  = LayoutInflater.from(container.getContext()).inflate(R.layout.main_viewpager_item,null);
        view.findViewById(R.id.vp_item_flag_pic).setBackgroundResource(picIds[position]);
        view.findViewById(R.id.vp_item_text).setBackgroundResource(TextIds[position]);
        btn = view.findViewById(R.id.vp_item_btn);
        btn.setText(btnIds[position]);
        final int finalPosition = position;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (finalPosition) {
                    case 0:
                        ToastUtil.show(context,"立即挑战", Toast.LENGTH_SHORT);
                        break;
                    case 1:
                        ToastUtil.show(context,"马上体验", Toast.LENGTH_SHORT);
                        break;
                    case 2:
                        ToastUtil.show(context,"找回自我", Toast.LENGTH_SHORT);
                        break;
                    case 3:
                        ToastUtil.show(context,"最我先知", Toast.LENGTH_SHORT);
                        break;
                }
            }
        });
        container.addView(view,0);
//        }
        return view;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public float getPageWidth(int position) {
        return (float) 0.3;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object){
        return object == view;
    }
}
