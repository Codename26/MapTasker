package com.codename26.maptasker;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
GoogleMap.OnMarkerClickListener {
    private GoogleMap mMap;
    FloatingActionButton fab;
    private ArrayList<Task> tasks = new ArrayList<>();
    boolean longClickPressed = false;
    Snackbar mSnackbar;
    private Task task;
    private HashMap<Double, Double> coordinates;
    private Menu mMenu;
    ArrayList<Marker> mMarkers;


    public MapFragment() {
        // Required empty public constructor

    }

    public static MapFragment newInstance(){
        return new MapFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //allow fragment to handle different menu items than it's root Activity
        setHasOptionsMenu(true);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(MainActivity.NEW_TASK_KEY)){
            task = arguments.getParcelable(MainActivity.NEW_TASK_KEY);
        }
        if (arguments != null && arguments.containsKey(MainActivity.TASK_ARRAY)){
            tasks = arguments.getParcelableArrayList(MainActivity.TASK_ARRAY);
        }
        if (tasks != null){
            coordinates = new HashMap<>();
        for (int i = 0; i < tasks.size(); i++) {

        coordinates.put(tasks.get(i).getTaskLatitude(), tasks.get(i).getTaskLongitude());
        }
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
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                       @Override
                                       public void onMapClick(LatLng latLng) {
                                           if (longClickPressed) {
                                               drawMap();
                                               fab.setVisibility(View.GONE);
                                               mSnackbar.dismiss();
                                               longClickPressed = false;
                                           }
                                           mMenu.findItem(R.id.action_delete).setVisible(false);
                                           mMenu.findItem(R.id.action_edit).setVisible(false);
                                           mMenu.findItem(R.id.action_settings).setVisible(true);
                                       }
                                   });

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                drawMap();
            }
        });

    }

    private void drawMap() {
        mMarkers = new ArrayList<>();
        mMap.clear();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
      Marker m =  mMap.addMarker(new MarkerOptions().position(new LatLng(task.getTaskLatitude(), task.getTaskLongitude()))
                    .title(task.getTaskName()));
            m.setTag(task);
            mMarkers.add(m);
            mMap.addCircle(new CircleOptions()
                    .center(new LatLng(task.getTaskLatitude(), task.getTaskLongitude()))
                    .radius(200)
                    .strokeWidth(2)
                    .strokeColor(Color.argb(153, 117, 200, 242))
                    .fillColor(Color.argb(153, 117, 200, 242)));

        }

        LatLngBounds.Builder bld = new LatLngBounds.Builder();
        for (int i = 0; i < tasks.size(); i++) {
            LatLng ll = new LatLng(tasks.get(i).getTaskLatitude(), tasks.get(i).getTaskLongitude());
            bld.include(ll);
        }
        LatLngBounds bounds = bld.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 135));
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



    @Override
    public boolean onMarkerClick(Marker marker) {
        mMenu.findItem(R.id.action_settings).setVisible(false);
        mMenu.findItem(R.id.action_delete).setVisible(true);
        mMenu.findItem(R.id.action_edit).setVisible(true);

        task = (Task) marker.getTag();

        return false;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;
    }

    private DeleteTaskListener mDeleteTaskListener;
    public void setDeleteTaskListener(DeleteTaskListener listener){
        mDeleteTaskListener = listener;
    }

    public interface DeleteTaskListener{
        void deleteTask(long id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_delete) {
           if (mDeleteTaskListener != null){
               mDeleteTaskListener.deleteTask(task.getTaskId());
               drawMap();
           }
            return true;
        }
        if (id == R.id.action_edit) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
