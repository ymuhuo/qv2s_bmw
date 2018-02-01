package com.bmw.peek2.presenter.impl;

import android.content.Context;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.Constant;
import com.bmw.peek2.R;
import com.bmw.peek2.presenter.VideoPlayerPresenter;
import com.bmw.peek2.utils.FileComparator;
import com.bmw.peek2.utils.FileUtil;
import com.bmw.peek2.utils.LogUtil;
import com.bmw.peek2.utils.UrlUtil;
import com.bmw.peek2.view.adapter.FileListAdapter;
import com.bmw.peek2.presenter.FilePresenter;
import com.bmw.peek2.model.Login_info;
import com.bmw.peek2.view.fragments.DialogNormalFragment;
import com.bmw.peek2.view.fragments.OnDialogFragmentClickListener;
import com.bmw.peek2.view.ui.FileShowNewActivity;
import com.bmw.peek2.view.viewImpl.FileViewImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.bmw.peek2.utils.FragmentUtil.showDialogFragment;
import static com.bmw.peek2.utils.LogUtil.error;

/**
 * Created by admin on 2016/9/8.
 */
public class FilePresenterImpl implements FilePresenter {

    private FileListAdapter adapter;
    private boolean isPicture;
    private FileViewImpl fileViewImpl;
    //    private boolean isChooseDeleteMode;
    private String mPath;
    private Context context;
    private VideoPlayerPresenter videoPlayerPresenter;

    public FilePresenterImpl(FileListAdapter adapter, boolean isPicture, FileViewImpl view, Context context, VideoPlayerPresenter videoPlayerPresenter) {
        this.isPicture = isPicture;
        this.adapter = adapter;
        this.fileViewImpl = view;
        this.context = context;
        this.videoPlayerPresenter = videoPlayerPresenter;
//        initAdapter();
    }

    public boolean isGetShowFile() {
        return mPath == null ? false : true;
    }

    @Override
    public void setEditModel(boolean isEdit) {
        adapter.setChoose(isEdit);
    }

    public void copyFile(final String targetPath) {
        final List<File> files = adapter.getfiles();
        final List<Integer> chooseFile = adapter.getDeleteChooseList();
        if (chooseFile == null || files == null)
            return;
        if (chooseFile.size() == 0) {
            BaseApplication.toast(context.getString(R.string.choose_file));
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                fileViewImpl.startShowCopy(chooseFile.size());
                for (int i = 0; i < chooseFile.size(); i++) {
                    fileViewImpl.copyWhichFile(i);
                    String fileName = files.get(chooseFile.get(i)).getName();
                    File fileNew = new File(targetPath, fileName);
                    if (!fileNew.exists()) {
                        try {
                            fileNew.createNewFile();
                        } catch (IOException e) {
                            error("create file error: " + e.toString());
                            e.printStackTrace();
                        }
                    }
                    try {
                        FileInputStream fIs = new FileInputStream(files.get(chooseFile.get(i)));
                        FileOutputStream fouts = new FileOutputStream(fileNew);
                        byte[] bufs = new byte[1024];
                        int len = 0;
                        int byteSum = 0;
                        while ((len = fIs.read(bufs)) != -1) {
                            byteSum += len;
                            fouts.write(bufs, 0, len);
                        }
                        fIs.close();
                        fouts.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                fileViewImpl.copyFinish();
            }
        }).start();


    }

    @Override
    public void initAdapter() {
        adapter.setFiles(initData());
        adapter.setOnClickChangeListener(new FileListAdapter.OnClickChangeListener() {
            @Override
            public void Click(String path) {

//                if (isChooseDeleteMode)
//                    isChooseDeleteMode = false;
                if (path == null) {
                    if (fileViewImpl != null && mPath != null && isPicture)
                        fileViewImpl.setEmptyPicture();
                    mPath = null;
                    if (!isPicture)
                        setVideoPresentPath();
                    return;
                }
                if (path.equals(mPath)) {
                    fileViewImpl.isBtnDeleteShow(true);
                    return;
                }


                mPath = path;
                fileViewImpl.isBtnDeleteShow(true);
                if (isPicture) {
                    fileViewImpl.pictureClick(path);
                } else {
                    setVideoPresentPath();
                    videoPlayerPresenter.startPlayVideo();
                }


            }

            @Override
            public void longClick(int position) {
                fileViewImpl.isBtnDeleteShow(true);
                if (videoPlayerPresenter != null)
                    videoPlayerPresenter.playStop();
//                isChooseDeleteMode = true;
                adapter.setChoose(true);
                adapter.addDeleteChooseList(position);
                if (fileViewImpl != null)
                    fileViewImpl.setEditModel(true);
            }

            @Override
            public void cancelLongClick() {
//                isChooseDeleteMode = false;
                fileViewImpl.isBtnDeleteShow(false);
                if (fileViewImpl != null)
                    fileViewImpl.setEditModel(true);
                if (fileViewImpl != null)
                    fileViewImpl.setEditModel(false);
            }

            public void chooseNull() {
                if (fileViewImpl != null)
                    fileViewImpl.setAllChooseBtn(false);
            }

            public void chooseAll() {
                if (fileViewImpl != null)
                    fileViewImpl.setAllChooseBtn(true);

            }
        });
    }


