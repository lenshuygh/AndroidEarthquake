package com.lens.profandroidbook.myearthquake;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lens.profandroidbook.myearthquake.databinding.ListItemEarthquakeBinding;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EarthquakeRecyclerViewAdapter extends RecyclerView.Adapter<EarthquakeRecyclerViewAdapter.ViewHolder> {
    private final List<Earthquake> mEarthQuakes;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.US);
    private static final NumberFormat MAGNITUDE_FORMAT = new DecimalFormat("0.0");

    public EarthquakeRecyclerViewAdapter(List<Earthquake> earthQuakes) {
        this.mEarthQuakes = earthQuakes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemEarthquakeBinding binding = ListItemEarthquakeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Earthquake earthquake = mEarthQuakes.get(position);

        holder.binding.setEarthquake(earthquake);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mEarthQuakes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ListItemEarthquakeBinding binding;

        public ViewHolder(ListItemEarthquakeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.setDateformat(DATE_FORMAT);
            binding.setMagnitudeformat(MAGNITUDE_FORMAT);
        }

    }
}
