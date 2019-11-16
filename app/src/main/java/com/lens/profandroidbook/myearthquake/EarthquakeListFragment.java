package com.lens.profandroidbook.myearthquake;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeListFragment extends Fragment {
    private ArrayList<Earthquake> mEartquakes = new ArrayList<>();

    private RecyclerView recyclerView;
    private EarthquakeRecyclerViewAdapter earthquakeRecyclerViewAdapter = new EarthquakeRecyclerViewAdapter(mEartquakes);

    protected EarthquakeViewModel earthquakeViewModel;

    private SwipeRefreshLayout swipeRefreshLayout;

    public EarthquakeListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_earthquake_list, container, false);
        recyclerView = view.findViewById(R.id.list);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set recycler view adapter
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(earthquakeRecyclerViewAdapter);

        // setup swipe to refresh view
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateEarthquakes();
            }
        });
    }

    public void setmEartquakes(List<Earthquake> earthquakes) {
        /*mEartquakes.clear();
        earthquakeRecyclerViewAdapter.notifyDataSetChanged();*/

        for (Earthquake earthquake : earthquakes) {
            if (!mEartquakes.contains(earthquake)) {
                mEartquakes.add(earthquake);
                earthquakeRecyclerViewAdapter.notifyItemInserted(mEartquakes.indexOf(earthquake));
            }
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        earthquakeViewModel = ViewModelProviders.of(getActivity()).get(EarthquakeViewModel.class);

        earthquakeViewModel.getEarthquakes()
                .observe(getViewLifecycleOwner(), new Observer<List<Earthquake>>() {
                    @Override
                    public void onChanged(List<Earthquake> earthquakes) {
                        if(earthquakes != null){
                            setmEartquakes(earthquakes);
                        }
                    }
                });
    }

    public interface OnListFragmentInteractionListener{
        void onListFragmentRefreshRequested();
    }

    private OnListFragmentInteractionListener onListFragmentInteractionListener;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        onListFragmentInteractionListener = (OnListFragmentInteractionListener) context;
    }

    @Override
    public void onDetach(){
        super.onDetach();
        onListFragmentInteractionListener = null;
    }

    protected void updateEarthquakes(){
        if(onListFragmentInteractionListener != null){
            onListFragmentInteractionListener.onListFragmentRefreshRequested();
        }
    }
}
