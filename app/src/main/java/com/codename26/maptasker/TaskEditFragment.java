package com.codename26.maptasker;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;


/**
 * A simple {@link Fragment} subclass.
 */
public class TaskEditFragment extends Fragment {
    private FloatingActionButton fab;
    private Task task;
    private EditText editName;
    private EditText editDescription;




    public TaskEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        task = bundle.getParcelable(MainActivity.NEW_TASK_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_edit, container, false);
        initSpinner(view);
        initViews(view);
        initFAB(view);
        return view;

    }

    private void initFAB(View view) {
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment mapFragment = new MapFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(MainActivity.NEW_TASK_KEY, task);
                mapFragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainer, mapFragment);
                transaction.commit();

            }
        });
    }

    private void initViews(View view) {
        editName = view.findViewById(R.id.editName);
        editDescription = view.findViewById(R.id.editDescription);
        if (task.getTaskName() != null){
            editName.setText(task.getTaskName());
            editDescription.setText((task.getTaskDescription()));
        }
    }

    private void initSpinner(View view) {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.task_edit_spinner_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = view.findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

}
