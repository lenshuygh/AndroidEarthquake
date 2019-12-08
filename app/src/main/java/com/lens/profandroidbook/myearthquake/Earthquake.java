package com.lens.profandroidbook.myearthquake;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity
public class Earthquake {
    @NonNull
    @PrimaryKey
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

    public Date getMDate() {
        return mDate;
    }

    public String getMDetails() {
        return mDetails;
    }

    public Location getLocation() {
        return location;
    }

    public double getMMagnitude() {
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
        if (obj instanceof Earthquake) {
            return ((Earthquake) obj).getmId().contentEquals(mId);
        } else {
            return false;
        }
    }
}
