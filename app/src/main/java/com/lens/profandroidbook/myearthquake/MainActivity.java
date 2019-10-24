package com.lens.profandroidbook.myearthquake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_LIST_FRAGMENT = "TAG_LIST_FRAGMENT";

    EarthquakeListFragment mEarthQuakeListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();

        if(savedInstanceState == null){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            mEarthQuakeListFragment = new EarthquakeListFragment();
            fragmentTransaction.add(R.id.main_activity_frame,mEarthQuakeListFragment,TAG_LIST_FRAGMENT);
            fragmentTransaction.commitNow();
        }else {
            mEarthQuakeListFragment = (EarthquakeListFragment) fragmentManager.findFragmentByTag(TAG_LIST_FRAGMENT);
        }

        Date now = Calendar.getInstance().getTime();
        List<Earthquake> dummyQuakes = new ArrayList<>(0);
        dummyQuakes.add(new Earthquake("0",now,"San Jose",null,7.0,null));
        dummyQuakes.add(new Earthquake("1",now,"LA",null,6.5,null));

        mEarthQuakeListFragment.setmEartquakes(dummyQuakes);

    }
}
