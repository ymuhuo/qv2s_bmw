package com.bmw.peek2.utils;

import android.util.Xml;

import com.bmw.peek2.model.VideoInfo;
import com.bmw.peek2.model.CapturePicture;
import com.bmw.peek2.model.PipeDefectDetail;
import com.bmw.peek2.model.PipeDefectImage;
import com.bmw.peek2.model.QueXianInfo;
import com.bmw.peek2.model.QueXian_GradeInfo;
import com.bmw.peek2.model.QueXian_StyleInfo;
import com.bmw.peek2.model.RecordTaskInfo;
import com.hikvision.netsdk.NET_DVR_TIME;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/3/6.
 */

public class PullXmlParseUtil {

    public static final String PipeSection = "PipeSection";
    public static final String Distance = "Distance";
    public static final String DefectType = "DefectType";
    public static final String DefectCode = "DefectCode";
    public static final String DefectLevel = "DefectLevel";
    public static final String ClockExpression = "ClockExpression";
    public static final String DefectLength = "DefectLength";
    public static final String DefectDescription = "DefectDescription";

    public static List<QueXianInfo> parseQueXianXml(InputStream inputStream) {

        if (inputStream == null)
            return null;
        XmlPullParser xmlPullParser = Xml.newPullParser();
        List<QueXianInfo> list = null;
        List<QueXian_StyleInfo> styleList = null;
        List<QueXian_GradeInfo> gradeList = null;
        QueXianInfo queXianInfo = null;
        QueXian_StyleInfo queXian_styleInfo = null;
        QueXian_GradeInfo queXian_gradeInfo = null;


        try {
            xmlPullParser.setInput(inputStream, "utf-8");

            int type = xmlPullParser.getEventType();

            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if ("ArrayOfDefectType".equals(xmlPullParser.getName())) {
                            list = new ArrayList<>();
                        } else if ("DefectType".equals(xmlPullParser.getName())) {
                            queXianInfo = new QueXianInfo();
                            styleList = new ArrayList<>();
                        } else if ("Index".equals(xmlPullParser.getName())) {
                            if (queXian_styleInfo == null)
                                queXianInfo.setIndex(Integer.parseInt(xmlPullParser.nextText()));
                            else
                                queXian_styleInfo.setIndex(Integer.parseInt(xmlPullParser.nextText()));
                        } else if ("Name".equals(xmlPullParser.getName())) {
                            if (queXian_styleInfo == null)
                                queXianInfo.setName(xmlPullParser.nextText());
                            else
                                queXian_styleInfo.setName(xmlPullParser.nextText());
                        } else if ("Code".equals(xmlPullParser.getName())) {
                            if (queXian_styleInfo == null)
                                queXianInfo.setCode(xmlPullParser.nextText());
                            else
                                queXian_styleInfo.setCode(xmlPullParser.nextText());
                        } else if ("Defect".equals(xmlPullParser.getName())) {
                            queXian_styleInfo = new QueXian_StyleInfo();
                            gradeList = new ArrayList<>();
                        } else if ("DefectDescribe".equals(xmlPullParser.getName())) {
                            queXian_gradeInfo = new QueXian_GradeInfo();
                        } else if ("Level".equals(xmlPullParser.getName())) {
                            queXian_gradeInfo.setLevel(Integer.parseInt(xmlPullParser.nextText()));
                        } else if ("Content".equals(xmlPullParser.getName())) {
                            queXian_gradeInfo.setContent(xmlPullParser.nextText());
                        } else if ("Define".equals(xmlPullParser.getName())) {
                            queXian_styleInfo.setDefine(xmlPullParser.nextText());
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        if ("DefectType".equals(xmlPullParser.getName())) {
                            queXianInfo.setStyleList(styleList);
                            list.add(queXianInfo);
                            queXianInfo = null;
                            styleList = null;
                        } else if ("DefectDescribe".equals(xmlPullParser.getName())) {
                            gradeList.add(queXian_gradeInfo);
                            queXian_gradeInfo = null;
                        } else if ("Defect".equals(xmlPullParser.getName())) {
                            queXian_styleInfo.setGradeList(gradeList);
                            styleList.add(queXian_styleInfo);
                            queXian_styleInfo = null;
                            gradeList = null;
                        }


                        break;
                    default:
                        break;
                }
                type = xmlPullParser.next();
            }


        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (inputStream != null)
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        return list;
    }

