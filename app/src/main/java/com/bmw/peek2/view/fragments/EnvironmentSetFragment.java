package com.bmw.peek2.view.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.R;
import com.bmw.peek2.model.Environment;
import com.bmw.peek2.view.adapter.EnvironmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/4/7.
 */

public class EnvironmentSetFragment extends Fragment {
    @Bind(R.id.environment_recyclerview)
    RecyclerView eRecyclerview;
    private View root;
    private EnvironmentAdapter adapter;
    private float environmentNum;


    private BroadcastReceiver environmentDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            float environment_qiya = intent.getFloatExtra("environment_qiya", -10000);
            if (environment_qiya != -10000) {
                setEnvironment(environment_qiya);
            }
        }
    };


    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter("data.receiver");
        getActivity().registerReceiver(environmentDataReceiver, intentFilter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_environment, container, false);
        ButterKnife.bind(this, root);
        initBroadcastReceiver();

        initRecyclerView();
        return root;
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        eRecyclerview.setLayoutManager(layoutManager);
        adapter = new EnvironmentAdapter(getActivity());
        eRecyclerview.setAdapter(adapter);
        adapter.setAdapterDateChangeListener(new EnvironmentAdapter.AdapterDateChangeListener() {
            @Override
            public void resetDate() {
                setEnvironment(environmentNum);
            }
        });
    }


    public void setEnvironment(float num) {
        environmentNum = num;
        List<Environment> list = new ArrayList<>();
        Environment[] environment = {null};
        environment[0] = getEnvironmentDate("气压", num);
        if (list != null) {
            if (list.size() != 0)
                list.clear();
            list.add(environment[0]);
            if(adapter!= null)
                adapter.setList(list);
        }
    }


    private Environment getEnvironmentDate(String name, float num) {
        Environment environment = new Environment();
        environment.setCurrent_num(num);
        environment.setName(name);
        switch (name) {
            case "气压":
                environment.setMin_num(BaseApplication.getSharedPreferences().getFloat(Environment.QIYA_MIN, (float) 0));
                environment.setMax_num(BaseApplication.getSharedPreferences().getFloat(Environment.QIYA_MAX, (float) 16.48));
                break;
        }
        return environment;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        getActivity().unregisterReceiver(environmentDataReceiver);
    }
}