    public void setVideoPresentPath() {
        if (videoPlayerPresenter != null) {
            videoPlayerPresenter.setPlayPath(mPath);
            videoPlayerPresenter.playStop();
            videoPlayerPresenter.setVideoViewAlpha0();
        }
    }

    private List<File> initData() { //从指定文件夹获取文件列表

        List<File> files = null;
        if (isPicture) {
            files = UrlUtil.getFileUtils(FileUtil.getFileSavePath() + Login_info.local_picture_path);
            List<File> picFiles = new ArrayList<>();
            if (files != null) {
                for (int i = 0; i < files.size(); i++) {
                    String name = files.get(i).getName();
                    name = name.substring(name.lastIndexOf("."), name.length());
                    if (name.equals(".jpg")) {
                        picFiles.add(files.get(i));
                    } else {
                        boolean isDelete = true;
                        for (File f : files) {
                            String fName = files.get(i).getName();
                            fName = fName.substring(fName.lastIndexOf("/") + 1, fName.lastIndexOf("."));
                            String tName = f.getName();
                            String tNameF = tName.substring(tName.lastIndexOf("/") + 1, tName.lastIndexOf("."));
                            String tNameE = tName.substring(tName.lastIndexOf("."), tName.length());
                            if (tNameF.equals(fName) && tNameE.equals(".jpg")) {
                                isDelete = false;
                            }
                        }
                        if (isDelete) {
                            files.get(i).delete();
                            FileUtil.updateSystemLibFile(files.get(i).getAbsolutePath());
                            files.remove(i);
                        }
                    }
                }
                files = picFiles;
            }
        } else {
            files = UrlUtil.getFileUtils(FileUtil.getFileSavePath() + Login_info.local_video_path);
            List<File> videoFiles = new ArrayList<>();
            if (files != null) {
                for (int i = 0; i < files.size(); i++) {
                    String name = files.get(i).getName();
                    name = name.substring(name.lastIndexOf("."), name.length());
                    if (name.equals(".avi") || name.equals(".AVI") || name.equals(".mp4") || name.equals("MP4")) {
                        videoFiles.add(files.get(i));
                    } else {
                        boolean isDelete = true;
                        for (File f : files) {
                            String fName = files.get(i).getName();
                            fName = fName.substring(fName.lastIndexOf("/") + 1, fName.lastIndexOf("."));
                            String tName = f.getName();
                            String tNameF = tName.substring(tName.lastIndexOf("/") + 1, tName.lastIndexOf("."));
                            String tNameE = tName.substring(tName.lastIndexOf("."), tName.length());
                            if (tNameF.equals(fName) && !tNameE.equals(".xml")) {
                                isDelete = false;
                            }
                        }
                        if (isDelete) {
                            files.get(i).delete();
                            FileUtil.updateSystemLibFile(files.get(i).getAbsolutePath());
                            files.remove(i);
                        }
                    }
                }
                files = videoFiles;
            }
        }
        if (files != null) {
            FileComparator fileComparator = new FileComparator();
            Collections.sort(files, fileComparator);
        }

        if (isPicture) {
            if (files != null)
                for (int i = 0; i < files.size(); i++) {
                    String fileName = files.get(i).getName();
                    int indexOf_ = fileName.indexOf("-");
                    int count = BaseApplication.getSharedPreferences().getInt(Constant.KEY_PICTURE_FILE_COUNT, 1);
                    if (indexOf_ >= 1 && indexOf_ <= 5) {
                        String countStr = fileName.substring(0, indexOf_);
                        try {
                            count = Integer.valueOf(countStr);
                        } catch (Exception e) {
                            continue;
                        }
                        count++;
                        BaseApplication.getSharedPreferences().edit().putInt(Constant.KEY_PICTURE_FILE_COUNT, count).commit();
                        break;
                    } else {
                        continue;
                    }
                }
        } else {
            if (files != null)
                for (int i = 0; i < files.size(); i++) {
                    String fileName = files.get(i).getName();
                    int indexOf_ = fileName.indexOf("-");
                    int count = BaseApplication.getSharedPreferences().getInt(Constant.KEY_VIDEO_FILE_COUNT, 1);
                    if (indexOf_ >= 1 && indexOf_ <= 5) {
                        String countStr = fileName.substring(0, indexOf_);
                        try {
                            count = Integer.valueOf(countStr);
                        } catch (Exception e) {
                            continue;
                        }
                        count++;
                        BaseApplication.getSharedPreferences().edit().putInt(Constant.KEY_VIDEO_FILE_COUNT, count).commit();
                        break;
                    } else {
                        continue;
                    }
                }
        }

        return files;

    }

