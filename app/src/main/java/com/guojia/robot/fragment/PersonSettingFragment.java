package com.guojia.robot.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guojia.robot.R;
import com.guojia.robot.activity.FaceSetActivity;
import com.guojia.robot.activity.VoiceSetActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonSettingFragment extends BaseFragment implements View.OnClickListener {
    private TextView mFaceRegist,mVoiceRegist;


    public PersonSettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_person_setting, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mFaceRegist = view.findViewById(R.id.person_set_face_ver);
        mVoiceRegist = view.findViewById(R.id.person_set_voice_ver);

        mFaceRegist.setOnClickListener(this);
        mVoiceRegist.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_set_face_ver:
                startActivity(new Intent(mActivity, FaceSetActivity.class));
                break;
            case R.id.person_set_voice_ver:
                startActivity(new Intent(mActivity, VoiceSetActivity.class));
                break;
        }
    }
}
