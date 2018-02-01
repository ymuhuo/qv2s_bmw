package com.bmw.peek2.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 2018/1/31.
 */

public class PipeDefectImage implements Serializable {
    private ArrayList<PipeDefectDetail> pipeDefectDetails;
    private String PipeSection;
    private String Filename;

    public PipeDefectImage() {
        pipeDefectDetails = new ArrayList<>();
    }

    public PipeDefectImage(ArrayList<PipeDefectDetail> pipeDefectDetails, String pipeSection, String filename) {
        this.pipeDefectDetails = pipeDefectDetails;
        PipeSection = pipeSection;
        Filename = filename;
    }

    public void addDefectDetail(PipeDefectDetail pipeDefectDetail) {
        pipeDefectDetails.add(pipeDefectDetail);
    }

    public ArrayList<PipeDefectDetail> getPipeDefectDetails() {
        return pipeDefectDetails;
    }

    public void setPipeDefectDetails(ArrayList<PipeDefectDetail> pipeDefectDetails) {
        this.pipeDefectDetails = pipeDefectDetails;
    }

    public String getPipeSection() {
        return PipeSection;
    }

    public void setPipeSection(String pipeSection) {
        PipeSection = pipeSection;
    }

    public String getFilename() {
        return Filename;
    }

    public void setFilename(String filename) {
        Filename = filename;
    }

    @Override
    public String toString() {
        StringBuilder mainBuilder = new StringBuilder();

//        mainBuilder.append("文件名:").append(Filename.substring(Filename.lastIndexOf("/") + 1, Filename.length())).append("\n\n");
        if (PipeSection != null)
            mainBuilder.append("管道号:").append(PipeSection).append("\n");
        if (pipeDefectDetails.size() > 0)
            mainBuilder.append("此处一共有").append(pipeDefectDetails.size()).append("处缺陷,详情如下:\n");
        else
            mainBuilder.append("此处无缺陷！");

        for (int i = 0; i < pipeDefectDetails.size(); i++) {
            mainBuilder.append("\n").append(i + 1).append(".").append(pipeDefectDetails.get(i).toString());
        }

        return mainBuilder.toString();
    }
}
