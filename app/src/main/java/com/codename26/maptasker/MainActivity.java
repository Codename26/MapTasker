package com.codename26.maptasker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String NEW_TASK_KEY = "NewTask";
    public static final String EDIT_TASK_KEY = "EditTask";
    public static final String TASK_ARRAY = "TaskArray";
    private ArrayList<Task> tasks;
    private MapFragment mMapFragment;

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
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
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
        public void saveTask(Task task) {
            DataBaseHelper helper = new DataBaseHelper(MainActivity.this);
            if (task.getTaskId() > 0){
            helper.updateTask(task);
                initMapFragment();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.fragmentContainer, mMapFragment);
                //transaction.addToBackStack(null);
                transaction.commit();
            }else if (task.getTaskName().length() > 0) {
                helper.insertTask(task);
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
        public void deleteTask(long id) {
            DataBaseHelper helper = new DataBaseHelper(MainActivity.this);
            helper.deleteTask(id);
        }
    };

    private MapFragment.CreateTaskListener mCreateTaskListener = new MapFragment.CreateTaskListener(){

        @Override
        public void createTask(Task task) {
                TaskEditFragment taskEditFragment = new TaskEditFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(MainActivity.NEW_TASK_KEY, task);
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
        public void editTask(Task task) {
            TaskEditFragment taskEditFragment = new TaskEditFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(MainActivity.EDIT_TASK_KEY, task);
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
        tasks = helper.getTasks();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(TASK_ARRAY, tasks);
        mMapFragment.setArguments(bundle);
        mMapFragment.setDeleteTaskListener(mDeleteTaskListener);
        mMapFragment.setCreateTaskListener(mCreateTaskListener);
        mMapFragment.setEditTaskListener(mEditTaskListener);
    }

}
