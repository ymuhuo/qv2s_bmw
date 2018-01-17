package com.bmw.peek2.presenter;

/**
 * Created by admin on 2017/3/13.
 */

public interface CharacterOverLapingPresenter {
    void setAlwaysShowInfo(String taskName,String taskId,String startWell,String endWell,String task_guancai,String task_diameter);
    void recordFinish();
    void setSingleSign(String[] msgs);
    void setMultSign(String[] strings);
    void setNoRecordHead();
    void setGpsAndRang(String ranging, double jingdu, double weidu);
    void setTongDaoName(String name, boolean isShowOsd);
    void setOSD();
    void release();
}
