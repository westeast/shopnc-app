package com.daxueoo.shopnc.ui.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daxueoo.shopnc.R;

/**
 * 设置的fragment
 * Created by user on 15-8-2.
 */
public class SettingFragment extends PreferenceFragment {

    private View view = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.fragment_setting, null);
        return view;

    }
}
