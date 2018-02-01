package com.bmw.peek2.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bmw.peek2.R;
import com.bmw.peek2.model.PipeDefectDetail;
import com.bmw.peek2.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by admin on 2018/1/31.
 */

public class PicQueXianShowInVideoAdapter extends RecyclerView.Adapter<PicQueXianShowInVideoAdapter.ViewHolder> {

    private ArrayList<PipeDefectDetail> mPipeDefectDetails;
//    private String mPipeSection;
    private Context mContext;

    public PicQueXianShowInVideoAdapter(Context context) {
        mPipeDefectDetails = new ArrayList<>();
//        mPipeSection = "";
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_pic_quexian_show_horizontal, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

//        if (position == 0) {
////            holder.tv_item_guandaoId.setText(mContext.getString(R.string.capture_guandao_id));
//            holder.tv_item_xuhao.setText(mContext.getString(R.string.capture_guandao_xuhao));
//            holder.tv_item_quexianStyle.setText(mContext.getString(R.string.capture_quexian_style));
//            holder.tv_item_quexianName.setText(mContext.getString(R.string.capture_quexian_name));
//            holder.tv_item_quexianGrade.setText(mContext.getString(R.string.capture_quexian_grade));
//            holder.tv_item_quexianDistance.setText(mContext.getString(R.string.capture_quexian_distance));
//            holder.tv_item_quexianClock.setText(mContext.getString(R.string.capture_quexian_clock));
//            holder.tv_item_quexianLength.setText(mContext.getString(R.string.capture_quexian_length));
//        } else {

        holder.tv_item_xuhao.setText(String.valueOf(position+1));
        PipeDefectDetail pipeDefectDetail = mPipeDefectDetails.get(position);
//        if (mPipeSection != null)
//            holder.tv_item_guandaoId.setText(mPipeSection);
        if (!StringUtils.isStringEmpty(pipeDefectDetail.getDefectType()))
            holder.tv_item_quexianStyle.setText(pipeDefectDetail.getDefectType());
        if (!StringUtils.isStringEmpty(pipeDefectDetail.getDefectCode()))
            holder.tv_item_quexianName.setText(pipeDefectDetail.getDefectCode());
        if (!StringUtils.isStringEmpty(pipeDefectDetail.getDefectLevel()))
            holder.tv_item_quexianGrade.setText(pipeDefectDetail.getDefectLevel());
        if (!StringUtils.isStringEmpty(pipeDefectDetail.getDistance()))
            holder.tv_item_quexianDistance.setText(pipeDefectDetail.getDistance());
        if (!StringUtils.isStringEmpty(pipeDefectDetail.getClockExpression()))
            holder.tv_item_quexianClock.setText(pipeDefectDetail.getClockExpression());
        if (!StringUtils.isStringEmpty(pipeDefectDetail.getDefectLength()))
            holder.tv_item_quexianLength.setText(pipeDefectDetail.getDefectLength());
//        }
    }

    @Override
    public int getItemCount() {
        return mPipeDefectDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_item_xuhao;
//        private TextView tv_item_guandaoId;
        private TextView tv_item_quexianStyle;
        private TextView tv_item_quexianName;
        private TextView tv_item_quexianGrade;
        private TextView tv_item_quexianDistance;
        private TextView tv_item_quexianClock;
        private TextView tv_item_quexianLength;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_item_xuhao = (TextView) itemView.findViewById(R.id.tv_item_xuhao);
//            tv_item_guandaoId = (TextView) itemView.findViewById(R.id.tv_item_guandaoId);
            tv_item_quexianStyle = (TextView) itemView.findViewById(R.id.tv_item_quexianStyle);
            tv_item_quexianName = (TextView) itemView.findViewById(R.id.tv_item_quexianName);
            tv_item_quexianGrade = (TextView) itemView.findViewById(R.id.tv_item_quexianGrade);
            tv_item_quexianDistance = (TextView) itemView.findViewById(R.id.tv_item_quexianDistance);
            tv_item_quexianClock = (TextView) itemView.findViewById(R.id.tv_item_quexianClock);
            tv_item_quexianLength = (TextView) itemView.findViewById(R.id.tv_item_quexianLength);
        }
    }

//
//    public String getmPipeSection() {
//        return mPipeSection;
//    }
//
//    public void setmPipeSection(String mPipeSection) {
//        this.mPipeSection = mPipeSection;
//        notifyDataSetChanged();
//    }

    public ArrayList<PipeDefectDetail> getmPipeDefectDetails() {
        return mPipeDefectDetails;
    }

    public void setmPipeDefectDetails(ArrayList<PipeDefectDetail> mPipeDefectDetails) {
        this.mPipeDefectDetails = mPipeDefectDetails;
        notifyDataSetChanged();
    }

    public void resetAllData() {
//        mPipeSection = "";
        mPipeDefectDetails = new ArrayList<>();
        notifyDataSetChanged();
    }

}
