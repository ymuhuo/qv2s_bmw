package com.bmw.peek2.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NotNull;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by admin on 2017/11/1.
 */
@Table(name = "BZInfo", execAfterTableCreated = "")
public class BZInfo   implements Comparable,Serializable {

    @Id(column = "key")
    private int id;
    @Column(column = "msg")
    private String msg;
    @Column(column = "time")
    private long time;
    @Column(column =  "row")
    private int row;

    public BZInfo(int id, String msg, long time, int row) {
        this.id = id;
        this.msg = msg;
        this.time = time;
        this.row = row;
    }

    public BZInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    @Override
    public int compareTo(Object o) {
        BZInfo bzInfo = (BZInfo) o;
//        int ids = Integer.valueOf(recordTaskInfo.getTask_id());

//        return  Integer.valueOf(this.task_id).compareTo(ids);
        return  Integer.valueOf(this.id).compareTo(bzInfo.getId());
    }
}
