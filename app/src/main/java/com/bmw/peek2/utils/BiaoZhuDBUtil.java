package com.bmw.peek2.utils;

import com.bmw.peek2.model.BZInfo;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by admin on 2017/11/1.
 */

public class BiaoZhuDBUtil {
    private static final int MAX_ROW = 5;

    private DbUtils mDbUtils;
    private List<BZInfo> mList;

    public BiaoZhuDBUtil() {
        this.mDbUtils = DbHelper.getDbUtils();
        try {
//            mDbUtils.deleteAll(BZInfo.class);
            mList = mDbUtils.findAll(BZInfo.class);
        } catch (DbException e) {
            e.printStackTrace();
            LogUtil.error("BZInfo find all Data false!!!");
        }
    }


    private List<BZInfo> getRowData(int row) {

        List<BZInfo> list = new ArrayList<>();
        if (mList != null) {
            for (BZInfo bzInfo : mList) {
                LogUtil.log("mlist size = " + mList.size() + "  bzinfo = " + bzInfo);
                if (bzInfo == null) {

                } else if (bzInfo.getRow() == row)
                    list.add(bzInfo);
            }
            if (list.size() > 1) {
                BZComparator comparator = new BZComparator();
                Collections.sort(list, comparator);
            }
            if (list.size() > MAX_ROW) {
                for (int i = list.size() - 1; i >= MAX_ROW; i--) {
                    delete(list.get(i));
                    list.remove(i);
                }
            }
        }
        return list;

    }

    public void add(BZInfo bzInfo, List<BZInfo> list) {
        if (list == null)
            list = getRowData(bzInfo.getRow());
        boolean isExist = isExist(bzInfo.getMsg(), list);
        if (!isExist) {
            add(bzInfo);
        }

    }

    public void delete(String str, int row) {
        if (mList != null)
            for (int i = mList.size() - 1; i >= 0; i--) {
                BZInfo bzInfo = mList.get(i);
                if (bzInfo.getRow() == row && bzInfo.getMsg().equals(str)) {
                    delete(bzInfo);
                    mList.remove(bzInfo);
                }
            }

    }

    public void deleteAll() {
        try {
            mDbUtils.deleteAll(BZInfo.class);
        } catch (DbException e) {
            e.printStackTrace();
            LogUtil.error("delete BZInfo All false!!!");
        }
        if (mList != null)
            mList.clear();
    }

    public String[] getRowDataString(int row) {
        List<BZInfo> list = getRowData(row);
        String[] str = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            str[i] = list.get(i).getMsg();
        }
        return str;
    }

    private boolean isExist(String str, List<BZInfo> list) {
        for (BZInfo bzInfo : list) {
            if (bzInfo.getMsg().equals(str))
                return true;
        }

        return false;
    }

    private void delete(BZInfo bzInfo) {
        try {
            mDbUtils.delete(bzInfo);
        } catch (DbException e) {
            e.printStackTrace();
            LogUtil.error("BZInfo delete Data false!!!");
        }
    }

    private void add(BZInfo bzInfo) {
        LogUtil.log("add BZInfo " + bzInfo);
        try {
            mDbUtils.save(bzInfo);
        } catch (DbException e) {
            e.printStackTrace();
            LogUtil.error("BZInfo add Data false!!!");
        }
    }


    private class BZComparator implements Comparator<BZInfo> {

        @Override
        public int compare(BZInfo o1, BZInfo o2) {
            int flag = Long.valueOf(o2.getTime()).compareTo(Long.valueOf(o1.getTime()));
            return flag;
        }
    }


}
