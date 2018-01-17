package com.bmw.peek2.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bmw.peek2.R;

import java.io.File;
import java.util.List;

/**
 * Created by admin on 2017/10/17.
 */

public class KanbanAdapter extends RecyclerView.Adapter<KanbanAdapter.ViewHolder> {

    private Context mContext;
    private List<File> files;
    private int mOneChoose = -1;

    public KanbanAdapter(Context mContext, List<File> files) {
        this.mContext = mContext;
        this.files = files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public void deleteChooseItem() {
        if (mOneChoose == -1)
            return;
        String mKanbanPath = files.get(mOneChoose).getAbsolutePath();
        File file = new File(mKanbanPath);
        if (file.exists())
            file.delete();
        File file_index = new File(mKanbanPath + "_index");
        if (file_index.exists())
            file_index.delete();
        File file_avi = new File(mKanbanPath + ".avi");
        if (file_avi.exists())
            file_avi.delete();
        files.remove(mOneChoose);
        mOneChoose = -1;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.kanban_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (files != null && files.size() > 0) {
            if (mOneChoose == position) {
                holder.bg.setBackgroundColor(mContext.getResources().getColor(R.color.btn_press));
            } else {
                holder.bg.setBackgroundColor(mContext.getResources().getColor(R.color.nothing));
            }
        }
        holder.imageView.setImageResource(R.mipmap.video);
        holder.textView.setText(files.get(position).getName());

        holder.bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOneChoose = position;
                if (listener != null)
                    listener.choose(files.get(position).getAbsolutePath());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private LinearLayout bg;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_img);
            textView = (TextView) itemView.findViewById(R.id.item_text);
            bg = (LinearLayout) itemView.findViewById(R.id.item);

        }
    }

    public interface OnKanbanItemChooseListener {
        void choose(String kanbanPath);
    }

    private OnKanbanItemChooseListener listener;

    public void setListener(OnKanbanItemChooseListener listener) {
        this.listener = listener;
    }
}
