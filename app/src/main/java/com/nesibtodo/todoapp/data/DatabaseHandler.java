package com.nesibtodo.todoapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.nesibtodo.todoapp.model.Todo;
import com.nesibtodo.todoapp.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private Context context;
    private static DatabaseHandler mDatabaseHandlerInstance;
    public DatabaseHandler(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        this.context = context;
    }

    public static DatabaseHandler getInstance(Context context){
        if(mDatabaseHandlerInstance == null){
            mDatabaseHandlerInstance = new DatabaseHandler(context);
        }
        return mDatabaseHandlerInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Constants.TABLE_NAME + "(" +
                Constants.KEY_ID + " INTEGER PRIMARY KEY," +
                Constants.KEY_NAME + " TEXT," +
                Constants.KEY_PLACE + " TEXT," +
                Constants.KEY_ISDONE + " TEXT," +
                Constants.KEY_TIME + " TEXT"+")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
    }

    public void addTodo(Todo todo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.KEY_NAME,todo.getName());
        values.put(Constants.KEY_PLACE,todo.getPlace());
        values.put(Constants.KEY_TIME,todo.getTime());
        values.put(Constants.KEY_ISDONE,todo.getIsDone());

        db.insert(Constants.TABLE_NAME,null,values);
    }

    public void deleteTodo(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Constants.TABLE_NAME,Constants.KEY_ID + "=?",new String[]{String.valueOf(id)});
    }

    public Todo getTodo(int id){
        Todo todo = new Todo();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                Constants.TABLE_NAME,
                new String[]{Constants.KEY_NAME,Constants.KEY_PLACE,Constants.KEY_TIME,Constants.KEY_ID,Constants.KEY_ISDONE},
                Constants.KEY_ID + "=?",
                new String[]{String.valueOf(id)},
                null,null,null
                );

        if(cursor != null && cursor.moveToFirst()){
            String name = cursor.getString(cursor.getColumnIndex(Constants.KEY_NAME));
            String time = cursor.getString(cursor.getColumnIndex(Constants.KEY_TIME));
            String place = cursor.getString(cursor.getColumnIndex(Constants.KEY_PLACE));
            boolean isDone = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(Constants.KEY_ISDONE)));
            int todoId = cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID));
            todo.setName(name);
            todo.setId(todoId);
            todo.setPlace(place);
            todo.setTime(time);
            todo.setIsDone(isDone);
        }

        cursor.close();

        return todo;
    }

    public ArrayList<Todo> getAllTodos(){
        ArrayList<Todo> todoArrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.TABLE_NAME,null);

        if(cursor != null && cursor.moveToNext()){
            do{
                Todo todo = new Todo();
                String name = cursor.getString(cursor.getColumnIndex(Constants.KEY_NAME));
                String time = cursor.getString(cursor.getColumnIndex(Constants.KEY_TIME));
                String place = cursor.getString(cursor.getColumnIndex(Constants.KEY_PLACE));
                int id = cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID));
                boolean isDone = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(Constants.KEY_ISDONE)));
                todo.setName(name);
                todo.setId(id);
                todo.setPlace(place);
                todo.setTime(time);
                todo.setIsDone(isDone);

                todoArrayList.add(todo);

            }while (cursor.moveToNext());
            cursor.close();
        }
        return todoArrayList;
    }

    public int getTodoCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.TABLE_NAME,null);
        cursor.close();
        return cursor.getCount();
    }

    public void updateTodo(int id,Todo newTodo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.KEY_NAME,newTodo.getName());
        values.put(Constants.KEY_PLACE,newTodo.getPlace());
        values.put(Constants.KEY_TIME,newTodo.getTime());
        values.put(Constants.KEY_ISDONE,String.valueOf(newTodo.getIsDone()));

        db.update(Constants.TABLE_NAME,values,Constants.KEY_ID + "=?",new String[]{String.valueOf(newTodo.getId())});
        db.close();
    }
}
