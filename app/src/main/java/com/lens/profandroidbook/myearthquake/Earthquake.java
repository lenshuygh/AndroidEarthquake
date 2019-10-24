package com.lens.profandroidbook.myearthquake;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Earthquake {
    private String mId;
    private Date mDate;
    private String mDetails;
    private Location location;
    private double mMagnitude;
    private String mLink;

    public Earthquake(String mId, Date mDate, String mDetails, Location location, double mMagnitude, String mLink) {
        this.mId = mId;
        this.mDate = mDate;
        this.mDetails = mDetails;
        this.location = location;
        this.mMagnitude = mMagnitude;
        this.mLink = mLink;
    }

    public String getmId() {
        return mId;
    }

    public Date getmDate() {
        return mDate;
    }

    public String getmDetails() {
        return mDetails;
    }

    public Location getLocation() {
        return location;
    }

    public double getmMagnitude() {
        return mMagnitude;
    }

    public String getmLink() {
        return mLink;
    }

    @NonNull
    @Override
    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH.mm", Locale.US);
        String dateString = simpleDateFormat.format(mDate);
        return dateString + ": " + mMagnitude + " " + mDetails;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Earthquake){
            return ((Earthquake)obj).getmId().contentEquals(mId);
        }else {
            return false;
        }
    }
}
