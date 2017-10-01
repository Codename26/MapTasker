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

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

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
