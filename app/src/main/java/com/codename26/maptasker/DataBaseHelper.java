package com.codename26.maptasker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {
    public DataBaseHelper(Context context) {
        super(context, "MapTasker.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + Task.TABLE_NAME + "("
                + Task.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Task.COLUMN_TASK_NAME + " TEXT NOT NULL,"
                + Task.COLUMN_TASK_DESCRIPTION + " TEXT,"
                + Task.COLUMN_TASK_TAG + " TEXT,"
                + Task.COLUMN_TASK_LATITUDE + " REAL NOT NULL,"
                + Task.COLUMN_TASK_LONGITUDE + " REAL NOT NULL);");

        fillDB(sqLiteDatabase);

    }

    private void fillDB(SQLiteDatabase sqLiteDatabase) {
        double[] latArray = {50.516303, 50.515873, 50.515795, 50.512714};
        double[] lonArray = {30.455847, 30.442139, 30.432531, 30.417893};
        try {
            for (int i = 0; i < 4; i++) {
                ContentValues values = new ContentValues();
                values.put(Task.COLUMN_TASK_NAME, "Task " + i );
                values.put(Task.COLUMN_TASK_DESCRIPTION, "Desc " + i );
                values.put(Task.COLUMN_TASK_TAG, "Tag " + i );
                values.put(Task.COLUMN_TASK_LATITUDE, latArray[i]);
                values.put(Task.COLUMN_TASK_LONGITUDE , lonArray[i]);

                sqLiteDatabase.insert(Task.TABLE_NAME, null, values);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean updateTask(Task task){
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(Task.COLUMN_TASK_NAME, task.getTaskName());
            values.put(Task.COLUMN_TASK_DESCRIPTION, task.getTaskDescription());
            values.put(Task.COLUMN_TASK_TAG, task.getTaskTag());
            values.put(Task.COLUMN_TASK_LATITUDE, task.getTaskLatitude());
            values.put(Task.COLUMN_TASK_LONGITUDE , task.getTaskLongitude());

            db.update(Task.TABLE_NAME, values, Task.COLUMN_ID + "=" + task.getTaskId(),null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public long insertTask(Task task){
        long id = 0;
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(Task.COLUMN_TASK_NAME, task.getTaskName());
            values.put(Task.COLUMN_TASK_DESCRIPTION, task.getTaskDescription());
            values.put(Task.COLUMN_TASK_TAG, task.getTaskTag());
            values.put(Task.COLUMN_TASK_LATITUDE, task.getTaskLatitude());
            values.put(Task.COLUMN_TASK_LONGITUDE , task.getTaskLongitude());

            id = db.insert(Task.TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }

    public long newTask(){
        long id = 0;
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(Task.COLUMN_TASK_NAME, "");
            values.put(Task.COLUMN_TASK_DESCRIPTION, "");
            values.put(Task.COLUMN_TASK_TAG, "");
            values.put(Task.COLUMN_TASK_LATITUDE, 0);
            values.put(Task.COLUMN_TASK_LONGITUDE , 0);

            id = db.insert(Task.TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }

    public Task getTask(long id){
        SQLiteDatabase db = getWritableDatabase();
        Task task = new Task();
        Cursor cursor = null;

        try {
            cursor = db.query(Task.TABLE_NAME, null, Task.COLUMN_ID + "=" + id, null, null, null, null);

            if (cursor.moveToFirst()) {

                    task.setTaskId(cursor.getLong(cursor.getColumnIndex(Task.COLUMN_ID)));
                    task.setTaskName(cursor.getString(cursor.getColumnIndex(Task.COLUMN_TASK_NAME)));
                    task.setTaskDescription(cursor.getString(cursor.getColumnIndex(Task.COLUMN_TASK_DESCRIPTION)));
                    task.setTaskTag(cursor.getString(cursor.getColumnIndex(Task.COLUMN_TASK_TAG)));
                    task.setTaskLatitude(cursor.getDouble(cursor.getColumnIndex(Task.COLUMN_TASK_LATITUDE)));
                    task.setTaskLongitude(cursor.getDouble(cursor.getColumnIndex(Task.COLUMN_TASK_LONGITUDE)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return task;
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(Task.TABLE_NAME, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Task task = new Task();

                    task.setTaskId(cursor.getLong(cursor.getColumnIndex(Task.COLUMN_ID)));
                    task.setTaskName(cursor.getString(cursor.getColumnIndex(Task.COLUMN_TASK_NAME)));
                    task.setTaskDescription(cursor.getString(cursor.getColumnIndex(Task.COLUMN_TASK_DESCRIPTION)));
                    task.setTaskTag(cursor.getString(cursor.getColumnIndex(Task.COLUMN_TASK_TAG)));
                    task.setTaskLatitude(cursor.getDouble(cursor.getColumnIndex(Task.COLUMN_TASK_LATITUDE)));
                    task.setTaskLongitude(cursor.getDouble(cursor.getColumnIndex(Task.COLUMN_TASK_LONGITUDE)));
                    tasks.add(task);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return tasks;
    }

    public boolean deleteTask(long id) {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();

        try {
            count = db.delete(Task.TABLE_NAME, Task.COLUMN_ID + "=" + id, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count > 0;
    }


}
