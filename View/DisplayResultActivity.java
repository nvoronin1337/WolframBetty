package com.projects.nikita.wolframbetty.View;
import android.support.v4.app.Fragment;

import com.projects.nikita.wolframbetty.Model.FullResultsWrapper;

public class DisplayResultActivity extends SingleFragmentActivity {

    private final static String KEY = "LIST";

    @Override
    protected Fragment createFragment() {
        return DisplayResultFragment.newInstance((FullResultsWrapper) getIntent().getSerializableExtra(KEY));
    }
}
