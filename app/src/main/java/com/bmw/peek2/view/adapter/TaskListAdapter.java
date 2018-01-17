package com.bmw.peek2.view.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bmw.peek2.R;
import com.bmw.peek2.model.RecordTaskInfo;
import com.bmw.peek2.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/9/2.
 */
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private Context context;
    private List<RecordTaskInfo> list;
    private List<Boolean> isClicks;
    private int choosePosition = -1;

    public TaskListAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
        isClicks = new ArrayList<>();
    }

    public void setList(List<RecordTaskInfo> list) {
        if (list != null) {
            this.list = list;
            isClicks.clear();
            for (int i = 0; i < list.size(); i++) {
                isClicks.add(false);
            }
            notifyDataSetChanged();

        }
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_record_item, null);
        return new ViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.id.setText(list.get(position).getTask_id());
        holder.name.setText(list.get(position).getTask_name());
        holder.place.setText(list.get(position).getTask_place());
        holder.start.setText(list.get(position).getTask_start());
        holder.end.setText(list.get(position).getTask_end());
        holder.direction.setText(list.get(position).getTask_direction());
        holder.sort.setText(list.get(position).getTask_sort());
        holder.guancai.setText(list.get(position).getTask_guancai());
        holder.diameter.setText(list.get(position).getTask_diameter());
        holder.computer.setText(list.get(position).getTask_computer());
        holder.people.setText(list.get(position).getTask_people());

        if (!isClicks.get(position)) {
            holder.container.setBackgroundColor(context.getResources().getColor(R.color.nothing));
            setTextColorYellow(true, holder);
        } else {
            holder.container.setBackgroundColor(context.getResources().getColor(R.color.colorText));
            setTextColorYellow(false, holder);
        }
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setItemClickBg(position);
            }
        });

    }

    private void setTextColorYellow(boolean b, ViewHolder holder) {
        int colorId = 0;
        if (b) {
            colorId = context.getResources().getColor(R.color.colorText);
        } else {
            colorId = context.getResources().getColor(android.R.color.black);
        }
        holder.id.setTextColor(colorId);
        holder.name.setTextColor(colorId);
        holder.place.setTextColor(colorId);
        holder.start.setTextColor(colorId);
        holder.end.setTextColor(colorId);
        holder.direction.setTextColor(colorId);
        holder.sort.setTextColor(colorId);
        holder.guancai.setTextColor(colorId);
        holder.diameter.setTextColor(colorId);
        holder.computer.setTextColor(colorId);
        holder.people.setTextColor(colorId);
    }

    private void setItemClickBg(int position) {
        if (position >= isClicks.size())
            return;

        if (isClicks.get(position)) {
            isClicks.set(position, false);
            choosePosition = -1;
        } else {

            for (int i = 0; i < isClicks.size(); i++) {
                isClicks.set(i, false);
            }
            isClicks.set(position, true);
            choosePosition = position;
        }
        notifyDataSetChanged();
        if (listener != null)
            listener.setData(choosePosition);
    }


    public void setChooseByPosition(int position){
        if(position<0 || position>=list.size())
            return;
        setItemClickBg(position);
    }

    public void setChooseByTaskId(int id){
        LogUtil.log("recordHeadIdFind：comeId = ",id);
        if(id <0 )
            return;
        for(int i=0;i<list.size();i++){
            LogUtil.log("recordHeadIdFind：findId "+i+" = ",list.get(i).getId());
            if(id ==(list.get(i).getId())){
                LogUtil.log("adapter：",id);
                setItemClickBg(i);
            }
        }
    }

    private void setAllChoose(int choose) {
        if (isClicks.size() == 0)
            return;
        for (int i = 0; i < isClicks.size(); i++) {
            isClicks.set(i, false);
        }
        setItemClickBg(choose);
    }

    public void chooseFirstItem() {
        setAllChoose(0);
    }

    public void chooseEndItem() {
        setAllChoose(isClicks.size() - 1);
    }

    public void chooseLastItem() {
        if (choosePosition == -1)
            return;
        if (choosePosition == 0)
            chooseEndItem();
        else
            setAllChoose(choosePosition - 1);
    }

    public void chooseNextItem() {
        if (choosePosition == -1)
            return;
        if (choosePosition == isClicks.size() - 1)
            chooseFirstItem();
        else
            setAllChoose(choosePosition + 1);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView id;
        private TextView name;
        private TextView place;
        private TextView start;
        private TextView end;
        private TextView direction;
        private TextView sort;
        private TextView guancai;
        private TextView diameter;
        private TextView computer;
        private TextView people;
        private LinearLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.record_item_task_id);
            name = (TextView) itemView.findViewById(R.id.record_item_task_name);
            place = (TextView) itemView.findViewById(R.id.record_item_task_place);
            start = (TextView) itemView.findViewById(R.id.record_item_task_start);
            end = (TextView) itemView.findViewById(R.id.record_item_task_end);
            direction = (TextView) itemView.findViewById(R.id.record_item_task_direction);
            sort = (TextView) itemView.findViewById(R.id.record_item_task_sort);
            guancai = (TextView) itemView.findViewById(R.id.record_item_task_guancai);
            diameter = (TextView) itemView.findViewById(R.id.record_item_task_diameter);
            computer = (TextView) itemView.findViewById(R.id.record_item_task_computer);
            people = (TextView) itemView.findViewById(R.id.record_item_task_people);
            container = (LinearLayout) itemView.findViewById(R.id.record_item_container);

        }
    }


    public interface OnDataChooseListener {
        void setData(int id);
    }

    private OnDataChooseListener listener;

    public void setOnDataChooseListener(OnDataChooseListener listener) {
        this.listener = listener;
    }

}
