package com.bmw.peek2.model;

/**
 * Created by admin on 2018/1/29.
 */

public class CapturePicture {
    private String Filename;
    private String DefectFilename;
    private String Timestamp;
    private String PipedefectCode;

    public CapturePicture() {
    }

    public CapturePicture(String filename, String defectFilename, String timestamp) {
        Filename = filename;
        DefectFilename = defectFilename;
        Timestamp = timestamp;
    }

    public String getPipedefectCode() {
        return PipedefectCode;
    }

    public void setPipedefectCode(String pipedefectCode) {
        PipedefectCode = pipedefectCode;
    }

    public String getFilename() {
        return Filename;
    }

    public void setFilename(String filename) {
        Filename = filename;
    }

    public String getDefectFilename() {
        return DefectFilename;
    }

    public void setDefectFilename(String defectFilename) {
        DefectFilename = defectFilename;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "CapturePicture{" +
                "Filename='" + Filename + '\'' +
                ", DefectFilename='" + DefectFilename + '\'' +
                ", Timestamp='" + Timestamp + '\'' +
                '}';
    }
}
