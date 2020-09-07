package com.nesibtodo.todoapp.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.nesibtodo.todoapp.R;
import com.nesibtodo.todoapp.data.DatabaseHandler;
import com.nesibtodo.todoapp.model.Todo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Todo> todoArrayList;
    private DatabaseHandler databaseHandler;
    private TextView completedCountText,completedPercent;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;

    public RecyclerViewAdapter(Context context, ArrayList<Todo> todoArrayList,TextView completedCountText,TextView completedPercent) {
        this.context = context;
        this.todoArrayList = todoArrayList;
        databaseHandler = DatabaseHandler.getInstance(context);
        this.completedCountText = completedCountText;
        this.completedPercent = completedPercent;
    }

    public void setTodoList(ArrayList<Todo> todoArrayList){
        this.todoArrayList = todoArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.todo_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Todo todo = todoArrayList.get(position);
        holder.todoName.setText(todo.getName());
        holder.todoPlace.setText(todo.getPlace());
        holder.todoTime.setText(todo.getTime());

        if(todo.getIsDone()){
            holder.todoRow.setBackgroundResource(R.drawable.border_bottom_done_bg);
            holder.doneButtonBackground.setBackgroundResource(R.drawable.button_done_bg);
            holder.doneImage.setVisibility(View.VISIBLE);
        }
        else{
            holder.todoRow.setBackgroundResource(R.drawable.border_bottom_background);
            holder.doneButtonBackground.setBackgroundResource(R.drawable.circle_border);
            holder.doneImage.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    public int getItemCount() {
        return todoArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView todoName, todoPlace, todoTime;
        public Button doneButton;
        public LinearLayout todoRow;
        public ImageView doneImage;
        public RelativeLayout doneButtonBackground;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            todoName = itemView.findViewById(R.id.todoName);
            todoPlace = itemView.findViewById(R.id.todoPlace);
            todoTime = itemView.findViewById(R.id.todoTime);
            doneButton = itemView.findViewById(R.id.doneButton);
            todoRow = itemView.findViewById(R.id.todoRow);
            doneImage = itemView.findViewById(R.id.doneImage);
            doneButtonBackground = itemView.findViewById(R.id.doneButtonBackground);

            doneButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Animation scaleAnimation = AnimationUtils.loadAnimation(context,R.anim.scale_button);
            doneButtonBackground.startAnimation(scaleAnimation);

            int position = getAdapterPosition();
            Todo todo = todoArrayList.get(position);
            todo.setIsDone(!todo.getIsDone());
            databaseHandler.updateTodo(position,todo);

            notifyItemChanged(position,todo);
            calculateTodos();

        }

        public void calculateTodos(){
            double completedCount = 0;
            double completedPercentValue;
            for(int i =0;i<todoArrayList.size();i++){
                if(todoArrayList.get(i).getIsDone()){
                    completedCount++;
                }
            }
            completedPercentValue = Math.round(completedCount/todoArrayList.size() *100);
            completedCountText.setText(String.valueOf((int)completedCount));
            String percentText = String.format(Locale.getDefault(),"%.0f",completedPercentValue)+"% done";
            completedPercent.setText(percentText);

            if(todoArrayList.size() > 0 && completedCount>0){
                if(completedPercentValue == 100){
                    showSuccessDialog();
                }
            }
        }

        public void showSuccessDialog(){
            alertDialogBuilder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.alltodos_done_popup,null,false);
            alertDialogBuilder.setView(view);
            alertDialog = alertDialogBuilder.create();
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(alertDialog.isShowing()){
                        alertDialog.dismiss();
                    }
                }
            },3500);
        }
    }
}
