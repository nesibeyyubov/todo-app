package com.nesibtodo.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.nesibtodo.todoapp.data.DatabaseHandler;
import com.nesibtodo.todoapp.model.Todo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class AddTodoActivity extends AppCompatActivity {
    private ImageButton backButton;
    private EditText todoName, todoPlace, todoTime;
    private DatabaseHandler databaseHandler;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtodo);

        databaseHandler = DatabaseHandler.getInstance(this);

        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.saveButton);
        todoName = findViewById(R.id.addTodoName);
        todoPlace = findViewById(R.id.addTodoPlace);
        todoTime = findViewById(R.id.addTodoTime);

        todoName.setFocusableInTouchMode(true);
        todoName.requestFocus();
        final InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMethodManager.hideSoftInputFromWindow(todoName.getWindowToken(),0);
                finish();
                overridePendingTransition(R.anim.slide_ltr_2,R.anim.slide_rtl_2);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoNameValue,todoPlaceValue,todoTimeValue;
                todoNameValue = todoName.getText().toString().trim();
                todoPlaceValue = todoPlace.getText().toString().trim();
                todoTimeValue = todoTime.getText().toString().trim();
                if(!todoNameValue.isEmpty()  && !todoTimeValue.isEmpty()){
                    Todo todo = new Todo(todoNameValue,todoPlaceValue,todoTimeValue,false);
                    databaseHandler.addTodo(todo);
                    inputMethodManager.hideSoftInputFromWindow(todoName.getWindowToken(),0);
                    finish();
                    overridePendingTransition(R.anim.slide_ltr_2,R.anim.slide_rtl_2);
                }
                else{
                    Toast.makeText(AddTodoActivity.this,"Please fill all the fields",Toast.LENGTH_LONG).show();
                }
            }
        });

        todoTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    hideKeyboard();
                    openTimePickerDialog();
                }
            }
        });

        todoTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                openTimePickerDialog();
            }
        });

    }

    public void openTimePickerDialog(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                AddTodoActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = hourOfDay+":"+minute;
                        SimpleDateFormat _24HourDateFormat = new SimpleDateFormat("HH:mm");
                        SimpleDateFormat _12HourDateFormat = new SimpleDateFormat("hh:mm a");
                        try {
                            Date _24HourDate = _24HourDateFormat.parse(time);
                            assert _24HourDate != null;
                            todoTime.setText(_12HourDateFormat.format(_24HourDate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                6,0,false
        );

        Objects.requireNonNull(timePickerDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.show();
    }


    public void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
         inputMethodManager.hideSoftInputFromWindow(todoTime.getWindowToken(),0);
    }
}