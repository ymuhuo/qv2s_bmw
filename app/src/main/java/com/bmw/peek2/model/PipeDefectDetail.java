package com.bmw.peek2.model;

import com.bmw.peek2.utils.StringUtils;

import java.io.Serializable;

/**
 * Created by admin on 2018/1/31.
 */

public class PipeDefectDetail implements Serializable {
    private String Distance;
    private String DefectType;
    private String DefectCode;
    private String DefectLevel;
    private String ClockExpression;
    private String DefectLength;
    private String DefectDescription;


    public PipeDefectDetail() {
    }

    public PipeDefectDetail(String distance, String defectType, String defectCode, String defectLevel, String clockExpression, String defectLength, String defectDescription) {
        Distance = distance;
        DefectType = defectType;
        DefectCode = defectCode;
        DefectLevel = defectLevel;
        ClockExpression = clockExpression;
        DefectLength = defectLength;
        DefectDescription = defectDescription;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public String getDefectType() {
        return DefectType;
    }

    public void setDefectType(String defectType) {
        DefectType = defectType;
    }

    public String getDefectCode() {
        return DefectCode;
    }

    public void setDefectCode(String defectCode) {
        DefectCode = defectCode;
    }

    public String getDefectLevel() {
        return DefectLevel;
    }

    public void setDefectLevel(String defectLevel) {
        DefectLevel = defectLevel;
    }

    public String getClockExpression() {
        return ClockExpression;
    }

    public void setClockExpression(String clockExpression) {
        ClockExpression = clockExpression;
    }

    public String getDefectLength() {
        return DefectLength;
    }

    public void setDefectLength(String defectLength) {
        DefectLength = defectLength;
    }

    public String getDefectDescription() {
        return DefectDescription;
    }

    public void setDefectDescription(String defectDescription) {
        DefectDescription = defectDescription;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!StringUtils.isStringEmpty(DefectType))
            stringBuilder.append("缺陷类型:").append(DefectType).append("\n");
        if (!StringUtils.isStringEmpty(DefectCode))
            stringBuilder.append("缺陷名:").append(DefectCode).append("\n");
        if (!StringUtils.isStringEmpty(DefectLevel))
            stringBuilder.append("等级:").append(DefectLevel).append("\n");
        if (!StringUtils.isStringEmpty(Distance))
            stringBuilder.append("距离:").append(Distance).append("m\n");
        if (!StringUtils.isStringEmpty(ClockExpression))
            stringBuilder.append("时钟表示:").append(ClockExpression).append("\n");
        if (!StringUtils.isStringEmpty(DefectLength))
            stringBuilder.append("长度:").append(DefectLength).append("m\n");

        return stringBuilder.toString();
    }
}
