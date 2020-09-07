package com.nesibtodo.todoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.nesibtodo.todoapp.data.DatabaseHandler;
import com.nesibtodo.todoapp.model.Todo;
import com.nesibtodo.todoapp.ui.RecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Button goAppButton;
    private DatabaseHandler databaseHandler;
    private ArrayList<Todo> todoArrayList;
    private TextView completedCountTextView,completedPercentTextView,dateTextView;
    private RecyclerView recyclerView;
    private LinearLayout doneIcon;
    private RecyclerViewAdapter recyclerViewAdapter;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private SharedPreferences preferences;
    private final String[] MONTHS = new String[]{
            "Jan","Feb","Mar","Apr","Mai","Jun","Jul","Aug","Sep","Oct","Nov","Dec"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        completedCountTextView = findViewById(R.id.completedCount);
        completedPercentTextView = findViewById(R.id.completedPercent);
        dateTextView = findViewById(R.id.dateTextView);
        doneIcon = findViewById(R.id.doneIcon);
        preferences = getSharedPreferences("alertDialogPreference",MODE_PRIVATE);

        if(!preferences.getBoolean("seenDialogBox",false)){
            showHelpDialog();
            goAppButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("seenDialogBox",true);
                    editor.apply();
                    alertDialog.dismiss();
                }
            });
        }

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseHandler = DatabaseHandler.getInstance(this);
        todoArrayList = databaseHandler.getAllTodos();
        if(todoArrayList.size() == 0){
            doneIcon.setVisibility(View.VISIBLE);
        }
        setTime();
        calculateTodos();
        recyclerViewAdapter = new RecyclerViewAdapter(this,todoArrayList,completedCountTextView,completedPercentTextView);
        recyclerView.setAdapter(recyclerViewAdapter);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTodoActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_rtl,R.anim.slide_ltr);
            }
        });

    }

    public void showHelpDialog(){
        alertDialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.help_popup,null,false);
        alertDialogBuilder.setView(view);
        goAppButton = view.findViewById(R.id.goAppBtn);
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
    public void setTime(){
        String date= String.valueOf(java.time.LocalDate.now());
        int currentMonthIndex = Integer.parseInt(date.split("-")[1])-1;
        String year = date.split("-")[0];
        String day = date.split("-")[2];
        date = day + " " + MONTHS[currentMonthIndex] + ", " + year; // 3 Jul, 2020

        dateTextView.setText(date);
    }

    public void calculateTodos(){
        double completedPercentValue = 0;
        int completedCount=0;
        for(int i =0;i<todoArrayList.size();i++){
            if(todoArrayList.get(i).getIsDone()){
                completedCount++;
            }
        }
        completedPercentValue = Math.round((double) completedCount/todoArrayList.size() *100);
        completedCountTextView.setText(String.valueOf(completedCount));
        String percentText = String.format(Locale.getDefault(),"%.0f",completedPercentValue)+"% done";
        completedPercentTextView.setText(percentText);
    }



    @Override
    protected void onPostResume() {
        super.onPostResume();
        ArrayList<Todo> newTodoArrayList = databaseHandler.getAllTodos();
        if(todoArrayList.size() != newTodoArrayList.size()){
            if(doneIcon.getVisibility() == View.VISIBLE){
                doneIcon.setVisibility(View.INVISIBLE);
            }
            recyclerViewAdapter.setTodoList(newTodoArrayList);
            recyclerViewAdapter.notifyItemInserted(todoArrayList.size());
            todoArrayList = newTodoArrayList;
            calculateTodos();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Todo deletedTodo = todoArrayList.get(viewHolder.getAdapterPosition());
            databaseHandler.deleteTodo(deletedTodo.getId());
            todoArrayList.remove(viewHolder.getAdapterPosition());
            if(deletedTodo.getIsDone()){
                calculateTodos();
            }
            if(todoArrayList.size() == 0){
                doneIcon.setVisibility(View.VISIBLE);
            }
            recyclerViewAdapter.setTodoList(todoArrayList);
            recyclerViewAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
        }
    };
}