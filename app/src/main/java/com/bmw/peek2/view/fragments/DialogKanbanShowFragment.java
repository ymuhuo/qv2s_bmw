package com.bmw.peek2.view.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bmw.peek2.BaseApplication;
import com.bmw.peek2.R;
import com.bmw.peek2.model.Login_info;
import com.bmw.peek2.utils.FileUtil;
import com.bmw.peek2.utils.LogUtil;
import com.bmw.peek2.view.adapter.KanbanAdapter;
import com.bmw.peek2.view.ui.KanbanPreviewActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/5/17.
 */

public class DialogKanbanShowFragment extends DialogFragment {


    @Bind(R.id.rcv_list)
    RecyclerView rcvList;
    @Bind(R.id.tv_addNewKanban)
    TextView tvAddNewKanban;
    @Bind(R.id.ll_kanbanMenu)
    LinearLayout llKanbanMenu;
    @Bind(R.id.tv_btn_addNewKanban)
    TextView tvBtnAddNewKanban;
    private View mView;
    private String mKanbanPath;


    @Override
    public void onStart() {
        super.onStart();
        WindowManager manager = (WindowManager) getActivity().
                getSystemService(Context.WINDOW_SERVICE);

        //为获取屏幕宽、高
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();  //获取对话框当前的参数值

//        p.height = (int) (dm.heightPixels);   //高度设置为屏幕的0.3
        p.width = (int) (dm.widthPixels * 0.6);    //宽度设置为全屏
        //设置生效
        getDialog().getWindow().setAttributes(p);
    }

    public static DialogKanbanShowFragment getInstance() {
        DialogKanbanShowFragment dialogNormalFragment = new DialogKanbanShowFragment();
        Bundle bundle = new Bundle();

        dialogNormalFragment.setArguments(bundle);
        return dialogNormalFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_kanban_show, null);
        ButterKnife.bind(this, mView);
        rcvList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        String kanbanPathDes = getKanbanPathDes();
        List<File> kanbanFiles = getKanbanFiles(kanbanPathDes);
        if (kanbanFiles == null)
            kanbanFiles = new ArrayList<>();
        if (kanbanFiles.size() == 0) {
            changeAddKanbanShow();
        }
        KanbanAdapter mAdapter = new KanbanAdapter(getContext(), kanbanFiles);
        rcvList.setAdapter(mAdapter);
        mAdapter.setListener(new KanbanAdapter.OnKanbanItemChooseListener() {
            @Override
            public void choose(String kanbanPath) {
                mKanbanPath = kanbanPath;
            }
        });
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(mView).create();
        dialog.setCanceledOnTouchOutside(false);
        setWindowStyle(dialog);

        return dialog;
    }

    private void changeAddKanbanShow() {
        llKanbanMenu.setVisibility(View.GONE);
        tvAddNewKanban.setVisibility(View.GONE);
        tvBtnAddNewKanban.setVisibility(View.VISIBLE);
    }

    private List<File> getKanbanFiles(String kanbanPathDes) {
        File file = new File(kanbanPathDes);
        if (!file.exists())
            file.mkdirs();
        File[] files = file.listFiles();
        List<File> fileList = new ArrayList<>();
        for (File f : files) {
            if (f.isFile() && !f.getName().endsWith("_index") && !f.getName().endsWith(".avi")) {
                if (isFileExist(files, f.getName() + "_index"))
                    fileList.add(f);
                else {
                    if (isFileExist(files, f.getName() + ".avi"))
                        new File(f.getName() + ".avi").delete();
                    f.delete();

                }
            }
        }
        if (fileList.size() == 0) {
            for (File f : files) {
                f.delete();
            }
        }

        return fileList;
    }


    private boolean isFileExist(File[] files, String fileName) {
        for (File f : files) {
            if (f.getName().equals(fileName))
                return true;
        }
        return false;
    }

    private String getKanbanPathDes() {
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtil.getFileSavePath());
        sb.append(Login_info.local_kanban_path);
        return sb.toString();
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        return super.show(transaction, tag);
    }

    private void setWindowStyle(Dialog dialog) {
        Window window = dialog.getWindow();

        window.setWindowAnimations(R.style.dialog_anim);
        WindowManager manager = (WindowManager) getActivity().
                getSystemService(Context.WINDOW_SERVICE);

        //为获取屏幕宽、高
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = window.getAttributes();  //获取对话框当前的参数值

        p.height = (int) (dm.heightPixels);   //高度设置为屏幕的0.3
        p.width = (int) (dm.widthPixels * 0.5);    //宽度设置为全屏
        //设置生效
        window.setAttributes(p);
        window.setBackgroundDrawableResource(android.R.color.transparent);//加上这句实现满屏效果
        window.setGravity(Gravity.CENTER); // 非常重要：设置对话框弹出的位置
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(mView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }


    @OnClick({R.id.normal_dialog_delete, R.id.normal_dialog_preview, R.id.normal_dialog_sure, R.id.tv_addNewKanban, R.id.img_cancel, R.id.tv_btn_addNewKanban})
    public void onClick(View view) {
        if (mKanbanPath == null && view.getId() != R.id.tv_addNewKanban && view.getId() != R.id.img_cancel && view.getId() != R.id.tv_btn_addNewKanban) {

            if (rcvList.getAdapter().getItemCount() == 0) {
                BaseApplication.toast("看板列表为空，请先录制新的看板");
            } else {
                BaseApplication.toast("请先选中要添加的看板选项！");
            }
            return;
        }
        switch (view.getId()) {
            case R.id.normal_dialog_delete:
                KanbanAdapter adapter = (KanbanAdapter) rcvList.getAdapter();
                adapter.deleteChooseItem();
                mKanbanPath = null;
               /* if(adapter.getItemCount() == 0)
                    changeAddKanbanShow();*/

                break;
            case R.id.normal_dialog_preview:
                Intent intent = new Intent(getContext(), KanbanPreviewActivity.class);
                intent.putExtra("path", mKanbanPath + ".avi");
                startActivity(intent);
                break;
            case R.id.normal_dialog_sure:
                File file = new File(mKanbanPath + "_index");
                if (!file.exists()) {
                    BaseApplication.toast("看板文件损坏！请删除后重新录制！");
                } else {
                    if (listener != null)
                        listener.result(mKanbanPath);
                    getDialog().dismiss();
                }
                break;

            case R.id.tv_btn_addNewKanban:
            case R.id.tv_addNewKanban:
                if (listener != null)
                    listener.addNewKanban();
                if (rcvList.getAdapter().getItemCount() < 5)
                    getDialog().dismiss();
                break;

            case R.id.img_cancel:
                if (listener != null)
                    listener.goback();
                getDialog().dismiss();
                break;

        }
    }


    public interface OnKanbanChooseItemListener {
        void result(String path);

        void addNewKanban();

        void goback();
    }

    private OnKanbanChooseItemListener listener;

    public void setListener(OnKanbanChooseItemListener listener) {
        this.listener = listener;
    }
}
