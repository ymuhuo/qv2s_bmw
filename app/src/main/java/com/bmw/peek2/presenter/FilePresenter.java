package com.bmw.peek2.presenter;

/**
 * Created by admin on 2016/9/8.
 */
public interface FilePresenter {
    void initAdapter();
    void searching(String str);
    void deleteFile();
    int lastItem();
    int nextItem();
    void chooseAll();
    void cancelAll();
    boolean isGetShowFile();
    void setEditModel(boolean isEdit);
    void copyFile(String targetPath);
}
