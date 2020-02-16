package com.lens.profandroidbook.myearthquake;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class EarthquakeViewModel extends AndroidViewModel {
    private static final String TAG = "EarthquakeUpdate";

    private LiveData<List<Earthquake>> earthquakes;

    public EarthquakeViewModel(Application application) {
        super(application);
    }

    public LiveData<List<Earthquake>> getEarthquakes() {
        if (earthquakes == null) {
            //earthquakes = new MutableLiveData<>();
            earthquakes =
                    EarthquakeDatabaseAccessor
                            .getInstance(getApplication())
                            .earthquakeDao()
                            .loadAllEarthQuakes();

            loadEarthquakes();
        }
        return earthquakes;
    }

    public void loadEarthquakes() {
        EarthQuakeUpdateJobsService.scheduleUpdateJob(getApplication());
    }
}
