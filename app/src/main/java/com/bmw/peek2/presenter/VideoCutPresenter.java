package com.bmw.peek2.presenter;

/**
 * Created by admin on 2016/9/30.
 */
public interface VideoCutPresenter {
    void record(String recordName,boolean isAddKanban,String kanbanPath);
    void capture(String captureName);
//    void reRecord(int count);
    void release();
}
