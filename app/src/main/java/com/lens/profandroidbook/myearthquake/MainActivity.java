package com.lens.profandroidbook.myearthquake;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity implements EarthquakeListFragment.OnListFragmentInteractionListener {

    private static final String TAG_LIST_FRAGMENT = "TAG_LIST_FRAGMENT";

    EarthquakeListFragment mEarthQuakeListFragment;

    EarthquakeViewModel earthquakeViewModel;

    private static final int MENU_PREFERENCES = Menu.FIRST+1;

    private static final int SHOW_PREFERENCES = 1;

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

    @Override
    public void onListFragmentRefreshRequested(){
        updateEarthQuakes();
    }

    private void updateEarthQuakes() {
        earthquakeViewModel.loadEarthquakes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0,MENU_PREFERENCES ,Menu.NONE,R.string.menu_settings);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case MENU_PREFERENCES:
                Intent intent = new Intent(this,PreferencesActivity.class);
                startActivityForResult(intent,SHOW_PREFERENCES);
                return true;
        }
        return false;
    }
}