    public static void writeVideoRecordXml(VideoInfo videoInfo) {
        if (videoInfo == null || StringUtils.isStringEmpty(videoInfo.getVideoFilename()))
            return;
        String path = FileUtil.replacePostFix(videoInfo.getVideoFilename(), ".xml");

        XmlSerializer serializer = Xml.newSerializer();
        try {
            FileOutputStream outputStream = new FileOutputStream(path);
            serializer.setOutput(outputStream, "utf-8");

            serializer.startDocument("UTF-8", null);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            serializer.setPrefix("xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.setPrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.startTag("", "VideoInfo");

            RecordTaskInfo recordTaskInfo = videoInfo.getRecordTaskInfo();
            if (recordTaskInfo != null) {
                serializer.startTag(null, "Headline");
                if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_name())) {
                    serializer.startTag(null, "TaskName");
                    serializer.text(recordTaskInfo.getTask_name());
                    serializer.endTag(null, "TaskName");
                }
                if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_place())) {
                    serializer.startTag(null, "InspectAddress");
                    serializer.text(recordTaskInfo.getTask_place());
                    serializer.endTag(null, "InspectAddress");
                }
                if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_id())) {
                    serializer.startTag(null, "TaskID");
                    serializer.text(recordTaskInfo.getTask_id());
                    serializer.endTag(null, "TaskID");
                }

                NET_DVR_TIME net_dvr_time = HkUtils.getTime();
                if (net_dvr_time != null) {
                    StringBuilder timeBuilder = new StringBuilder();
                    timeBuilder.append(net_dvr_time.dwYear).append("年")
                            .append(net_dvr_time.dwMonth).append("月")
                            .append(net_dvr_time.dwDay).append("日");

                    serializer.startTag(null, "InspectDate");
                    serializer.text(timeBuilder.toString());
                    serializer.endTag(null, "InspectDate");
                }

                if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_start())) {
                    serializer.startTag(null, "StartWellNum");
                    serializer.text(recordTaskInfo.getTask_start());
                    serializer.endTag(null, "StartWellNum");
                }
                if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_end())) {
                    serializer.startTag(null, "EndWellNum");
                    serializer.text(recordTaskInfo.getTask_end());
                    serializer.endTag(null, "EndWellNum");
                }
                if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_direction())) {
                    serializer.startTag(null, "InspectDirection");
                    serializer.text(recordTaskInfo.getTask_direction());
                    serializer.endTag(null, "InspectDirection");
                }

                if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_sort())) {
                    serializer.startTag(null, "PipeType");
                    serializer.text(recordTaskInfo.getTask_sort());
                    serializer.endTag(null, "PipeType");
                }
                if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_guancai())) {
                    serializer.startTag(null, "PipeStock");
                    serializer.text(recordTaskInfo.getTask_guancai());
                    serializer.endTag(null, "PipeStock");
                }
                if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_diameter())) {
                    serializer.startTag(null, "PipeDiameter");
                    serializer.text(recordTaskInfo.getTask_diameter());
                    serializer.endTag(null, "PipeDiameter");
                }
                if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_computer())) {
                    serializer.startTag(null, "InspectUnit");
                    serializer.text(recordTaskInfo.getTask_computer());
                    serializer.endTag(null, "InspectUnit");
                }
                if (!StringUtils.isStringEmpty(recordTaskInfo.getTask_people())) {
                    serializer.startTag(null, "Inspector");
                    serializer.text(recordTaskInfo.getTask_people());
                    serializer.endTag(null, "Inspector");
                }

                serializer.endTag(null, "Headline");
            }

            if (!StringUtils.isStringEmpty(videoInfo.getDeviceModel())) {
                serializer.startTag(null, "DeviceModel");
                serializer.text(videoInfo.getDeviceModel());
                serializer.endTag(null, "DeviceModel");
            }
            if (!StringUtils.isStringEmpty(videoInfo.getInspectionStandard())) {
                serializer.startTag(null, "InspectionStandard");
                serializer.text(videoInfo.getInspectionStandard());
                serializer.endTag(null, "InspectionStandard");
            }
            if (!StringUtils.isStringEmpty(videoInfo.getVideoFilename())) {
                serializer.startTag(null, "VideoFilename");
                serializer.text(videoInfo.getVideoFilename());
                serializer.endTag(null, "VideoFilename");
            }
            if (!StringUtils.isStringEmpty(videoInfo.getCapturePictureDirectoryName())) {
                serializer.startTag(null, "CapturePictureDirectoryName");
                serializer.text(videoInfo.getCapturePictureDirectoryName());
                serializer.endTag(null, "CapturePictureDirectoryName");
            }
            ArrayList<CapturePicture> capturePictures = videoInfo.getCapturePictures();
            for (int i = 0; i < capturePictures.size(); i++) {
                serializer.startTag(null, "CapturePicture");
                CapturePicture capturePicture = capturePictures.get(i);
                if (!StringUtils.isStringEmpty(capturePicture.getFilename())) {
                    serializer.startTag(null, "Filename");
                    serializer.text(capturePicture.getFilename());
                    serializer.endTag(null, "Filename");
                }
                if (!StringUtils.isStringEmpty(capturePicture.getDefectFilename())) {
                    serializer.startTag(null, "DefectFilename");
                    serializer.text(capturePicture.getDefectFilename());
                    serializer.endTag(null, "DefectFilename");
                }
                if (!StringUtils.isStringEmpty(capturePicture.getTimestamp())) {
                    serializer.startTag(null, "Timestamp");
                    serializer.text(capturePicture.getTimestamp());
                    serializer.endTag(null, "Timestamp");
                }
                if (!StringUtils.isStringEmpty(capturePicture.getPipedefectCode())) {
                    serializer.startTag(null, "PipedefectCount");
                    serializer.text(capturePicture.getPipedefectCode());
                    serializer.endTag(null, "PipedefectCount");
                }

                serializer.endTag(null, "CapturePicture");
            }
            serializer.endTag("", "VideoInfo");
            serializer.endDocument();

            serializer.flush();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static VideoInfo getVideoRecordXml(String path) {

        XmlPullParser pullParser = Xml.newPullParser();
        VideoInfo videoInfo = null;
        RecordTaskInfo recordTaskInfo = null;
        CapturePicture capturePicture = null;

        try {
            InputStream inputStream = new FileInputStream(path);
            pullParser.setInput(inputStream, "utf-8");
            int type = pullParser.getEventType();

            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if (pullParser.getName().equals("VideoInfo")) {
                            videoInfo = new VideoInfo();
                        } else if (pullParser.getName().equals("Headline")) {
                            recordTaskInfo = new RecordTaskInfo();
                        } else if (pullParser.getName().equals("TaskName")) {
                            recordTaskInfo.setTask_name(pullParser.nextText());
                        } else if (pullParser.getName().equals("InspectAddress")) {
                            recordTaskInfo.setTask_place(pullParser.nextText());
                        } else if (pullParser.getName().equals("TaskID")) {
                            recordTaskInfo.setTask_id(pullParser.nextText());
                        } else if (pullParser.getName().equals("InspectDate")) {
                            recordTaskInfo.setTask_date(pullParser.nextText());
                        } else if (pullParser.getName().equals("StartWellNum")) {
                            recordTaskInfo.setTask_start(pullParser.nextText());
                        } else if (pullParser.getName().equals("EndWellNum")) {
                            recordTaskInfo.setTask_end(pullParser.nextText());
                        } else if (pullParser.getName().equals("InspectDirection")) {
                            recordTaskInfo.setTask_direction(pullParser.nextText());
                        } else if (pullParser.getName().equals("PipeType")) {
                            recordTaskInfo.setTask_sort(pullParser.nextText());
                        } else if (pullParser.getName().equals("PipeStock")) {
                            recordTaskInfo.setTask_guancai(pullParser.nextText());
                        } else if (pullParser.getName().equals("PipeDiameter")) {
                            recordTaskInfo.setTask_diameter(pullParser.nextText());
                        } else if (pullParser.getName().equals("InspectUnit")) {
                            recordTaskInfo.setTask_computer(pullParser.nextText());
                        } else if (pullParser.getName().equals("Inspector")) {
                            recordTaskInfo.setTask_people(pullParser.nextText());
                        } else if (pullParser.getName().equals("VideoFilename")) {
                            videoInfo.setVideoFilename(pullParser.nextText());
                        } else if (pullParser.getName().equals("CapturePicture")) {
                            capturePicture = new CapturePicture();
                        } else if (pullParser.getName().equals("Filename")) {
                            capturePicture.setFilename(pullParser.nextText());
                        } else if (pullParser.getName().equals("DefectFilename")) {
                            capturePicture.setDefectFilename(pullParser.nextText());
                        } else if (pullParser.getName().equals("Timestamp")) {
                            capturePicture.setTimestamp(pullParser.nextText());
                        }else if (pullParser.getName().equals("PipedefectCount")) {
                            capturePicture.setPipedefectCode(pullParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (pullParser.getName().equals("Headline")) {
                            if (videoInfo != null) {
                                videoInfo.setRecordTaskInfo(recordTaskInfo);
                                recordTaskInfo = null;
                            }
                        } else if (pullParser.getName().equals("CapturePicture")) {
                            if (capturePicture != null && videoInfo != null) {
                                videoInfo.addCapturePic(capturePicture);
                                capturePicture = null;
                            }
                        }
                        break;
                    default:
                        break;
                }
                type = pullParser.next();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return videoInfo;
    }



    public static void writePicQueXianXml(PipeDefectImage pipeDefectImage) {
        if (pipeDefectImage == null || StringUtils.isStringEmpty(pipeDefectImage.getFilename()))
            return;
        String path = FileUtil.replacePostFix(pipeDefectImage.getFilename(), ".xml");

        XmlSerializer serializer = Xml.newSerializer();
        try {
            FileOutputStream outputStream = new FileOutputStream(path);
            serializer.setOutput(outputStream, "utf-8");

            serializer.startDocument("UTF-8", null);
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            serializer.setPrefix("xsd", "http://www.w3.org/2001/XMLSchema");
            serializer.setPrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.startTag("", "PipeDefectImage");


            if (!StringUtils.isStringEmpty(pipeDefectImage.getPipeSection())) {
                serializer.startTag(null, "PipeSection");
                serializer.text(pipeDefectImage.getPipeSection());
                serializer.endTag(null, "PipeSection");
            }
            ArrayList<PipeDefectDetail> pipeDefectImages = pipeDefectImage.getPipeDefectDetails();
            for (int i = 0; i < pipeDefectImages.size(); i++) {
                serializer.startTag(null, "Defect");
                PipeDefectDetail pipeDefectDetail = pipeDefectImages.get(i);

                if (!StringUtils.isStringEmpty(pipeDefectDetail.getDistance())) {
                    serializer.startTag(null, "Distance");
                    serializer.text(pipeDefectDetail.getDistance());
                    serializer.endTag(null, "Distance");
                }
                if (!StringUtils.isStringEmpty(pipeDefectDetail.getDefectType())) {
                    serializer.startTag(null, "DefectType");
                    serializer.text(pipeDefectDetail.getDefectType());
                    serializer.endTag(null, "DefectType");
                }
                if (!StringUtils.isStringEmpty(pipeDefectDetail.getDefectCode())) {
                    serializer.startTag(null, "DefectCode");
                    serializer.text(pipeDefectDetail.getDefectCode());
                    serializer.endTag(null, "DefectCode");
                }
                if (!StringUtils.isStringEmpty(pipeDefectDetail.getDefectLevel())) {
                    serializer.startTag(null, "DefectLevel");
                    serializer.text(pipeDefectDetail.getDefectLevel());
                    serializer.endTag(null, "DefectLevel");
                }

                if (!StringUtils.isStringEmpty(pipeDefectDetail.getClockExpression())) {
                    serializer.startTag(null, "ClockExpression");
                    serializer.text(pipeDefectDetail.getClockExpression());
                    serializer.endTag(null, "ClockExpression");
                }

                if (!StringUtils.isStringEmpty(pipeDefectDetail.getDefectLength())) {
                    serializer.startTag(null, "DefectLength");
                    serializer.text(pipeDefectDetail.getDefectLength());
                    serializer.endTag(null, "DefectLength");
                }

                if (!StringUtils.isStringEmpty(pipeDefectDetail.getDefectDescription())) {
                    serializer.startTag(null, "DefectDescription");
                    serializer.text(pipeDefectDetail.getDefectDescription());
                    serializer.endTag(null, "DefectDescription");
                }

                serializer.endTag(null, "Defect");
            }
            if (!StringUtils.isStringEmpty(pipeDefectImage.getFilename())) {
                serializer.startTag(null, "Filename");
                serializer.text(pipeDefectImage.getFilename());
                serializer.endTag(null, "Filename");
            }
            serializer.endTag("", "PipeDefectImage");
            serializer.endDocument();

            serializer.flush();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtil.updateSystemLibFile(path);
    }

    public static PipeDefectImage getPicQueXianXml(String path) {

        XmlPullParser pullParser = Xml.newPullParser();
        PipeDefectImage pipeDefectImage = null;
        PipeDefectDetail pipeDefectDetail = null;

        try {
            InputStream inputStream = new FileInputStream(path);
            pullParser.setInput(inputStream, "utf-8");
            int type = pullParser.getEventType();

            while (type != XmlPullParser.END_DOCUMENT) {
                switch (type) {
                    case XmlPullParser.START_TAG:
                        if (pullParser.getName().equals("PipeDefectImage")) {
                            pipeDefectImage = new PipeDefectImage();
                        } else if (pullParser.getName().equals("PipeSection")) {
                            pipeDefectImage.setPipeSection(pullParser.nextText());
                        } else if (pullParser.getName().equals("Defect")) {
                            pipeDefectDetail = new PipeDefectDetail();
                        } else if (pullParser.getName().equals("Distance")) {
                           pipeDefectDetail.setDistance(pullParser.nextText());
                        } else if (pullParser.getName().equals("DefectType")) {
                           pipeDefectDetail.setDefectType(pullParser.nextText());
                        } else if (pullParser.getName().equals("DefectCode")) {
                            pipeDefectDetail.setDefectCode(pullParser.nextText());
                        } else if (pullParser.getName().equals("DefectLevel")) {
                            pipeDefectDetail.setDefectLevel(pullParser.nextText());
                        } else if (pullParser.getName().equals("ClockExpression")) {
                            pipeDefectDetail.setClockExpression(pullParser.nextText());
                        } else if (pullParser.getName().equals("DefectLength")) {
                           pipeDefectDetail.setDefectLength(pullParser.nextText());
                        } else if (pullParser.getName().equals("DefectDescription")) {
                            pipeDefectDetail.setDefectDescription(pullParser.nextText());
                        } else if (pullParser.getName().equals("Filename")) {
                           pipeDefectImage.setFilename(pullParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (pullParser.getName().equals("Defect")) {
                            if (pipeDefectImage != null) {
                                pipeDefectImage.addDefectDetail(pipeDefectDetail);
                                pipeDefectDetail = null;
                            }
                        }
                        break;
                    default:
                        break;
                }
                type = pullParser.next();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return pipeDefectImage;
    }


}