    @Override
    public void searching(String msg) {

        List<File> list = initData();
        if (msg.equals("")) {
//            fileViewImpl.mToast("请先输入搜索内容！");
            adapter.setFiles(list);
            return;
        }
        List<File> searFiles = new ArrayList<>();
        if (list != null)
            for (File f : list) {
                String name = f.getName();
                if (name.contains(msg)) {
                    searFiles.add(f);
                }
            }
        adapter.setFiles(searFiles);
    }

    @Override
    public void deleteFile() {
        deleteAllChooseFile();
    }

    @Override
    public int lastItem() {
        if (mPath != null)
            return adapter.lastItem();
        return -1;
    }

    @Override
    public int nextItem() {
        if (mPath != null)
            return adapter.nextItem();
        return -1;
    }


    private void deleteAllChooseFile() {
        if (adapter.getDeleteChooseList().size() == 0) {
            return;
        }
        DialogNormalFragment dialogNormalFragment = DialogNormalFragment.getInstance(
                context.getResources().getString(R.string.isDeleteAllFile),
                context.getResources().getString(R.string.isdeleteChooseFile),
                context.getResources().getString(R.string.yes),
                context.getResources().getString(R.string.no), true);
        dialogNormalFragment.setOnDialogFragmentClickListener(new OnDialogFragmentClickListener() {
            @Override
            public void sure() {
                adapter.deleteFile();
//                adapter.setChoose(false);
                mPath = null;
                setVideoPresentPath();
                initAdapter();
//                isChooseDeleteMode = false;
                fileViewImpl.setEmptyPicture();
            }

            @Override
            public void cancel() {

            }
        });
        showDialogFragment(((FileShowNewActivity) context).getSupportFragmentManager(), dialogNormalFragment, "DialogNormalFragment");

    }


    private void deleteClickChooseFile() {
        if (mPath != null) {
            String pathName = mPath.substring(mPath.lastIndexOf("/") + 1, mPath.length());
            if (videoPlayerPresenter != null && videoPlayerPresenter.isPlaying())
                videoPlayerPresenter.playStart();
            DialogNormalFragment dialogNormalFragment = DialogNormalFragment.getInstance(context.getResources().getString(R.string.isdelete),
                    context.getResources().getString(R.string.isdeleteFile) + pathName,
                    context.getResources().getString(R.string.yes),
                    context.getResources().getString(R.string.no), true);
            dialogNormalFragment.setOnDialogFragmentClickListener(new OnDialogFragmentClickListener() {
                @Override
                public void sure() {
                    if (videoPlayerPresenter != null)
                        videoPlayerPresenter.playStop();
                    adapter.deleteOneChoose();
                    deleteXmlFile();
//                            filePresenter.initAdapter();
                }

                @Override
                public void cancel() {
                    if (videoPlayerPresenter != null && videoPlayerPresenter.isStartPlay() && !videoPlayerPresenter.isPlaying())
                        videoPlayerPresenter.playStart();
                }
            });
            showDialogFragment(((FileShowNewActivity) context).getSupportFragmentManager(), dialogNormalFragment, "DialogNormalFragment");

            pathName = null;
        }
    }

    private void deleteXmlFile() {
        if (isPicture && mPath != null) {
            String pathName = mPath.substring(0, mPath.lastIndexOf("."));
            File xmlFile = new File(pathName + ".xml");
            if (xmlFile.exists())
                xmlFile.delete();
        }
    }

    @Override
    public void chooseAll() {
        adapter.chooseAll();
    }

    @Override
    public void cancelAll() {
        adapter.cancelAll();
//        adapter.setChoose(false);
    }


}
