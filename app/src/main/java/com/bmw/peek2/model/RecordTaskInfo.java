package com.bmw.peek2.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NotNull;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by admin on 2017/2/28.
 */


@Table(name = "RecordTaskInfo", execAfterTableCreated = "")
public class RecordTaskInfo  implements Comparable,Serializable{

    @Id(column = "key")
    private int id;
    @Column(column = "task_id")
    private String task_id;
    @Column(column = "task_name")
    private String task_name;
    @Column(column = "task_place")
    private String task_place;
    @Column(column = "task_start")
    private String task_start;
    @Column(column = "task_end")
    private String task_end;
    @Column(column = "task_direction")
    private String task_direction;
    @Column(column = "task_sort")
    private String task_sort;
    @Column(column = "task_guancai")
    private String task_guancai;
    @Column(column = "task_diameter")
    private String task_diameter;
    @Column(column = "task_computer")
    private String task_computer;
    @Column(column = "task_people")
    private String task_people;


    public RecordTaskInfo() {

    }

    public RecordTaskInfo(long id,String task_id, String task_name,
                          String task_place, String task_start,
                          String task_end, String task_direction,
                          String task_sort, String task_guancai,
                          String task_diameter, String task_computer,
                          String task_people) {
        this.task_id = task_id;
        this.task_name = task_name;
        this.task_place = task_place;
        this.task_start = task_start;
        this.task_end = task_end;
        this.task_direction = task_direction;
        this.task_sort = task_sort;
        this.task_guancai = task_guancai;
        this.task_diameter = task_diameter;
        this.task_computer = task_computer;
        this.task_people = task_people;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RecordTaskInfo(String task_id) {
        this.task_id = task_id;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getTask_place() {
        return task_place;
    }

    public void setTask_place(String task_place) {
        this.task_place = task_place;
    }

    public String getTask_start() {
        return task_start;
    }

    public void setTask_start(String task_start) {
        this.task_start = task_start;
    }

    public String getTask_end() {
        return task_end;
    }

    public void setTask_end(String task_end) {
        this.task_end = task_end;
    }

    public String getTask_direction() {
        return task_direction;
    }

    public void setTask_direction(String task_direction) {
        this.task_direction = task_direction;
    }

    public String getTask_sort() {
        return task_sort;
    }

    public void setTask_sort(String task_sort) {
        this.task_sort = task_sort;
    }

    public String getTask_guancai() {
        return task_guancai;
    }

    public void setTask_guancai(String task_guancai) {
        this.task_guancai = task_guancai;
    }

    public String getTask_diameter() {
        return task_diameter;
    }

    public void setTask_diameter(String task_diameter) {
        this.task_diameter = task_diameter;
    }

    public String getTask_computer() {
        return task_computer;
    }

    public void setTask_computer(String task_computer) {
        this.task_computer = task_computer;
    }

    public String getTask_people() {
        return task_people;
    }

    public void setTask_people(String task_people) {
        this.task_people = task_people;
    }

    @Override
    public int compareTo(Object o) {
        RecordTaskInfo recordTaskInfo = (RecordTaskInfo) o;
//        int ids = Integer.valueOf(recordTaskInfo.getTask_id());

//        return  Integer.valueOf(this.task_id).compareTo(ids);
        return  Integer.valueOf(this.id).compareTo(recordTaskInfo.getId());
    }
}
