package com.lens.profandroidbook.myearthquake;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_LIST_FRAGMENT = "TAG_LIST_FRAGMENT";

    EarthquakeListFragment mEarthQuakeListFragment;

    EarthquakeViewModel earthquakeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            mEarthQuakeListFragment = new EarthquakeListFragment();
            fragmentTransaction.add(R.id.main_activity_frame, mEarthQuakeListFragment, TAG_LIST_FRAGMENT);
            fragmentTransaction.commitNow();
        } else {
            mEarthQuakeListFragment = (EarthquakeListFragment) fragmentManager.findFragmentByTag(TAG_LIST_FRAGMENT);
        }

    earthquakeViewModel = ViewModelProviders.of(this).get(EarthquakeViewModel.class);

    }
}
