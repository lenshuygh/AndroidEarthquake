package com.lens.profandroidbook.myearthquake;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity
public class Earthquake {
    @NonNull
    @PrimaryKey
    private String id;
    @TypeConverters(DateConverter.class)
    private Date date;
    private String details;
    private Location location;
    private double magnitude;
    private String link;

    public Earthquake(String id, Date date, String details, Location location, double magnitude, String link) {
        this.id = id;
        this.date = date;
        this.details = details;
        this.location = location;
        this.magnitude = magnitude;
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getDetails() {
        return details;
    }

    public Location getLocation() {
        return location;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public String getLink() {
        return link;
    }

    @NonNull
    @Override
    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH.mm", Locale.US);
        String dateString = simpleDateFormat.format(date);
        return dateString + ": " + magnitude + " " + details;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Earthquake) {
            return ((Earthquake) obj).getId().contentEquals(id);
        } else {
            return false;
        }
    }
}
