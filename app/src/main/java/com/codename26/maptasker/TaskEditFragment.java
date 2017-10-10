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
    private boolean isNewTask = false;




    public TaskEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle.containsKey(MainActivity.NEW_TASK_KEY)) {
            task = bundle.getParcelable(MainActivity.NEW_TASK_KEY);
            isNewTask = true;
        } else if (bundle.containsKey(MainActivity.EDIT_TASK_KEY)){
            task = bundle.getParcelable(MainActivity.EDIT_TASK_KEY);
        }
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
                if (isNewTask){
                    task.setTaskId(0);
                }
                task.setTaskName(String.valueOf(editName.getText()));
                task.setTaskDescription(String.valueOf(editDescription.getText()));
if (mSaveTaskListener != null) {
    mSaveTaskListener.saveTask(task);
                }

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


    private SaveTaskListener mSaveTaskListener;

    public void setSaveTaskListener(SaveTaskListener listener){
        mSaveTaskListener = listener;
    }

    public interface SaveTaskListener {
       void saveTask(Task task);
    }

}
