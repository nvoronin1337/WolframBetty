package com.projects.nikita.wolframbetty.View;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import com.projects.nikita.wolframbetty.Model.FullResultsWrapper;
import com.projects.nikita.wolframbetty.R;


public abstract class SingleFragmentActivity extends AppCompatActivity {

    private final static String TAG = "SINGLE FRAGMENT";
    private final static String KEY = "LIST";

    protected abstract Fragment createFragment();

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.result_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = DisplayResultFragment.newInstance((FullResultsWrapper) getIntent().getSerializableExtra(KEY));
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }
}
