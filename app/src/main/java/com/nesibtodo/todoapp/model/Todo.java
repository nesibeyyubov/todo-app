package com.nesibtodo.todoapp.model;

public class Todo {
    public String name;
    public String place;
    public String time;
    public boolean isDone;
    public int id;

    @Override
    public String toString() {
        return "{" +
                "name:'" + name + '\'' +
                ", place:'" + place + '\'' +
                ", time:'" + time + '\'' +
                ", isDone:" + isDone +
                ", id:" + id +
                '}';
    }

    public Todo() {
    }
    public Todo(String name,  String place, String time, boolean isDone) {
        this.name = name;
        this.place = place;
        this.time = time;
        this.isDone = isDone;
    }

    public Todo(String name,  String place, String time, boolean isDone,int id) {
        this.name = name;
        this.place = place;
        this.id = id;
        this.time = time;
        this.isDone = isDone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTime() {
        return time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean getIsDone() {
        return isDone;
    }

    public void setIsDone(boolean done) {
        isDone = done;
    }
}
