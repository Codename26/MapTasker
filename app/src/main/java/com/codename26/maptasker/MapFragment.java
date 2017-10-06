package com.codename26.maptasker;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private double lattitude = 50.515969;
    private double longitude = 30.425726;
    private GoogleMap mMap;
    FloatingActionButton fab;
    private ArrayList<Task> tasks = new ArrayList<>();
    boolean longClickPressed = false;
    LatLng coordinates;
    Snackbar mSnackbar;
    private Task task;


    public MapFragment() {
        // Required empty public constructor

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(MainActivity.NEW_TASK_KEY)){
            task = arguments.getParcelable(MainActivity.NEW_TASK_KEY);
        }
        if (arguments != null && arguments.containsKey(MainActivity.TASK_ARRAY)){
            tasks = arguments.getParcelableArrayList(MainActivity.TASK_ARRAY);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fab = view.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Fragment taskEditFragment = new TaskEditFragment();
                DataBaseHelper helper = new DataBaseHelper(getActivity());
                long id = helper.newTask();
                Task task = new Task();
                task.setTaskId(id);
                Bundle bundle = new Bundle();
                bundle.putParcelable(MainActivity.NEW_TASK_KEY, task);
                taskEditFragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, taskEditFragment);
                //transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker and move the camera
       coordinates = new LatLng(lattitude, longitude);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(longClickPressed){
                    drawMap();
                    fab.setVisibility(View.GONE);
                    mSnackbar.dismiss();

                }
            }
        });
        drawMap();

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds AUSTRALIA = new LatLngBounds(
                        new LatLng(-44, 113), new LatLng(-10, 154));
             //   mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(AUSTRALIA, 0));
            }
        });

    }

    private void drawMap() {
        mMap.clear();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            mMap.addMarker(new MarkerOptions().position(new LatLng(task.getTaskLatitude(), task.getTaskLongitude()))
                    .title(task.getTaskName()));
            mMap.addCircle(new CircleOptions()
                    .center(new LatLng(task.getTaskLatitude(), task.getTaskLongitude()))
                    .radius(200)
                    .strokeWidth(2)
                    .strokeColor(Color.argb(153, 117, 200, 242))
                    .fillColor(Color.argb(153, 117, 200, 242)));

        }
        /*
        mMap.addMarker(new MarkerOptions().position(coordinates).title("Test Title"));
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(lattitude,longitude))
                .radius(200)
                .strokeWidth(2)
                .strokeColor(Color.argb(153, 117, 200, 242))
                .fillColor(Color.argb(153, 117, 200, 242)));
                */
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15.5f));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        longClickPressed = true;
            mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(200)
                .strokeWidth(2)
                .strokeColor(Color.argb(153, 117, 200, 242))
                .fillColor(Color.argb(153, 117, 200, 242)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.5f));
        fab.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.appear);
        fab.setAnimation(anim);
        mSnackbar = Snackbar.make(fab, "Add task at this location", Snackbar.LENGTH_INDEFINITE);
        mSnackbar.show();
        }

}
