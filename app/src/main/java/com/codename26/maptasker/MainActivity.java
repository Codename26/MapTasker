package com.codename26.maptasker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String NEW_TASK_KEY = "NewTask";
    public static final String EDIT_TASK_KEY = "EditTask";
    public static final String TASK_ARRAY = "TaskArray";
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final String KEY_CAMERA_POSITION = "camera_position";
    public static final String KEY_LOCATION = "location";
    public static final int DEFAULT_ZOOM = 15;
    private ArrayList<GeoTask> mGeoTasks;
    private MapFragment mMapFragment;
    private BroadcastReceiver mBroadcastReceiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(!runtimePermissions()){

        }


        FragmentManager fm = getSupportFragmentManager();
        if (findViewById(R.id.fragmentContainer) != null) {
            if (savedInstanceState != null) {
                return;
            }
           initMapFragment();
            fm.beginTransaction().add(R.id.fragmentContainer, mMapFragment).commit();
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_startService) {
            Intent startIntent = new Intent(getApplicationContext(), GPSTaskService.class);
            startService(startIntent);
            return true;
        }
        if (id == R.id.action_stopService) {
            Intent stopIntent = new Intent(getApplicationContext(), GPSTaskService.class);
            stopService(stopIntent);
            return true;
        }
     /*   if (id == R.id.action_delete) {
            Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_edit) {
            Toast.makeText(this, "Edit", Toast.LENGTH_SHORT).show();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private TaskEditFragment.SaveTaskListener mSaveTaskListener = new TaskEditFragment.SaveTaskListener() {
        @Override
        public void saveTask(GeoTask geoTask) {
            DataBaseHelper helper = new DataBaseHelper(MainActivity.this);
            if (geoTask.getTaskId() > 0){
            helper.updateTask(geoTask);
                initMapFragment();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.fragmentContainer, mMapFragment);
                //transaction.addToBackStack(null);
                transaction.commit();
            }else if (geoTask.getTaskName().length() > 0) {
                helper.insertTask(geoTask);
                initMapFragment();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.fragmentContainer, mMapFragment);
                //transaction.addToBackStack(null);
                transaction.commit();
            }
        }
    };

    private MapFragment.DeleteTaskListener mDeleteTaskListener = new MapFragment.DeleteTaskListener(){

        @Override
        public void deleteGeoTask(long id) {
            DataBaseHelper helper = new DataBaseHelper(MainActivity.this);
            helper.deleteTask(id);
        }
    };

    private MapFragment.CreateTaskListener mCreateTaskListener = new MapFragment.CreateTaskListener(){

        @Override
        public void createTask(GeoTask geoTask) {
                TaskEditFragment taskEditFragment = new TaskEditFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(MainActivity.NEW_TASK_KEY, geoTask);
                taskEditFragment.setArguments(bundle);
                taskEditFragment.setSaveTaskListener(mSaveTaskListener);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.fragmentContainer, taskEditFragment);
                //transaction.addToBackStack(null);
                transaction.commit();
        }
    };

    private MapFragment.EditTaskListener mEditTaskListener = new MapFragment.EditTaskListener(){

        @Override
        public void editTask(GeoTask geoTask) {
            TaskEditFragment taskEditFragment = new TaskEditFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(MainActivity.EDIT_TASK_KEY, geoTask);
            taskEditFragment.setArguments(bundle);
            taskEditFragment.setSaveTaskListener(mSaveTaskListener);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.fragmentContainer, taskEditFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        }
    };

    private void initMapFragment(){
        mMapFragment = new MapFragment();
        DataBaseHelper helper = new DataBaseHelper(this);
        mGeoTasks = helper.getTasks();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(TASK_ARRAY, mGeoTasks);
        mMapFragment.setArguments(bundle);
        mMapFragment.setDeleteTaskListener(mDeleteTaskListener);
        mMapFragment.setCreateTaskListener(mCreateTaskListener);
        mMapFragment.setEditTaskListener(mEditTaskListener);
    }

    private boolean runtimePermissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission
                        .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest
                    .permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            } else {
                runtimePermissions();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(getApplicationContext(), GPSTaskService.class);
        startService(intent);

        System.out.println("********************Main Activity Strating Broadcast receiver***************************");
        if (mBroadcastReceiver == null){
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    System.out.println("+++++++++Received coordinates");
                    LatLng mCurrentCoordinates = (LatLng) intent.getExtras().get("coordinates");
                    System.out.println(mCurrentCoordinates.toString() + "++++++++++++++++++");
                    getDistanceToMarker(mCurrentCoordinates);
                }
            };
        }
        registerReceiver(mBroadcastReceiver, new IntentFilter("location_update"));
    }

    private void getDistanceToMarker(LatLng mCurrentCoordinates) {
        Location loc1 = new Location("");
        loc1.setLatitude(mCurrentCoordinates.latitude);
        loc1.setLongitude(mCurrentCoordinates.longitude);
        for (int i = 0; i < mGeoTasks.size(); i++) {
            Location loc2 = new Location("");
            loc2.setLatitude(mGeoTasks.get(i).getTaskLatitude());
            loc2.setLongitude(mGeoTasks.get(i).getTaskLongitude());
            float distanceInMeters = loc1.distanceTo(loc2);
            System.out.println("Calculating distance");
            if (distanceInMeters <= 200) {
                System.out.println("Distance to marker is " + distanceInMeters + "******************");
            }
        }
    }

   /* @Override
    public void onPause() {
        super.onPause();
        System.out.println("fragment On Pause");
        if (mBroadcastReceiver != null){
            unregisterReceiver(mBroadcastReceiver);
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
         if (mBroadcastReceiver != null){
            unregisterReceiver(mBroadcastReceiver);
        }
    }

}
