package com.ozsaat.sunshine.ui;

import android.os.Bundle;

import com.ozsaat.sunshine.ui.forecast.ForecastFragment;

public class MainActivity extends BaseActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, ForecastFragment.newInstance())
                    .commit();
        }
    }
}
