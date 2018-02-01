package com.bmw.peek2.model;

import com.bmw.peek2.model.CapturePicture;
import com.bmw.peek2.model.RecordTaskInfo;

import java.util.ArrayList;

/**
 * Created by admin on 2018/1/29.
 */

public class VideoInfo {
    private RecordTaskInfo recordTaskInfo;
    private String DeviceModel = "Peek2S";
    private String InspectionStandard = "GJBB";
    private String VideoFilename;
    private String CapturePictureDirectoryName = "capture";
    private ArrayList<CapturePicture> capturePictures;




    public VideoInfo() {
        capturePictures = new ArrayList<>();
    }

    public VideoInfo(RecordTaskInfo recordTaskInfo, String videoFilename, ArrayList<CapturePicture> capturePictures) {

        this.recordTaskInfo = recordTaskInfo;
        VideoFilename = videoFilename;
        this.capturePictures = capturePictures;
    }


    public void setDeviceModel(String deviceModel) {
        DeviceModel = deviceModel;
    }

    public void setInspectionStandard(String inspectionStandard) {
        InspectionStandard = inspectionStandard;
    }

    public void setCapturePictureDirectoryName(String capturePictureDirectoryName) {
        CapturePictureDirectoryName = capturePictureDirectoryName;
    }

    public String getDeviceModel() {
        return DeviceModel;
    }

    public String getInspectionStandard() {
        return InspectionStandard;
    }

    public String getCapturePictureDirectoryName() {
        return CapturePictureDirectoryName;
    }

    public RecordTaskInfo getRecordTaskInfo() {
        return recordTaskInfo;
    }

    public void setRecordTaskInfo(RecordTaskInfo recordTaskInfo) {
        this.recordTaskInfo = recordTaskInfo;
    }

    public String getVideoFilename() {
        return VideoFilename;
    }

    public void setVideoFilename(String videoFilename) {
        VideoFilename = videoFilename;
    }

    public ArrayList<CapturePicture> getCapturePictures() {
        return capturePictures;
    }

    public void setCapturePictures(ArrayList<CapturePicture> capturePictures) {
        this.capturePictures = capturePictures;
    }

    public void addCapturePic(CapturePicture capturePic) {
        capturePictures.add(capturePic);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < capturePictures.size(); i++) {
            stringBuilder.append("\n")
                    .append(capturePictures.get(i).getFilename()).append("\n")
                    .append(capturePictures.get(i).getDefectFilename()).append("\n")
                    .append(capturePictures.get(i).getPipedefectCode()).append("\n")
                    .append(capturePictures.get(i).getTimestamp()).append("\n");
        }

        return "VideoInfo{\n" +
                "recordTaskInfo=" + recordTaskInfo +
                ", DeviceModel='" + DeviceModel + '\'' +
                ", InspectionStandard='" + InspectionStandard + '\'' +
                ", VideoFilename='" + VideoFilename + '\'' +
                ", CapturePictureDirectoryName='" + CapturePictureDirectoryName + '\'' +
                ", capturePictures=" + stringBuilder.toString() +
                '}';
    }
}
